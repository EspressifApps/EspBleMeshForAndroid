package com.espressif.blemesh.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.espressif.blemesh.model.MeshScanResult;
import com.espressif.blemesh.utils.EspLog;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import no.nordicsemi.android.mesh.MeshManagerApi;
import no.nordicsemi.android.mesh.espressif.EspMeshConstants;

public class EspGattClient extends BluetoothGattCallback {
    public static final int MTU_MIN = 23;
    public static final int MTU_MAX = 69;

    private final EspLog mLog = new EspLog(getClass());

    private final Context mContext;

    private final ReentrantLock mWriteLock;
    private final Condition mWriteCondition;

    private final MeshScanResult mDevice;
    private final UUID mServiceUUID;
    private final UUID mWriteUUID;
    private final UUID mNotifyUUID;

    private volatile BluetoothGattCharacteristic mWriteChar;
    private volatile BluetoothGattCharacteristic mNotifyChar;

    private volatile BluetoothGatt mGatt;
    private volatile int mMtu;
    private final MeshManagerApi mMeshApi;

    private volatile ExecutorService mExecutor;
    private DeviceCallback mDeviceCallback;
    private final Handler mUiHandler;

    public interface DeviceCallback {
        void onDeviceConnected(MeshScanResult sr);

        void onDeviceDisconnected(MeshScanResult sr);

        void onDeviceReady(MeshScanResult sr);

        void onDeviceFailed(MeshScanResult sr);
    }

    public EspGattClient(@NonNull Context context, @NonNull MeshScanResult device, @NonNull UUID service,
                         @NonNull UUID writeChar, @NonNull UUID notifyChar, @NonNull MeshManagerApi managerApi) {
        mContext = context.getApplicationContext();
        mDevice = device;
        mServiceUUID = service;
        mWriteUUID = writeChar;
        mNotifyUUID = notifyChar;

        mWriteLock = new ReentrantLock(true);
        mWriteCondition = mWriteLock.newCondition();

        mMtu = MTU_MIN;

        mMeshApi = managerApi;
        mUiHandler = new Handler(Looper.getMainLooper());
    }

    public void setDeviceCallback(DeviceCallback callback) {
        mDeviceCallback = callback;
    }

    public int getAvailableMtu() {
        return mMtu - 3;
    }

    public String getDeviceAddress() {
        return mDevice.getAddress();
    }

    public synchronized void connect() {
        if (mGatt != null) {
            mLog.w("Gatt exists, call close() first");
            return;
        }
        mExecutor = Executors.newSingleThreadExecutor();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mGatt = mDevice.getDevice().connectGatt(mContext, false, this, BluetoothDevice.TRANSPORT_LE);
        } else {
            mGatt = mDevice.getDevice().connectGatt(mContext, false, this);
        }
    }

    public synchronized void close() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
        }
        mExecutor.shutdownNow();
    }

    public synchronized void write(byte[] value) {
        if (mExecutor.isShutdown()) {
            return;
        }

        int length = getAvailableMtu();
        List<byte[]> splits = new ArrayList<>(value.length / length + 1);
        ByteArrayInputStream dataIS = new ByteArrayInputStream(value);
        byte[] buf = new byte[length];
        while (true) {
            int read = dataIS.read(buf, 0, buf.length);
            if (read == -1) {
                break;
            }

            byte[] data = new byte[read];
            System.arraycopy(buf, 0, data, 0, read);
            splits.add(data);
        }
        mExecutor.submit(() -> {
            for (byte[] data : splits) {
                mWriteLock.lock();

                mWriteChar.setValue(data);
                mGatt.writeCharacteristic(mWriteChar);

                try {
                    mWriteCondition.await();
                } catch (InterruptedException e) {
                    mLog.w("write() InterruptedException");
                }
                mWriteLock.unlock();
            }
        });
    }

    private void onReady() {
        if (mDeviceCallback != null) {
            mUiHandler.post(() -> mDeviceCallback.onDeviceReady(mDevice));
        }
    }

    private void onFailed() {
        close();
        if (mDeviceCallback != null) {
            mUiHandler.post(() -> mDeviceCallback.onDeviceFailed(mDevice));
        }
    }

    private void onConnected() {
        if (mDeviceCallback != null) {
            mUiHandler.post(() -> mDeviceCallback.onDeviceConnected(mDevice));
        }
    }

    private void onDisconnected() {
        close();
        if (mDeviceCallback != null) {
            mUiHandler.post(() -> mDeviceCallback.onDeviceDisconnected(mDevice));
        }
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        mLog.d("onConnectionStateChange() " + "status: " + status + " , state: " + newState);
        mGatt = gatt;
        if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothGatt.STATE_CONNECTED) {
            onConnected();
            if (!gatt.requestMtu(MTU_MAX)) {
                onMtuChanged(gatt, MTU_MIN, BluetoothGatt.GATT_SUCCESS);
            }
        }
        if (status != BluetoothGatt.GATT_SUCCESS || newState == BluetoothGatt.STATE_DISCONNECTED) {
            close();
            onDisconnected();
        }
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        mLog.d("onMtuChanged() status: " + status + " , mtu: " + mtu);
        mMtu = mtu;
        if (status == BluetoothGatt.GATT_SUCCESS) {
            gatt.discoverServices();
        } else {
            onFailed();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        mLog.d("onServicesDiscovered() status: " + status);
        if (status != BluetoothGatt.GATT_SUCCESS) {
            onFailed();
            return;
        }

        BluetoothGattService service = gatt.getService(mServiceUUID);
        if (service == null) {
            mLog.w("onServicesDiscovered() getService failed: " + mServiceUUID);
            onFailed();
            return;
        }

        mWriteChar = service.getCharacteristic(mWriteUUID);
        if (mWriteChar == null) {
            mLog.w("onServicesDiscovered() get WriteChar failed: " + mWriteUUID);
            onFailed();
            return;
        }

        mNotifyChar = service.getCharacteristic(mNotifyUUID);
        if (mNotifyChar == null) {
            mLog.w("onServicesDiscovered() get NotifyChar failed: " + mNotifyUUID);
            onFailed();
            return;
        }
        gatt.setCharacteristicNotification(mNotifyChar, true);

        BluetoothGattDescriptor descriptor = mNotifyChar.getDescriptor(EspMeshConstants.UUID_DESC_NOTIFICATION);
        if (descriptor == null) {
            mLog.w("onServicesDiscovered() get NotifyDesc failed");
            onFailed();
            return;
        }

        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(descriptor);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        mLog.d("onDescriptorWrite() status: " + status);
        if (Objects.equals(EspMeshConstants.UUID_DESC_NOTIFICATION, descriptor.getUuid())) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                onReady();
            } else {
                onFailed();
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        mLog.d("onCharacteristicChanged() " + Arrays.toString(characteristic.getValue()));
        mMeshApi.handleNotifications(getAvailableMtu(), characteristic.getValue());
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        mLog.d("onCharacteristicWrite() status: " + status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            int availableMtu = getAvailableMtu();
            mMeshApi.handleWriteCallbacks(availableMtu, characteristic.getValue());
            mWriteLock.lock();
            mWriteCondition.signal();
            mWriteLock.unlock();
        } else {
            onFailed();
        }

    }
}

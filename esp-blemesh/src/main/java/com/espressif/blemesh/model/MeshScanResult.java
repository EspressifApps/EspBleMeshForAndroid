package com.espressif.blemesh.model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;

import no.nordicsemi.android.mesh.MeshBeacon;
import no.nordicsemi.android.mesh.MeshManagerApi;

public class MeshScanResult {
    private ScanResult mScanResult;
    private MeshBeacon mMeshBeacon;

    private String mUUID;

    public MeshScanResult(ScanResult scanResult) {
        setScanResult(scanResult);
    }

    public void setScanResult(ScanResult scanResult) {
        mScanResult = scanResult;
    }

    public ScanResult getScanResult() {
        return mScanResult;
    }

    public String getName() {
        return getDevice().getName();
    }

    public String getAddress() {
        return getDevice().getAddress();
    }

    public BluetoothDevice getDevice() {
        return mScanResult.getDevice();
    }

    public int getRssi() {
        return mScanResult.getRssi();
    }

    public ScanRecord getScanRecord() {
        return mScanResult.getScanRecord();
    }

    public void setNodeUUID(String uuid) {
        mUUID = uuid;
    }

    public String getUUID() {
        return mUUID;
    }

    public byte[] getRaw() {
        ScanRecord scanRecord = getScanRecord();
        return scanRecord == null ? null : scanRecord.getBytes();
    }

    public void updateMeshBeacon(MeshManagerApi meshManagerApi) {
        final ScanRecord scanRecord = getScanRecord();
        if (scanRecord != null) {
            byte[] scanBytes = scanRecord.getBytes();
            byte[] beaconData = meshManagerApi.getMeshBeaconData(scanBytes);
            if (beaconData != null) {
                mMeshBeacon = meshManagerApi.getMeshBeacon(beaconData);
            }
        }
    }

    public MeshBeacon getMeshBeacon() {
        return mMeshBeacon;
    }
}

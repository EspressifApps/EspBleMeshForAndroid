package com.espressif.blemesh.ota;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.espressif.blemesh.utils.EspLog;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import no.nordicsemi.android.mesh.espressif.EspCrc32;
import no.nordicsemi.android.mesh.espressif.EspDataUtils;
import no.nordicsemi.android.mesh.espressif.EspOtaUtils;

public class EspOtaClient implements IEspOtaClient {
    private static final Boolean DEBUG = true;

    private static final String DEVICE_HOST = "192.168.4.1";
    private static final int DEVICE_PORT = 8090;
    private static final int SO_TIMEOUT = 5000;

    private static final int PACKET_SECURITY_REQUEST = 0x00;
    private static final int PACKET_SECURITY_RESPONSE = 0x01;
    private static final int PACKET_BIN_INFO = 0x02;
    private static final int PACKET_BIN_INFO_RESPONSE = 0x03;
    private static final int PACKET_BIN_DATA = 0x04;
    private static final int PACKET_BIN_DATA_ACK = 0x05;

    private static final String AES_TRANSFORMATION = "AES/CFB/NoPadding";
    private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private final int mCompanyId;
    private final int mBinId;
    private final int mVersion;
    private final byte[] mNetKey;

    private volatile boolean mClosed = false;
    private final ExecutorService mListenerPools;

    public interface OtaListener {
        void onConnected();

        void onOtaProgress(float progress);

        void onOtaComplete();

        void onFailed(int status);
    }

    private volatile Socket mSocket;

    private OtaListener mListener;

    private EspLog mLog = new EspLog(getClass());

    private File mBin;

    public EspOtaClient(@NonNull File bin, int companyId, int binId, int version, byte[] netKey) {
        mBin = bin;
        mCompanyId = companyId;
        mBinId = binId;
        mVersion = version;
        mNetKey = netKey;

        mListenerPools = Executors.newSingleThreadExecutor();
    }

    public void setOtaListener(OtaListener listener) {
        mListener = listener;
    }

    @Override
    public boolean ota() {
        return __ota();
    }

    @Override
    public void close() throws IOException {
        mClosed = true;
        synchronized (this) {
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }

            mListenerPools.shutdownNow();
        }
    }

    private void notifyListener(Runnable runnable) {
        if (!mListenerPools.isShutdown()) {
            mListenerPools.execute(runnable);
        }
    }

    private int checksum(byte[] data) {
        return EspCrc32.crc32(0, data);
    }

    private RSAKey[] generateRSAKeys() {
        final String algorithm = "RSA";

        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            return new RSAKey[]{publicKey, privateKey};
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Cipher getRsaDecryptCipher(Key privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Cipher getAesEncrypt(byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Cipher getAesDecrypt(byte[] key, byte[] iv) {
        try {
            Cipher cipher = Cipher.getInstance(AES_TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            return cipher;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void disconnect() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private EspOtaPacket receivePacket(InputStream inputStream, Cipher decrypt) throws IOException {
        EspOtaPacket packet = new EspOtaPacket();

        packet.packetId = inputStream.read() & 0xff;
        if (DEBUG) {
            mLog.i("Receive packetId: " + packet.packetId);
        }

        int flag = inputStream.read() & 0xff;
        packet.flag = flag;
        if (DEBUG) {
            mLog.i("Receive flag: " + packet.flag);
        }

        packet.companyId = (inputStream.read() | (inputStream.read() << 8)) & 0xffff;
        if (DEBUG) {
            mLog.i("Receive companyId: " + Integer.toHexString(packet.companyId));
        }

        int dataLength = (inputStream.read() | (inputStream.read() << 8)) & 0xffff;
        if (DEBUG) {
            mLog.i("Receive dataLength: " + dataLength);
        }
        byte[] data = new byte[dataLength];
        int offset = 0;
        while (true) {
            int read = inputStream.read(data, offset, dataLength - offset);
            if (DEBUG) {
                mLog.i("Receive data length: " + read);
            }
            if (read == -1) {
                mLog.w("receivePacket() Receive AES IV error: read -1");
                return null;
            }
            offset += read;
            if (offset == dataLength) {
                mLog.d("receivePacket() Receive AES IV complete");
                break;
            }
        }

        if (EspOtaUtils.isSecurity(flag) && decrypt != null) {
            try {
                packet.data = decrypt.doFinal(data, 0, dataLength);
            } catch (BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
                mLog.w("receivePacket() Decrypt data error");
                return null;
            }
        } else {
            packet.data = data;
        }

        if (EspOtaUtils.isChecksum(flag)) {
            packet.checksum = (inputStream.read() & 0xff)
                    | ((inputStream.read() & 0xff) << 8)
                    | ((inputStream.read() & 0xff) << 16)
                    | ((inputStream.read() & 0xff) << 24);

            int dataChecksum = checksum(packet.data);
            if (dataChecksum != packet.checksum) {
                mLog.w("receivePacket() Checksum invalid");
                return null;
            }
        }

        return packet;
    }

    private interface Predicate {
        boolean test(EspOtaPacket packet);
    }

    private boolean checkPacket(EspOtaPacket packet, int expectId, @Nullable Predicate predicate) {
        if (packet == null) {
            mLog.w("Receive packet null, expectId=" + expectId);
            return false;
        }
        if (packet.packetId != expectId) {
            mLog.w("Receive invalid packetId: " + packet.packetId + " , expectId=" + expectId);
            return false;
        }
        if (predicate != null) {
            return predicate.test(packet);
        }

        return true;
    }

    private int getCompleteBinSequenceSize(byte[] sequenceBits, int totalSequence) {
        int count = 0;
        int sequence = 0;
        TASK:
        for (byte b : sequenceBits) {
            for (int i = 0; i < 8; ++i) {
                if ((b >> i & 1) == 1) {
                    ++count;
                }

                if (sequence >= totalSequence) {
                    break TASK;
                }
                ++sequence;
            }
        }

        return count;
    }

    private boolean __ota() {
        if (mClosed) {
            mLog.w("ota() The client is closed");
            return false;
        }
        if (!mBin.exists()) {
            if (mListener != null) {
                notifyListener(() -> mListener.onFailed(STATUS_BIN_NOT_FOUND));
            }

            return false;
        }
        synchronized (this) {
            if (mSocket == null || mSocket.isClosed()) {
                try {
                    mSocket = new Socket(DEVICE_HOST, DEVICE_PORT);
                    mSocket.setSendBufferSize(1040);
                    mSocket.setSoTimeout(SO_TIMEOUT);
                    if (mListener != null) {
                        notifyListener(() -> mListener.onConnected());
                    }
                    mLog.d("ota() create socket success");
                } catch (IOException e) {
                    mLog.w("ota() create socket failed");
                    if (mListener != null) {
                        notifyListener(() -> mListener.onFailed(STATUS_CONNECT_FAILED));
                    }
                    return false;
                }
            }
        }

        RSAKey[] keys = generateRSAKeys();
        assert keys != null;
        RSAPublicKey publicKey = (RSAPublicKey) keys[0];
        byte[] publicKeyEncoded = publicKey.getEncoded();
        RSAPrivateKey privateKey = (RSAPrivateKey) keys[1];
        Cipher rsaDecrypt = getRsaDecryptCipher(privateKey);

        try (final FileInputStream fis = new FileInputStream(mBin);
             final Socket socket = mSocket;
             final OutputStream socketOS = socket.getOutputStream();
             final InputStream socketIS = socket.getInputStream()) {
            int flag;
            byte[] packet;
            byte[] checksum;
            EspOtaPacket recvPacket;

            // Send security request and RSA PublicKey data
            flag = EspOtaUtils.getFlag(false, false);
            packet = EspOtaUtils.getPacketBytes(PACKET_SECURITY_REQUEST, flag, mCompanyId,
                    publicKeyEncoded, null, null);
            mLog.i("ota() security_request length: " + packet.length);
            mLog.i("ota() security_request packet: " + Arrays.toString(packet));
            socketOS.write(packet);
            mLog.d("ota() send PACKET_SECURITY_REQUEST");

            // Receive AES IV data
            recvPacket = receivePacket(socketIS, rsaDecrypt);
            boolean packetValid = checkPacket(recvPacket, PACKET_SECURITY_RESPONSE, p -> {
                mLog.d("Receive AES IV data length: " + p.data.length);
                return p.data.length >= 10;
            });
            if (!packetValid) {
                mLog.w("Receive AES IV failed");
                if (mListener != null) {
                    notifyListener(() -> mListener.onFailed(STATUS_AES_IV_ERROR));
                }
                return false;
            }

            byte[] aesIV = new byte[16];
            aesIV[0] = (byte) (mCompanyId & 0xff);
            aesIV[1] = (byte) (mCompanyId >> 8 & 0xff);
            aesIV[2] = (byte) (mBinId & 0xff);
            aesIV[3] = (byte) (mBinId >> 8 & 0xff);
            aesIV[4] = (byte) (mVersion & 0xff);
            aesIV[5] = (byte) (mVersion >> 8 & 0xff);
            assert recvPacket != null;
            System.arraycopy(recvPacket.data, 0, aesIV, 6, 10);

            Cipher aesEncrypt = getAesEncrypt(mNetKey, aesIV);
            Cipher aesDecrypt = getAesDecrypt(mNetKey, aesIV);
            if (aesEncrypt == null || aesDecrypt == null) {
                mLog.w("Generate AES Cipher failed");
                if (mListener != null) {
                    notifyListener(() -> mListener.onFailed(STATUS_AES_KEY_ERROR));
                }
                return false;
            }

            // Send bin file info
            final int binLength = (int) mBin.length();
            byte[] binData = new byte[binLength];
            MessageDigest md5 = MessageDigest.getInstance("md5");
            int read = fis.read(binData);
            md5.update(binData, 0, read);

            byte[] binLengthData = EspOtaUtils.int2Bytes(binLength);
            byte[] md5Data = md5.digest();
            byte[] binInfo = EspDataUtils.mergeBytes(binLengthData, md5Data);
            flag = EspOtaUtils.getFlag(true, true);
            checksum = EspOtaUtils.int2Bytes(EspCrc32.crc32(0, binInfo));
            packet = EspOtaUtils.getPacketBytes(PACKET_BIN_INFO, flag, mCompanyId, binInfo, checksum, aesEncrypt);
            socketOS.write(packet);

            recvPacket = receivePacket(socketIS, aesDecrypt);
            packetValid = checkPacket(recvPacket, PACKET_BIN_INFO_RESPONSE, p -> p.data.length > 4);
            if (packetValid) {
                mLog.w("Receive Bin info response failed");
                if (mListener != null) {
                    notifyListener(() -> mListener.onFailed(STATUS_BIN_INFO_ERROR));
                }
                return false;
            }
            final ByteArrayInputStream binIS = new ByteArrayInputStream(binData);
            assert recvPacket != null;
            final int binSegmentLength = (recvPacket.data[0]) & 0xff | ((recvPacket.data[1] & 0xff) << 8);
            final int totalSequence = (recvPacket.data[2]) & 0xff | ((recvPacket.data[3] & 0xff) << 8);
            final int totalSequenceCount = totalSequence + 1;
            final List<byte[]> binDataList = new ArrayList<>(totalSequenceCount);
            byte[] sequenceBits = EspDataUtils.subBytes(recvPacket.data, 4);
            for (int i = 0; i <= totalSequence; ++i) {
                byte[] data = new byte[binSegmentLength + 4];
                data[0] = (byte) (i & 0xff);
                data[1] = (byte) (i >> 8 & 0xff);
                data[2] = recvPacket.data[2];
                data[3] = recvPacket.data[3];
                read = binIS.read(data, 4, binSegmentLength);
                if (read == -1) {
                    break;
                }
                if (read < binSegmentLength) {
                    data = Arrays.copyOf(data, read + 4);
                }
                binDataList.add(data);
            }


            int sentCount = getCompleteBinSequenceSize(sequenceBits, totalSequence);
            mLog.i("Bin Info resp sent count = " + sentCount + " , total count = " + totalSequence);
            if (mListener != null) {
                final float progress = (float)sentCount / (float)totalSequenceCount;
                notifyListener(() -> mListener.onOtaProgress(progress));
            }

            // Send bin data
            int sequence = 0;
            SendTask:
            for (byte b : sequenceBits) {
                for (int i = 0; i < 8; ++i) {
                    byte[] recvBits = sequenceBits;
                    boolean sent = (b >> i & 1) == 1;
                    if (!sent) {
                        byte[] data = binDataList.get(sequence);
                        recvBits = sendBinData(socket, data, aesEncrypt, aesDecrypt);
                        if (recvBits == null) {
                            mLog.w("Send/Recv Bin Data failed");
                            if (mListener != null) {
                                notifyListener(() -> mListener.onFailed(STATUS_BIN_ACK_ERROR));
                            }
                            return false;
                        }
                        if (recvBits.length != sequenceBits.length) {
                            mLog.w("Receive Bits length invalid: " + recvBits.length + " , expect: "
                                    + sequenceBits.length);
                            if (mListener != null) {
                                notifyListener(() -> mListener.onFailed(STATUS_BIN_ACK_INVALID));
                            }
                            return false;
                        }
                        ++sentCount;

                        if (mListener != null) {
                            if (sentCount % 64 == 0) {
                                final float progress = (float)sentCount / (float)totalSequenceCount;
                                notifyListener(() -> mListener.onOtaProgress(progress));
                            }
                        }
                    }

                    if (sequence >= totalSequence) {
                        sequenceBits = recvBits;
                        break SendTask;
                    }
                    ++sequence;
                }
            }

            sentCount = getCompleteBinSequenceSize(sequenceBits, totalSequence);
            boolean complete = sentCount == totalSequenceCount;
            if (mListener != null) {
                final float lastProgress = (float)sentCount / (float)totalSequenceCount;
                notifyListener(() -> {
                    if (complete) {
                        mListener.onOtaComplete();
                    } else {
                        mListener.onOtaProgress(lastProgress);
                        mListener.onFailed(STATUS_BIN_INCOMPLETE);
                    }
                });
            }
            return complete;
        } catch (Exception e) {
            e.printStackTrace();
            if (mListener != null) {
                notifyListener(() -> mListener.onFailed(STATUS_EXCEPTION));
            }
            return false;
        }
    }

    private byte[] sendBinData(Socket socket, byte[] binData, Cipher encrypt, Cipher decrypt)
            throws IOException, BadPaddingException, IllegalBlockSizeException {
        OutputStream socketOS = socket.getOutputStream();
        InputStream socketIS = socket.getInputStream();

        int flag = EspOtaUtils.getFlag(true, true);
        byte[] checksum = EspOtaUtils.int2Bytes(EspCrc32.crc32(0, binData));
        byte[] packet = EspOtaUtils.getPacketBytes(PACKET_BIN_DATA, flag, mCompanyId, binData,
                checksum, encrypt);
        socketOS.write(packet);

        EspOtaPacket recvPacket = receivePacket(socketIS, decrypt);
        boolean valid = checkPacket(recvPacket, PACKET_BIN_DATA_ACK, p -> {
            boolean v = p.data[0] == binData[0] && p.data[1] == binData[1]
                    && p.data[2] == binData[2] && p.data[3] == binData[3];
            if (!v) {
                mLog.w(String.format(Locale.ENGLISH,
                        "Receive Bin Ack invalid sequence: 0x%02X%02X%02X%02X, expect sequence=0x%02X%02X%02X%02X",
                        p.data[3], p.data[2], p.data[1], p.data[0],
                        binData[3], binData[2], binData[1], binData[0]));
            }
            return v;
        });
        if (!valid) {
            mLog.w("Receive Bin Ack failed");
            return null;
        }

        assert recvPacket != null;
        return EspDataUtils.subBytes(recvPacket.data, 4);
    }
}

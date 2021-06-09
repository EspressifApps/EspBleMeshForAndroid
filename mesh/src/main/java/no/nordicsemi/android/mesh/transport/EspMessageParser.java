package no.nordicsemi.android.mesh.transport;

import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import no.nordicsemi.android.mesh.MeshStatusCallbacks;
import no.nordicsemi.android.mesh.NetworkKey;
import no.nordicsemi.android.mesh.espressif.EspDataUtils;
import no.nordicsemi.android.mesh.opcodes.EspMessageOpCodes;
import no.nordicsemi.android.mesh.utils.MeshParserUtils;
import no.nordicsemi.android.mesh.utils.SecureUtils;

import static no.nordicsemi.android.mesh.transport.NetworkLayer.createNetworkNonce;
import static no.nordicsemi.android.mesh.transport.NetworkLayer.deObfuscateNetworkHeader;

class EspMessageParser {
    private final static String TAG = "EspMessageParser";

    static boolean parseEspMessage(final int opCodeLength, final AccessMessage message, final MeshStatusCallbacks callbacks) {
        final byte[] accessPayload = message.getAccessPdu();
        int opCode = 0;
        for (int index = opCodeLength - 1, offset = 0; index >= 0; --index, ++offset) {
            opCode |= (accessPayload[index] & 0xff) << (offset * 8);
        }
        if (opCode == EspMessageOpCodes.FAST_PROV_INFO_STATUS) {
            Log.i(TAG, "ESP parseEspMessage: FAST_PROV_STATUS");
            final EspFastProvInfoStatus status = new EspFastProvInfoStatus(message);
            callbacks.onMeshMessageReceived(status.getSrc(), status);
            return true;
        } else if (opCode == EspMessageOpCodes.FAST_PROV_NODE_ADDR_STATUS) {
            Log.i(TAG, "ESP parseEspMessage: FAST_PROV_NODE_ADDR_STATUS");
            final EspFastProvNodeAddrStatus status = new EspFastProvNodeAddrStatus(message);
            callbacks.onMeshMessageReceived(status.getSrc(), status);
            return true;
        } else if ((opCode >> 16 & 0xff) == EspMessageOpCodes.OTA_NOUN_FB) {
            Log.i(TAG, "ESP parseEspMessage: OTA_NOUN");
            EspOtaNeedUpdateNotification notification = new EspOtaNeedUpdateNotification(message);
            callbacks.onMeshMessageReceived(notification.getSrc(), notification);
            return true;
        } else if ((opCode >> 16 & 0xff) == EspMessageOpCodes.OTA_START_RESPONSE_FB) {
            Log.i(TAG, "ESP parseEspMessage: OTA_START_RESPONSE");
            EspOtaStartResponse response = new EspOtaStartResponse(message, opCode);
            callbacks.onMeshMessageReceived(response.getSrc(), response);
            return true;
        } else if (opCode == EspMessageOpCodes.CURRENT_VERSION_STATUS) {
            Log.i(TAG, "ESP parseEspMessage: CURRENT_VERSION_STATUS");
            EspCurrentVersionStatus status = new EspCurrentVersionStatus(message);
            callbacks.onMeshMessageReceived(status.getSrc(), status);
            return true;
        } else {
            Log.i(TAG, "parseEspMessage: Not ESP message");
        }

        return false;
    }

    static class EspMeshPduDecrypted {
        byte[] networkHeader;
        byte[] decryptedPayload;
        byte[] sequenceNumber;
        int ivIndex;
    }

    static EspMeshPduDecrypted retryMeshPduDecrypt(NetworkKey networkKey, byte[]pdu) {
        final SecureUtils.K2Output k2Output = SecureUtils.calculateK2(networkKey.getKey(), SecureUtils.K2_MASTER_INPUT);

        final int retryCount = 10;
        for (int ivIndex = 0; ivIndex < retryCount; ++ivIndex) {
            Log.i(TAG, "retryMeshPduDecrypt: ivIndex = " + ivIndex);
            final byte[] networkHeader = deObfuscateNetworkHeader(pdu, MeshParserUtils.intToBytes(ivIndex), k2Output.getPrivacyKey());
            final int ctlTtl = networkHeader[0];
            final int ctl = (ctlTtl >> 7) & 0x01;
            final int ttl = ctlTtl & 0x7F;
            final int src = MeshParserUtils.unsignedBytesToInt(networkHeader[5], networkHeader[4]);

            final byte[] sequenceNumber = ByteBuffer.allocate(3).order(ByteOrder.BIG_ENDIAN).put(networkHeader, 1, 3).array();
            final int networkPayloadLength = pdu.length - (2 + networkHeader.length);
            final byte[] transportPdu = new byte[networkPayloadLength];
            System.arraycopy(pdu, 8, transportPdu, 0, networkPayloadLength);
            byte[] nonce = createNetworkNonce((byte) ctlTtl, sequenceNumber, src, MeshParserUtils.intToBytes(ivIndex));
            try {
                EspMeshPduDecrypted decrypted = new EspMeshPduDecrypted();
                decrypted.decryptedPayload = SecureUtils.decryptCCM(transportPdu, k2Output.getEncryptionKey(), nonce, SecureUtils.getNetMicLength(ctl));
                decrypted.networkHeader = networkHeader;
                decrypted.sequenceNumber = sequenceNumber;
                decrypted.ivIndex = ivIndex;
                Log.i(TAG, "retryMeshPduDecrypt: result: ivIndex=" + ivIndex + ", sequenceNumber=" + Arrays.toString(sequenceNumber));
                Log.i(TAG, "retryMeshPduDecrypt: result network payload = " + EspDataUtils.bigEndianBytesToHexString(decrypted.decryptedPayload));
                return decrypted;
            } catch (Exception e) {
                Log.w(TAG, "retryMeshPduDecrypt: Exception" + e.getMessage());
            }
        }
        return null;
    }
}

package no.nordicsemi.android.mesh.espressif;

import no.nordicsemi.android.mesh.utils.SecureUtils;

public class EspMeshUtils {
    public static byte[] getProvisioningUUID(byte[] scanRecord) {
        if (!isMeshProvisioning(scanRecord)) {
            return null;
        }
        return EspDataUtils.subBytes(scanRecord, 11, 16);
    }

    public static byte[][] getNodeHashAndRandom(byte[] scanRecord) {
        if (!isMeshNodeIdentity(scanRecord)) {
            return null;
        }
        byte[][] result = new byte[2][];
        result[0] = EspDataUtils.subBytes(scanRecord, 12, 8);
        result[1] = EspDataUtils.subBytes(scanRecord, 20, 8);
        return result;
    }

    public static boolean isMeshProvisioning(byte[] scanRecord) {
        return scanRecord.length >= 29
                && scanRecord[0] == 0x02
                && scanRecord[1] == 0x01
                && scanRecord[3] == 0x03
                && scanRecord[4] == 0x03
                && scanRecord[7] == 0x15
                && scanRecord[8] == 0x16;
    }

    public static boolean isMeshNodeIdentity(byte[] scanRecord) {
        return scanRecord.length >= 29
                && scanRecord[0] == 0x02
                && scanRecord[1] == 0x01
                && scanRecord[3] == 0x03
                && scanRecord[4] == 0x03
                && scanRecord[7] == 0x14
                && scanRecord[8] == 0x16
                && scanRecord[11] == 0x01;
    }

    public static boolean isNetworkID(byte[] scanRecord) {
        return scanRecord.length >= 20
                && scanRecord[0] == 0x02
                && scanRecord[1] == 0x01
                && scanRecord[3] == 0x03
                && scanRecord[4] == 0x03
                && scanRecord[7] == 0x0c
                && scanRecord[8] == 0x16
                && scanRecord[11] == 0x00;
    }

    public static byte[] getData(int proxySar, int proxyType, byte[]... datas) {
        int proxy = (proxySar << 6) | proxyType;
        byte[] head = new byte[]{(byte) proxy};
        return EspDataUtils.mergeBytes(head, datas);
    }

    public static int[] getProxySarAndType(byte proxy) {
        int[] result = new int[2];
        int proxyInt = proxy & 0xff;
        result[0] = proxyInt >> 6;
        result[1] = proxyInt & 63;
        return result;
    }

    /**
     * @param index1 key index
     * @param index2 key index
     * @return Little endian data
     */
    public static byte[] getIndexBytesWithTwoIndexes(long index1, long index2) {
        byte[] result = new byte[3];

        result[0] = (byte) (index1 & 0xff);
        result[1] = (byte) (((index1 >> 8) & 0xf) | ((index2 & 0xf) << 4));
        result[2] = (byte) ((index2 >> 4) & 0xff);

        return result;
    }

    /**
     * @param index key index
     * @return Little endian data
     */
    public static byte[] getIndexBytesWithOneIndex(long index) {
        byte[] result = new byte[2];

        result[0] = (byte) (index & 0xff);
        result[1] = (byte) ((index >> 8) & 0xf);
        return result;
    }

    /**
     * @param indexBytes Little endian data
     * @return two indexes array
     */
    public static long[] getIndexesWithThreeBytes(byte[] indexBytes) {
        long byte0 = indexBytes[0] & 0xff;
        long byte1 = indexBytes[1] & 0xff;
        long byte2 = indexBytes[2] & 0xff;

        long index1 = byte0 | ((byte1 & 0xf) << 8);
        long index2 = (byte1 >> 4) | (byte2 << 4);
        return new long[]{index1, index2};
    }

    /**
     * @param indexBytes Little endian data
     * @return index
     */
    public static long getIndexWithTwoBytes(byte[] indexBytes) {
        long byte0 = indexBytes[0] & 0xff;
        long byte1 = indexBytes[1] & 0xff;

        return byte0 | (byte1 << 8);
    }

    public static byte[] getSequenceBytes(long seq) {
        byte[] bytes = new byte[3];
        bytes[0] = (byte) (seq >> 16 & 0xff);
        bytes[1] = (byte) (seq >> 8 & 0xff);
        bytes[2] = (byte) (seq & 0xff);
        return bytes;
    }

    public static long getSeqZero(long seq) {
        return seq & 0x1fff;
    }

    public static byte[] getAID(byte[] appKey) {
        return new byte[]{SecureUtils.calculateK4(appKey)};
    }

    public static byte[] addressLong2BigEndianBytes(long address) {
        return long2BigEndianBytes(address, 2);
    }

    public static byte[] addressLong2LittleEndianBytes(long address) {
        return long2LittleEndianBytes(address, 2);
    }

    /**
     * @return Big endian
     */
    public static byte[] long2BigEndianBytes(long value, int bytesLength) {
        byte[] result = new byte[bytesLength];
        int offset = 0;
        for (int i = bytesLength - 1; i >= 0; i--) {
            result[i] = (byte) ((value >> (8 * offset)) & 0xff);
            offset++;
        }
        return result;
    }

    public static byte[] long2LittleEndianBytes(long value, int byteSize) {
        byte[] result = new byte[byteSize];
        for (int i = 0; i < byteSize; i++) {
            result[i] = (byte) ((value >> (8 * i)) & 0xff);
        }
        return result;
    }

    public static long bigEndianBytes2Long(byte[] bytes) {
        long result = 0L;
        for (int index = bytes.length - 1; index >= 0L; index--) {
            long l = bytes[index] & 0xffL;
            int offset = (bytes.length - 1 - index) * 8;

            result |= (l << offset);
        }
        return result;
    }

    public static long littleEndianBytes2Long(byte[] bytes) {
        long result = 0L;
        for (int i = 0; i < bytes.length; i++) {
            long l = bytes[i] & 0xffL;
            result |= (l << (8 * i));
        }
        return result;
    }

    public static boolean isGroupAddress(long address) {
        return address >= EspMeshConstants.ADDRESS_GROUP_MIN && address <= EspMeshConstants.ADDRESS_GROUP_MAX;
    }

    public static byte[] calcNodeHash(byte[] identityKey, byte[] random, long address) {
        byte[] padding = new byte[6];
        byte[] addrBytes = addressLong2BigEndianBytes(address);
        byte[] nonce = EspDataUtils.mergeBytes(padding, random, addrBytes);
//        byte[] e = MeshAlgorithmUtils.e(identityKey, nonce);
        byte[] e = SecureUtils.encryptWithAES(nonce, identityKey);
        return EspDataUtils.subBytes(e, 8, 8);

//        BigInteger eBI = new BigInteger(e);
//        BigInteger bi2 = new BigInteger(new byte[]{1, 0, 0, 0, 0, 0, 0, 0, 0});
//        BigInteger mod = eBI.mod(bi2);
//        return DataUtil.hexStringToBigEndianBytes(mod.toString(16));
    }

    public static int double2FFFF(double d) {
        return (int) (d * 0xffff);
    }

    public static double ffff2double(int i) {
        return i / 65535.0;
    }

    /**
     * @param hsl: h: [0 .. 1), s: [0...1], l: [0...1]
     */
    public static int[] HSL2MeshHSL(double[] hsl) {
        int lightHue = (int) (hsl[0] * 65535.0);
        int lightSaturation = (int) (hsl[1] * 65535.0);
        int lightLightness = (int) (hsl[2] * 65535.0);
        return new int[]{lightHue, lightSaturation, lightLightness};
    }

    public static double[] meshHSL2HSL(int[] lightHSL) {
        double h = lightHSL[0] / 65535.0;
        double s = lightHSL[1] / 65535.0;
        double l = lightHSL[2] / 65535.0;
        return new double[]{h, s, l};
    }
}

package no.nordicsemi.android.mesh.espressif;

import android.annotation.SuppressLint;

import androidx.annotation.IntRange;

import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import no.nordicsemi.android.mesh.ApplicationKey;
import no.nordicsemi.android.mesh.NetworkKey;
import no.nordicsemi.android.mesh.utils.SecureUtils;

public class EspOtaUtils {
    private static ApplicationKey sOtaAppKey;

    public static ApplicationKey getOtaAppKey() {
        if (sOtaAppKey == null) {
            int keyIndex = 0xF00; // 0xFFF - binId;
            byte[] aesKey = {
                    0x02, (byte) 0xE5, (byte) 0xE5, 0x02,
                    0x02, (byte) 0xE5, (byte) 0xE5, 0x02,
                    0x02, (byte) 0xE5, (byte) 0xE5, 0x02,
                    0x02, (byte) 0xE5, (byte) 0x01, 0x00
            };
            sOtaAppKey = new ApplicationKey(keyIndex, aesKey);
        }
        return sOtaAppKey;
    }

    private static byte[] aesEcbEncrypt(byte[] aesKey, byte[] data) {
        try {
            @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String binVersionInt2String(int version) {
        return (version & 0xff) + "." + (version >> 12 & 0xf) + "." + (version >> 8 & 0xf);
    }

    public static int binVersionString2Int(String version) {
        String[] splits = version.split("\\.");
        int primary = Integer.parseInt(splits[0]) & 0xff;
        int sub1 = Integer.parseInt(splits[1]) & 0xf;
        int sub2 = Integer.parseInt(splits[2]) & 0xf;
        return primary | (sub1 << 12) | (sub2 << 8);
    }

    public static String getOtaSoftApSSID(byte[] ssid, int nodeAddress) {
        StringBuilder sb = new StringBuilder();
        for (byte b : ssid) {
            sb.append(otaCharEncode(b & 0xff));
        }
        sb.append(otaCharEncode(nodeAddress & 0xff));
        sb.append(otaCharEncode(nodeAddress >> 8 & 0xff));
        return sb.toString();
    }

    public static String getOtaSoftApPassword(byte[] password, byte[] ssid, int nodeAddress) {
        StringBuilder sb = new StringBuilder();
        for (byte b : password) {
            sb.append(otaCharEncode(b & 0xff));
        }
        for (byte b : ssid) {
            sb.append(otaCharEncode(b & 0xff));
        }
        sb.append(otaCharEncode(nodeAddress & 0xff));
        sb.append(otaCharEncode(nodeAddress >> 8 & 0xff));
        for (byte b : password) {
            sb.append(otaCharEncode(b & 0xff));
        }
        return sb.toString();
    }

    private static char otaCharEncode(@IntRange(from = 0, to = 255) int uint8) {
        if (uint8 >= 0x21 && uint8 <= 0x7E) {
            return (char) uint8;
        } else {
            return (char) (uint8 % 26 + 0x61);
        }
    }

    public static int getFlag(boolean security, boolean checksum) {
        return (security ? 1 : 0)
                | (checksum ? 0b10 : 0);
    }

    public static boolean isSecurity(int flag) {
        return (flag & 1) == 1;
    }

    public static boolean isChecksum(int flag) {
        return (flag & 0b10) == 0b10;
    }

    public static byte[] int2Bytes(int i) {
        return new byte[]{
                (byte) (i & 0xff),
                (byte) (i >> 8 & 0xff),
                (byte) (i >> 16 & 0xff),
                (byte) (i >> 24 & 0xff)
        };
    }

    public static int bytes2Int(byte[] bytes) {
        return (bytes[0] & 0xff) |
                ((bytes[1] & 0xff) << 8) |
                ((bytes[2] & 0xff) << 16) |
                ((bytes[3] & 0xff) << 24);
    }

    public static byte[] getPacketBytes(int id, int flag, int companyId, byte[] data, byte[] checksum, Cipher cipher)
            throws BadPaddingException, IllegalBlockSizeException {
        if (EspOtaUtils.isSecurity(flag) && cipher == null) {
            throw new IllegalArgumentException("Flag contains security and cipher is null");
        }
        if (EspOtaUtils.isChecksum(flag) && checksum == null) {
            throw new IllegalArgumentException("Flag contains checksum and checksum is null");
        }

        final int checksumLen = checksum == null ? 0 : checksum.length;
        final ByteArrayOutputStream buf = new ByteArrayOutputStream(6 + data.length + checksumLen);
        if (EspOtaUtils.isSecurity(flag) && cipher != null) {
            data = cipher.doFinal(data);
        }

        buf.reset();
        buf.write(id);
        buf.write(flag);
        buf.write(companyId & 0xff);
        buf.write(companyId >> 8 & 0xff);
        buf.write(data.length & 0xff);
        buf.write(data.length >> 8 & 0xff);
        buf.write(data, 0, data.length);
        if (checksum != null) {
            buf.write(checksum, 0, checksum.length);
        }
        return buf.toByteArray();
    }

    public static int getCompanyId(int opCode) {
        return ((opCode & 0xff) << 8) | ((opCode >> 8) & 0xff);
    }
}

package no.nordicsemi.android.mesh.opcodes;

public class EspMessageOpCodes {
    public static final int FAST_PROV_INFO_SET = 0xC0E502;
    public static final int FAST_PROV_INFO_STATUS = 0xC1E502;
    public static final int FAST_PROV_NODE_ADDR_GET = 0xC6E502;
    public static final int FAST_PROV_NODE_ADDR_STATUS = 0xC7E502;

    // FB = First Byte
    public static final int OTA_NBVN_FB = 0xCC;
    public static final int OTA_NOUN_FB = 0xCD;
    public static final int OTA_START_FB = 0xCE;
    public static final int OTA_START_RESPONSE_FB = 0xCF;

    public static final int CURRENT_VERSION_GET = 0xD0E502;
    public static final int CURRENT_VERSION_STATUS = 0xD1E502;
}

package no.nordicsemi.android.mesh.utils;

/**
 * Address types
 */
public enum AddressType {

    UNASSIGNED_ADDRESS(0),
    UNICAST_ADDRESS(1),
    GROUP_ADDRESS(2),
    ALL_PROXIES(3),
    ALL_FRIENDS(4),
    ALL_RELAYS(5),
    ALL_NODES(6),
    VIRTUAL_ADDRESS(7);

    private final int type;

    /**
     * Constructs address type
     *
     * @param type Address type
     */
    AddressType(final int type) {
        this.type = type;
    }

    /**
     * Returns the address type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the oob method used for authentication
     *
     * @param method auth method used
     */
    public static AddressType fromValue(final int method) {
        switch (method) {
            default:
            case 0:
                return UNASSIGNED_ADDRESS;
            case 1:
                return UNICAST_ADDRESS;
            case 2:
                return GROUP_ADDRESS;
            case 3:
                return ALL_PROXIES;
            case 4:
                return ALL_FRIENDS;
            case 5:
                return ALL_RELAYS;
            case 6:
                return ALL_NODES;
            case 7:
                return VIRTUAL_ADDRESS;
        }
    }

    /**
     * Returns the address type name
     *
     * @param type Address type
     */
    public static String getTypeName(final AddressType type) {
        switch (type) {
            default:
            case UNASSIGNED_ADDRESS:
                return "Unassigned Address";
            case UNICAST_ADDRESS:
                return "Unicast Address";
            case GROUP_ADDRESS:
                return "Group Address";
            case ALL_PROXIES:
                return "All Proxies";
            case ALL_FRIENDS:
                return "All Friends";
            case ALL_RELAYS:
                return "All Relays";
            case ALL_NODES:
                return "All Nodes";
            case VIRTUAL_ADDRESS:
                return "Virtual Address";
        }
    }
}

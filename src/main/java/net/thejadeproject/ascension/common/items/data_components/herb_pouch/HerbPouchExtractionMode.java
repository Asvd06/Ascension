package net.thejadeproject.ascension.common.items.data_components.herb_pouch;

public enum HerbPouchExtractionMode {
    FIRST_ONE(0),
    LAST_ONE(1),
    ALL_FROM_AGE_GROUP(2),
    ALL_FROM_HERB(3);

    private final int networkId;

    HerbPouchExtractionMode(int networkId) {
        this.networkId = networkId;
    }

    public int networkId() {
        return networkId;
    }

    public static HerbPouchExtractionMode fromNetworkId(int networkId) {
        for (HerbPouchExtractionMode mode : values()) {
            if (mode.networkId == networkId) {
                return mode;
            }
        }

        return FIRST_ONE;
    }
}
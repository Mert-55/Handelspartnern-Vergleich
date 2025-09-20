package org.iu.handelspartnern.common.entity;

public enum PartnerType {
    SUPPLIER("Lieferant"), CUSTOMER("Kunde"), PARTNER("Partner");

    private final String displayName;

    PartnerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
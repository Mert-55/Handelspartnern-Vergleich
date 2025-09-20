package org.iu.handelspartnern.common.entity;

public enum PartnerStatus {
    ACTIVE("Aktiv"), PENDING_APPROVAL("Wartend"), INACTIVE("Inaktiv"), SUSPENDED("Gesperrt");

    private final String displayName;

    PartnerStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
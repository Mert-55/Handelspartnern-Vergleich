package org.iu.handelspartnern.common.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class FinancialEntry {

    private UUID id;
    private FinancialEntryType type;
    private FinancialEntryStatus status;
    private BigDecimal amount;
    private String purpose;
    private String reference;
    private LocalDate date;
    private LocalDateTime createdAt;

    public FinancialEntry() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.status = FinancialEntryStatus.OPEN;
        this.type = FinancialEntryType.CLAIM;
        this.amount = BigDecimal.ZERO;
        this.date = LocalDate.now();
    }

    public FinancialEntry(FinancialEntryType type, FinancialEntryStatus status, BigDecimal amount, String purpose,
            String reference, LocalDate date) {
        this();
        this.type = type;
        this.status = status;
        this.amount = amount;
        this.purpose = purpose;
        this.reference = reference;
        this.date = date != null ? date : LocalDate.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public FinancialEntryType getType() {
        return type;
    }

    public void setType(FinancialEntryType type) {
        this.type = type;
    }

    public FinancialEntryStatus getStatus() {
        return status;
    }

    public void setStatus(FinancialEntryStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSettled() {
        return FinancialEntryStatus.SETTLED.equals(status);
    }

    public BigDecimal getSignedAmount() {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return FinancialEntryType.CLAIM.equals(type) ? amount : amount.negate();
    }

    public String getDateFormatted() {
        return date != null ? date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "Unbekannt";
    }

    public String getStatusLabel() {
        return isSettled() ? "Beglichen" : "Offen";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FinancialEntry that = (FinancialEntry) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

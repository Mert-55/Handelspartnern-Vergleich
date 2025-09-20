package org.iu.handelspartnern.common.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import org.iu.handelspartnern.common.entity.converter.AddressConverter;
import org.iu.handelspartnern.common.entity.converter.ContactConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "trading_partners")
public class TradingPartner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2048)
    private String corporateImageUrl;

    @Column(nullable = false)
    private String name = "Unnamed Partner";

    @Column(nullable = false)
    private String taxId = "DE000000000";

    @Column(nullable = false)
    private String paymentTerms;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private LocalDateTime dateModified;

    @Column(nullable = false, columnDefinition = "NUMERIC(19,2)")
    private BigDecimal claims;

    @Column(nullable = false, columnDefinition = "NUMERIC(19,2)")
    private BigDecimal payable;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartnerStatus status;

    @Column(columnDefinition = "TEXT")
    private String about;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = AddressConverter.class)
    private List<Address> addresses;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Convert(converter = ContactConverter.class)
    private List<Contact> contacts;

    // Default Constructor
    public TradingPartner() {
        this.name = "Unbenannt";
        this.paymentTerms = "Net 30";
        this.status = PartnerStatus.ACTIVE;
        this.type = PartnerType.SUPPLIER;
        this.addresses = Collections.emptyList();
        this.contacts = Collections.emptyList();
        this.claims = BigDecimal.ZERO;
        this.payable = BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.dateCreated = now;
        this.dateModified = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModified = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCorporateImageUrl() {
        return corporateImageUrl != null ? corporateImageUrl : "https://picsum.photos/60/60?random=" + id;
    }

    public void setCorporateImageUrl(String corporateImageUrl) {
        this.corporateImageUrl = corporateImageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateModified() {
        return dateModified;
    }

    public BigDecimal getClaims() {
        return claims;
    }

    public void setClaims(BigDecimal claims) {
        this.claims = claims;
    }

    public BigDecimal getPayable() {
        return payable;
    }

    public void setPayable(BigDecimal payable) {
        this.payable = payable;
    }

    public PartnerType getType() {
        return type;
    }

    public void setType(PartnerType type) {
        this.type = type;
    }

    public PartnerStatus getStatus() {
        return status;
    }

    public void setStatus(PartnerStatus status) {
        this.status = status;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<Address> getAddresses() {
        return addresses != null ? addresses : Collections.emptyList();
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Contact> getContacts() {
        return contacts != null ? contacts : Collections.emptyList();
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }
}
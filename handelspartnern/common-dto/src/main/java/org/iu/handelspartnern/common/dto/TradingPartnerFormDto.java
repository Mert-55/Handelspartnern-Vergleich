package org.iu.handelspartnern.common.dto;

import org.iu.handelspartnern.common.entity.PartnerStatus;
import org.iu.handelspartnern.common.entity.PartnerType;
import java.math.BigDecimal;

public class TradingPartnerFormDto {
    private Long id;
    private String name;
    private PartnerType type;
    private PartnerStatus status;
    private String about;
    private String taxId;
    private String paymentTerms;
    private BigDecimal claims;
    private BigDecimal payable;
    private String contacts; // String statt List<Contact>
    private String addresses; // String statt List<Address>
    private String corporateImageUrl;

    // Konstruktoren
    public TradingPartnerFormDto() {
    }

    public TradingPartnerFormDto(String name, PartnerType type, PartnerStatus status, String about, String taxId,
            String paymentTerms, BigDecimal claims, BigDecimal payable, String contacts, String addresses,
            String corporateImageUrl) {
        this.name = name;
        this.type = type;
        this.status = status;
        this.about = about;
        this.taxId = taxId;
        this.paymentTerms = paymentTerms;
        this.claims = claims;
        this.payable = payable;
        this.contacts = contacts;
        this.addresses = addresses;
        this.corporateImageUrl = corporateImageUrl;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getCorporateImageUrl() {
        return corporateImageUrl;
    }

    public void setCorporateImageUrl(String corporateImageUrl) {
        this.corporateImageUrl = corporateImageUrl;
    }

    @Override
    public String toString() {
        return "TradingPartnerFormDto{" + "id=" + id + ", name='" + name + '\'' + ", type=" + type + ", status="
                + status + ", about='" + about + '\'' + ", taxId='" + taxId + '\'' + ", paymentTerms='" + paymentTerms
                + '\'' + ", claims=" + claims + ", payable=" + payable + ", contacts='" + contacts + '\''
                + ", addresses='" + addresses + '\'' + ", corporateImageUrl='" + corporateImageUrl + '\'' + '}';
    }
}

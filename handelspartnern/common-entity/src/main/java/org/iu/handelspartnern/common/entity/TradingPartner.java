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
import org.iu.handelspartnern.common.entity.converter.FinancialEntryConverter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

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

    @Column(columnDefinition = "TEXT")
    @Convert(converter = FinancialEntryConverter.class)
    private List<FinancialEntry> financialEntries;

    // Default Constructor
    public TradingPartner() {
        this.name = "Unbenannt";
        this.paymentTerms = "Net 30";
        this.status = PartnerStatus.ACTIVE;
        this.type = PartnerType.SUPPLIER;
        this.addresses = new ArrayList<>();
        this.contacts = new ArrayList<>();
        this.financialEntries = new ArrayList<>();
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
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Contact> getContacts() {
        if (contacts == null) {
            contacts = new ArrayList<>();
        }
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public List<FinancialEntry> getFinancialEntries() {
        if (financialEntries == null) {
            financialEntries = new ArrayList<>();
        }
        return financialEntries;
    }

    public void setFinancialEntries(List<FinancialEntry> financialEntries) {
        this.financialEntries = financialEntries;
        recalculateFinancials();
    }

    public void addFinancialEntry(FinancialEntry entry) {
        getFinancialEntries().add(entry);
        recalculateFinancials();
    }

    // ===== ALIAS METHODS FOR COMPATIBILITY =====

    // For backward compatibility with service layer that expects created/updated
    public LocalDateTime getCreated() {
        return dateCreated;
    }

    public void setCreated(LocalDateTime created) {
        this.dateCreated = created;
    }

    public LocalDateTime getUpdated() {
        return dateModified;
    }

    public void setUpdated(LocalDateTime updated) {
        this.dateModified = updated;
    }

    // ===== STRING-BASED CONTACT/ADDRESS METHODS =====

    // Convert contacts list to string representation
    public String getContactsAsString() {
        if (getContacts().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        List<Contact> contactList = getContacts();
        for (int i = 0; i < contactList.size(); i++) {
            Contact contact = contactList.get(i);
            if (contact != null) {
                if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
                    sb.append("E-Mail: ").append(contact.getEmail().trim()).append("\n");
                }
                if (contact.getPhone() != null && !contact.getPhone().trim().isEmpty()) {
                    sb.append("Telefon: ").append(contact.getPhone().trim()).append("\n");
                }
                if (contact.getName() != null && !contact.getName().trim().isEmpty()) {
                    sb.append("Ansprechpartner: ").append(contact.getName().trim()).append("\n");
                }
                if (contact.getRole() != null && !contact.getRole().trim().isEmpty()) {
                    sb.append("Abteilung: ").append(contact.getRole().trim());
                }
                if (i < contactList.size() - 1) {
                    sb.append("\n---\n");
                }
            }
        }
        return sb.toString().trim();
    }

    // Set contacts from string representation
    public void setContactsAsString(String contactsString) {
        getContacts().clear();
        if (contactsString == null || contactsString.trim().isEmpty()) {
            return;
        }

        String[] blocks = contactsString.split("\n---\n");
        for (String block : blocks) {
            Contact contact = new Contact();
            String[] lines = block.split("\n");
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.startsWith("E-Mail:")) {
                    contact.setEmail(line.substring(7).trim());
                } else if (line.startsWith("Telefon:")) {
                    contact.setPhone(line.substring(8).trim());
                } else if (line.startsWith("Ansprechpartner:")) {
                    contact.setName(line.substring(15).trim());
                } else if (line.startsWith("Abteilung:")) {
                    contact.setRole(line.substring(10).trim());
                }
            }
            if ((contact.getName() != null && !contact.getName().isBlank()) ||
                    (contact.getEmail() != null && !contact.getEmail().isBlank()) ||
                    (contact.getPhone() != null && !contact.getPhone().isBlank())) {
                getContacts().add(contact);
            }
        }
    }

    // Convert addresses list to string representation
    public String getAddressesAsString() {
        if (getAddresses().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        List<Address> addressList = getAddresses();
        for (int i = 0; i < addressList.size(); i++) {
            Address address = addressList.get(i);
            if (address != null) {
                if (address.getStreet() != null && !address.getStreet().trim().isEmpty()) {
                    sb.append(address.getStreet().trim()).append("\n");
                }
                if (address.getZipCode() != null && !address.getZipCode().trim().isEmpty()) {
                    sb.append(address.getZipCode().trim());
                    if (address.getCity() != null && !address.getCity().trim().isEmpty()) {
                        sb.append(" ").append(address.getCity().trim());
                    }
                    sb.append("\n");
                } else if (address.getCity() != null && !address.getCity().trim().isEmpty()) {
                    sb.append(address.getCity().trim()).append("\n");
                }
                if (address.getCountry() != null && !address.getCountry().trim().isEmpty()) {
                    sb.append(address.getCountry().trim());
                }
                if (i < addressList.size() - 1) {
                    sb.append("\n---\n");
                }
            }
        }
        return sb.toString().trim();
    }

    // Set addresses from string representation
    public void setAddressesAsString(String addressesString) {
        getAddresses().clear();
        if (addressesString == null || addressesString.trim().isEmpty()) {
            return;
        }

        String[] blocks = addressesString.split("\n---\n");
        for (String block : blocks) {
            Address address = new Address();
            String[] lines = block.split("\n");
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.toLowerCase().startsWith("typ:")) {
                    address.setType(line.substring(4).trim());
                    continue;
                }

                if (address.getStreet() == null) {
                    address.setStreet(line);
                } else if (address.getZipCode() == null) {
                    String[] parts = line.split(" ", 2);
                    if (parts.length == 2) {
                        address.setZipCode(parts[0]);
                        address.setCity(parts[1]);
                    } else {
                        address.setCity(line);
                    }
                } else if (address.getCountry() == null) {
                    address.setCountry(line);
                }
            }

            if ((address.getStreet() != null && !address.getStreet().isBlank()) ||
                    (address.getCity() != null && !address.getCity().isBlank())) {
                getAddresses().add(address);
            }
        }
    }

    public FinancialOverview getFinancialOverview() {
        recalculateFinancials();

        BigDecimal openClaims = BigDecimal.ZERO;
        BigDecimal settledClaims = BigDecimal.ZERO;
        BigDecimal openPayables = BigDecimal.ZERO;
        BigDecimal settledPayables = BigDecimal.ZERO;

        for (FinancialEntry entry : getFinancialEntries()) {
            if (entry.getAmount() == null) {
                continue;
            }

            if (FinancialEntryType.CLAIM.equals(entry.getType())) {
                if (entry.isSettled()) {
                    settledClaims = settledClaims.add(entry.getAmount());
                } else {
                    openClaims = openClaims.add(entry.getAmount());
                }
            } else if (FinancialEntryType.PAYABLE.equals(entry.getType())) {
                if (entry.isSettled()) {
                    settledPayables = settledPayables.add(entry.getAmount());
                } else {
                    openPayables = openPayables.add(entry.getAmount());
                }
            }
        }

        List<FinancialEntryView> recent = getFinancialEntries().stream()
                .sorted(Comparator.comparing(FinancialEntry::getDate, Comparator.nullsLast(LocalDate::compareTo))
                        .reversed())
                .limit(5)
                .map(FinancialEntryView::from)
                .collect(Collectors.toList());

        return new FinancialOverview(openClaims, settledClaims, openPayables, settledPayables,
                getFinancialEntries().size(), recent);
    }

    public void recalculateFinancials() {
        BigDecimal openClaims = BigDecimal.ZERO;
        BigDecimal openPayables = BigDecimal.ZERO;

        for (FinancialEntry entry : getFinancialEntries()) {
            if (entry.getAmount() == null) {
                continue;
            }

            if (FinancialEntryType.CLAIM.equals(entry.getType()) && !entry.isSettled()) {
                openClaims = openClaims.add(entry.getAmount());
            }

            if (FinancialEntryType.PAYABLE.equals(entry.getType()) && !entry.isSettled()) {
                openPayables = openPayables.add(entry.getAmount());
            }
        }

        this.claims = openClaims;
        this.payable = openPayables;
    }

    public static class FinancialOverview {
        private final BigDecimal openClaims;
        private final BigDecimal settledClaims;
        private final BigDecimal openPayables;
        private final BigDecimal settledPayables;
        private final int transactionCount;
        private final List<FinancialEntryView> recentTransactions;

        public FinancialOverview(BigDecimal openClaims, BigDecimal settledClaims, BigDecimal openPayables,
                BigDecimal settledPayables, int transactionCount, List<FinancialEntryView> recentTransactions) {
            this.openClaims = openClaims;
            this.settledClaims = settledClaims;
            this.openPayables = openPayables;
            this.settledPayables = settledPayables;
            this.transactionCount = transactionCount;
            this.recentTransactions = recentTransactions;
        }

        public BigDecimal getOpenClaims() {
            return openClaims;
        }

        public BigDecimal getSettledClaims() {
            return settledClaims;
        }

        public BigDecimal getOpenPayables() {
            return openPayables;
        }

        public BigDecimal getSettledPayables() {
            return settledPayables;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        public List<FinancialEntryView> getRecentTransactions() {
            return recentTransactions;
        }
    }

    public static class FinancialEntryView {
        private final String purpose;
        private final String statusLabel;
        private final FinancialEntryType type;
        private final BigDecimal amount;
        private final String dateFormatted;

        private FinancialEntryView(String purpose, String statusLabel, FinancialEntryType type, BigDecimal amount,
                String dateFormatted) {
            this.purpose = purpose;
            this.statusLabel = statusLabel;
            this.type = type;
            this.amount = amount;
            this.dateFormatted = dateFormatted;
        }

        public static FinancialEntryView from(FinancialEntry entry) {
            return new FinancialEntryView(entry.getPurpose(), entry.getStatusLabel(), entry.getType(),
                    entry.getAmount(), entry.getDateFormatted());
        }

        public String getPurpose() {
            return purpose;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public FinancialEntryType getType() {
            return type;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public String getDateFormatted() {
            return dateFormatted;
        }
    }
}
# 🚀 Complete Module Setup Guide - TradingPartner Homepage

## 🎯 **Ziel: GitHub-Style Homepage für TradingPartner Management**

**Outcome:** Zwei unabhängige, funktionsfähige Web-Anwendungen (Spring Boot & Spark Java) mit gemeinsamer Business-Logic und GitHub-ähnlichem UI-Design.

## 📋 **Setup-Reihenfolge (Critical Path):**

1. **common-entities** → JPA Entities & Enums
2. **common-dto** → Data Transfer Objects  
3. **common-dao** → Repository Interfaces
4. **common-services** → Service Layer Interfaces
5. **common-templates** → Thymeleaf Templates & CSS
6. **spring-web** → Spring Boot Application
7. **spark-web** → Spark Java Application

---

## 🔧 **SCHRITT 1: common-entities Setup**

### **1.1 POM Configuration**

**File:** `common-entities/pom.xml`
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>common-entities</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <!-- JPA API -->
        <dependency>
            <groupId>jakarta.persistence</groupId>
            <artifactId>jakarta.persistence-api</artifactId>
            <version>3.1.0</version>
        </dependency>
        
        <!-- Hibernate Core -->
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>6.2.7.Final</version>
        </dependency>
        
        <!-- Jackson for JSON Converters -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.15.2</version>
        </dependency>
    </dependencies>
</project>
```

### **1.2 Entity Classes erstellen**

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/PartnerType.java`
```java
package de.hochschule.handelspartner.common.entities;

public enum PartnerType {
    SUPPLIER("Lieferant"),
    CUSTOMER("Kunde"),
    PARTNER("Partner");
    
    private final String displayName;
    
    PartnerType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/PartnerStatus.java`
```java
package de.hochschule.handelspartner.common.entities;

public enum PartnerStatus {
    ACTIVE("Aktiv"),
    PENDING_APPROVAL("Wartend"),
    INACTIVE("Inaktiv"),
    SUSPENDED("Gesperrt");
    
    private final String displayName;
    
    PartnerStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
```

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/Address.java`
```java
package de.hochschule.handelspartner.common.entities;

public class Address {
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private String type; // BILLING, SHIPPING, etc.
    
    // Constructors
    public Address() {}
    
    public Address(String street, String city, String zipCode, String country, String type) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.country = country;
        this.type = type;
    }
    
    // Getters and Setters
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
```

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/Contact.java`
```java
package de.hochschule.handelspartner.common.entities;

public class Contact {
    private String name;
    private String email;
    private String phone;
    private String role;
    
    // Constructors
    public Contact() {}
    
    public Contact(String name, String email, String phone, String role) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
```

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/converter/AddressConverter.java`
```java
package de.hochschule.handelspartner.common.entities.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hochschule.handelspartner.common.entities.Address;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class AddressConverter implements AttributeConverter<List<Address>, String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(List<Address> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(addresses);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting addresses to JSON", e);
        }
    }
    
    @Override
    public List<Address> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Address>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to addresses", e);
        }
    }
}
```

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/converter/ContactConverter.java`
```java
package de.hochschule.handelspartner.common.entities.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.hochschule.handelspartner.common.entities.Contact;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Collections;
import java.util.List;

@Converter
public class ContactConverter implements AttributeConverter<List<Contact>, String> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public String convertToDatabaseColumn(List<Contact> contacts) {
        if (contacts == null || contacts.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(contacts);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting contacts to JSON", e);
        }
    }
    
    @Override
    public List<Contact> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<Contact>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to contacts", e);
        }
    }
}
```

**File:** `common-entities/src/main/java/de/hochschule/handelspartner/common/entities/TradingPartner.java`
```java
package de.hochschule.handelspartner.common.entities;

import jakarta.persistence.*;
import de.hochschule.handelspartner.common.entities.converter.AddressConverter;
import de.hochschule.handelspartner.common.entities.converter.ContactConverter;
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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCorporateImageUrl() { 
        return corporateImageUrl != null ? corporateImageUrl : "https://picsum.photos/60/60?random=" + id; 
    }
    public void setCorporateImageUrl(String corporateImageUrl) { this.corporateImageUrl = corporateImageUrl; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    
    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }
    
    public LocalDateTime getDateCreated() { return dateCreated; }
    public LocalDateTime getDateModified() { return dateModified; }
    
    public BigDecimal getClaims() { return claims; }
    public void setClaims(BigDecimal claims) { this.claims = claims; }
    
    public BigDecimal getPayable() { return payable; }
    public void setPayable(BigDecimal payable) { this.payable = payable; }
    
    public PartnerType getType() { return type; }
    public void setType(PartnerType type) { this.type = type; }
    
    public PartnerStatus getStatus() { return status; }
    public void setStatus(PartnerStatus status) { this.status = status; }
    
    public String getAbout() { return about; }
    public void setAbout(String about) { this.about = about; }
    
    public List<Address> getAddresses() { 
        return addresses != null ? addresses : Collections.emptyList(); 
    }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    
    public List<Contact> getContacts() { 
        return contacts != null ? contacts : Collections.emptyList(); 
    }
    public void setContacts(List<Contact> contacts) { this.contacts = contacts; }
}
```

---

## 🔧 **SCHRITT 2: common-dto Setup**

### **2.1 POM Configuration**

**File:** `common-dto/pom.xml`
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>common-dto</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>de.hochschule.handelspartner</groupId>
            <artifactId>common-entities</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

### **2.2 DTO Classes**

**File:** `common-dto/src/main/java/de/hochschule/handelspartner/common/dto/AddTradingPartnerDto.java`
```java
package de.hochschule.handelspartner.common.dto;

import de.hochschule.handelspartner.common.entities.PartnerType;
import java.util.Optional;

public record AddTradingPartnerDto(
    String name,
    Optional<String> about,
    Optional<String> taxId,
    Optional<String> paymentTerms,
    Optional<String> corporateImageUrl,
    PartnerType partnerType
) {
    public AddTradingPartnerDto {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (partnerType == null) {
            throw new IllegalArgumentException("Partner type cannot be null");
        }
    }
}
```

**File:** `common-dto/src/main/java/de/hochschule/handelspartner/common/dto/TradingPartnerListDto.java`
```java
package de.hochschule.handelspartner.common.dto;

import de.hochschule.handelspartner.common.entities.PartnerStatus;
import de.hochschule.handelspartner.common.entities.PartnerType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TradingPartnerListDto(
    Long id,
    String name,
    String corporateImageUrl,
    PartnerType type,
    PartnerStatus status,
    BigDecimal claims,
    BigDecimal payable,
    LocalDateTime dateModified
) {}
```

---

## 🔧 **SCHRITT 3: common-dao Setup**

### **3.1 POM Configuration**

**File:** `common-dao/pom.xml`
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>common-dao</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>de.hochschule.handelspartner</groupId>
            <artifactId>common-entities</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-jpa</artifactId>
            <version>3.1.5</version>
        </dependency>
    </dependencies>
</project>
```

### **3.2 Repository Interface**

**File:** `common-dao/src/main/java/de/hochschule/handelspartner/common/dao/TradingPartnerRepository.java`
```java
package de.hochschule.handelspartner.common.dao;

import de.hochschule.handelspartner.common.entities.TradingPartner;
import de.hochschule.handelspartner.common.entities.PartnerStatus;
import de.hochschule.handelspartner.common.entities.PartnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TradingPartnerRepository extends JpaRepository<TradingPartner, Long> {
    
    List<TradingPartner> findByStatus(PartnerStatus status);
    
    List<TradingPartner> findByType(PartnerType type);
    
    List<TradingPartner> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT tp FROM TradingPartner tp WHERE tp.status = :status ORDER BY tp.dateModified DESC")
    List<TradingPartner> findActivePartnersSortedByDate(@Param("status") PartnerStatus status);
    
    @Query("SELECT tp FROM TradingPartner tp WHERE " +
           "(:type IS NULL OR tp.type = :type) AND " +
           "(:status IS NULL OR tp.status = :status) AND " +
           "(:search IS NULL OR LOWER(tp.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<TradingPartner> findWithFilters(
        @Param("type") PartnerType type, 
        @Param("status") PartnerStatus status, 
        @Param("search") String search
    );
}
```

---

## 🔧 **SCHRITT 4: common-services Setup**

### **4.1 POM Configuration**

**File:** `common-services/pom.xml`
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>common-services</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>de.hochschule.handelspartner</groupId>
            <artifactId>common-entities</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <dependency>
            <groupId>de.hochschule.handelspartner</groupId>
            <artifactId>common-dto</artifactId>
            <version>${project.version}</version>
        </dependency>
        
        <dependency>
            <groupId>de.hochschule.handelspartner</groupId>
            <artifactId>common-dao</artifactId>
            <version>${project.version}</version>
        </dependency>
    </dependencies>
</project>
```

### **4.2 Service Interface**

**File:** `common-services/src/main/java/de/hochschule/handelspartner/common/services/TradingPartnerService.java`
```java
package de.hochschule.handelspartner.common.services;

import de.hochschule.handelspartner.common.entities.TradingPartner;
import de.hochschule.handelspartner.common.entities.PartnerStatus;
import de.hochschule.handelspartner.common.entities.PartnerType;
import de.hochschule.handelspartner.common.dto.AddTradingPartnerDto;
import de.hochschule.handelspartner.common.dto.TradingPartnerListDto;
import java.util.List;
import java.util.Optional;

public interface TradingPartnerService {
    
    /**
     * Get all trading partners with optional filtering
     */
    List<TradingPartnerListDto> getAllPartners(PartnerType type, PartnerStatus status, String search);
    
    /**
     * Get trading partner by ID
     */
    Optional<TradingPartner> getPartnerById(Long id);
    
    /**
     * Create new trading partner
     */
    TradingPartner createPartner(AddTradingPartnerDto dto);
    
    /**
     * Update existing trading partner
     */
    TradingPartner updatePartner(Long id, TradingPartner updatedPartner);
    
    /**
     * Delete trading partner
     */
    void deletePartner(Long id);
    
    /**
     * Get partners for dashboard (limited view)
     */
    List<TradingPartnerListDto> getDashboardPartners();
}
```

---

## 🔧 **SCHRITT 5: common-templates Setup**

### **5.1 POM Configuration**

**File:** `common-templates/pom.xml`
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>common-templates</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <dependency>
            <groupId>org.thymeleaf</groupId>
            <artifactId>thymeleaf</artifactId>
            <version>3.1.2.RELEASE</version>
        </dependency>
    </dependencies>
</project>
```

### **5.2 GitHub-Style Templates**

**File:** `common-templates/src/main/resources/templates/index.html`
```html
<!DOCTYPE html>
<html lang="de" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TradingPartner Management</title>
    <link rel="stylesheet" th:href="@{/css/github-style.css}">
</head>
<body>
    <div class="github-container">
        <!-- Header -->
        <header class="github-header">
            <div class="header-content">
                <h1 class="header-title">
                    <span class="icon">🏢</span>
                    TradingPartner Management
                </h1>
                <div class="header-actions">
                    <button class="btn btn-success" onclick="showAddPartnerModal()">
                        <span class="icon">➕</span>
                        Neuer Partner
                    </button>
                </div>
            </div>
        </header>

        <!-- Main Content -->
        <main class="main-content">
            <!-- Desktop Layout -->
            <div class="desktop-layout">
                <!-- Left Sidebar - Partner Details -->
                <aside class="partner-details" id="partnerDetails">
                    <div class="details-placeholder">
                        <div class="placeholder-icon">👤</div>
                        <h3>Partner auswählen</h3>
                        <p>Wählen Sie einen Partner aus der Liste, um Details zu sehen.</p>
                    </div>
                </aside>

                <!-- Right Content - Partner List -->
                <section class="partner-list-section">
                    <!-- Filters & Search -->
                    <div class="list-controls">
                        <div class="search-box">
                            <input type="text" id="searchInput" placeholder="Partner suchen...">
                        </div>
                        <div class="filter-controls">
                            <select id="typeFilter">
                                <option value="">Alle Typen</option>
                                <option value="SUPPLIER">Lieferant</option>
                                <option value="CUSTOMER">Kunde</option>
                                <option value="PARTNER">Partner</option>
                            </select>
                            <select id="statusFilter">
                                <option value="">Alle Status</option>
                                <option value="ACTIVE">Aktiv</option>
                                <option value="PENDING_APPROVAL">Wartend</option>
                                <option value="INACTIVE">Inaktiv</option>
                            </select>
                        </div>
                    </div>

                    <!-- Partner Cards -->
                    <div class="partner-cards" id="partnerCards">
                        <!-- Partner cards will be populated here -->
                        <div th:each="partner : ${partners}" 
                             class="partner-card" 
                             th:onclick="'selectPartner(' + ${partner.id} + ')'">
                            
                            <div class="card-header">
                                <img th:src="${partner.corporateImageUrl}" 
                                     th:alt="${partner.name}" 
                                     class="partner-avatar">
                                <div class="card-info">
                                    <h4 class="partner-name" th:text="${partner.name}">Partner Name</h4>
                                    <div class="partner-meta">
                                        <span class="partner-type" 
                                              th:text="${partner.type.displayName}"
                                              th:class="'type-' + ${partner.type.name().toLowerCase()}">Typ</span>
                                        <span class="partner-status"
                                              th:text="${partner.status.displayName}"
                                              th:class="'status-' + ${partner.status.name().toLowerCase()}">Status</span>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="card-body">
                                <div class="financial-info">
                                    <div class="financial-item">
                                        <span class="label">Forderungen:</span>
                                        <span class="amount positive" th:text="'€' + ${#numbers.formatDecimal(partner.claims, 1, 2)}">€0.00</span>
                                    </div>
                                    <div class="financial-item">
                                        <span class="label">Verbindlichkeiten:</span>
                                        <span class="amount negative" th:text="'€' + ${#numbers.formatDecimal(partner.payable, 1, 2)}">€0.00</span>
                                    </div>
                                </div>
                                <div class="card-footer">
                                    <small class="last-modified" 
                                           th:text="'Geändert: ' + ${#temporals.format(partner.dateModified, 'dd.MM.yyyy')}">
                                        Geändert: 01.01.2023
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>

            <!-- Mobile Layout -->
            <div class="mobile-layout">
                <!-- Mobile content will be handled by CSS media queries -->
            </div>
        </main>
    </div>

    <!-- Add Partner Modal -->
    <div id="addPartnerModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3>Neuen Partner hinzufügen</h3>
                <button class="close-modal" onclick="hideAddPartnerModal()">&times;</button>
            </div>
            <form id="addPartnerForm" th:action="@{/partners}" method="post">
                <div class="form-group">
                    <label for="partnerName">Name *</label>
                    <input type="text" id="partnerName" name="name" required>
                </div>
                <div class="form-group">
                    <label for="partnerType">Typ *</label>
                    <select id="partnerType" name="partnerType" required>
                        <option value="SUPPLIER">Lieferant</option>
                        <option value="CUSTOMER">Kunde</option>
                        <option value="PARTNER">Partner</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="partnerAbout">Beschreibung</label>
                    <textarea id="partnerAbout" name="about" rows="3"></textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" onclick="hideAddPartnerModal()">Abbrechen</button>
                    <button type="submit" class="btn btn-success">Partner erstellen</button>
                </div>
            </form>
        </div>
    </div>

    <script th:src="@{/js/trading-partner.js}"></script>
</body>
</html>
```

**File:** `common-templates/src/main/resources/static/css/github-style.css`
```css
/* GitHub-Style CSS for TradingPartner Management */

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
    background-color: #f6f8fa;
    color: #24292f;
    line-height: 1.5;
}

/* GitHub Container */
.github-container {
    min-height: 100vh;
    max-width: 1400px;
    margin: 0 auto;
}

/* Header */
.github-header {
    background: white;
    border-bottom: 1px solid #d0d7de;
    padding: 16px 24px;
    position: sticky;
    top: 0;
    z-index: 100;
}

.header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.header-title {
    font-size: 24px;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 8px;
}

.header-title .icon {
    font-size: 28px;
}

/* Buttons */
.btn {
    padding: 8px 16px;
    border-radius: 6px;
    border: 1px solid transparent;
    font-size: 14px;
    font-weight: 500;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    text-decoration: none;
    transition: all 0.2s ease;
}

.btn-success {
    background: #1f883d;
    color: white;
    border-color: #1f883d;
}

.btn-success:hover {
    background: #1a7f37;
}

.btn-secondary {
    background: #f6f8fa;
    color: #24292f;
    border-color: #d0d7de;
}

.btn-secondary:hover {
    background: #f3f4f6;
}

/* Main Content */
.main-content {
    padding: 24px;
}

/* Desktop Layout */
.desktop-layout {
    display: grid;
    grid-template-columns: 1fr 2fr;
    gap: 24px;
    min-height: calc(100vh - 120px);
}

/* Partner Details Sidebar */
.partner-details {
    background: white;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    padding: 24px;
    height: fit-content;
    position: sticky;
    top: 100px;
}

.details-placeholder {
    text-align: center;
    color: #656d76;
}

.placeholder-icon {
    font-size: 48px;
    margin-bottom: 16px;
}

/* Partner List Section */
.partner-list-section {
    background: white;
    border: 1px solid #d0d7de;
    border-radius: 8px;
    overflow: hidden;
}

/* List Controls */
.list-controls {
    padding: 16px;
    border-bottom: 1px solid #d0d7de;
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    align-items: center;
}

.search-box input {
    padding: 8px 12px;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    width: 300px;
    font-size: 14px;
}

.filter-controls {
    display: flex;
    gap: 8px;
}

.filter-controls select {
    padding: 8px 12px;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    background: white;
    font-size: 14px;
}

/* Partner Cards */
.partner-cards {
    max-height: 70vh;
    overflow-y: auto;
}

.partner-card {
    padding: 16px;
    border-bottom: 1px solid #d0d7de;
    cursor: pointer;
    transition: background-color 0.2s ease;
}

.partner-card:hover {
    background-color: #f6f8fa;
}

.partner-card:last-child {
    border-bottom: none;
}

.card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;
}

.partner-avatar {
    width: 48px;
    height: 48px;
    border-radius: 6px;
    object-fit: cover;
}

.card-info h4 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 4px;
}

.partner-meta {
    display: flex;
    gap: 8px;
}

.partner-type,
.partner-status {
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
}

/* Type Colors */
.type-supplier { background: #dbeafe; color: #1e40af; }
.type-customer { background: #dcfce7; color: #15803d; }
.type-partner { background: #fef3c7; color: #d97706; }

/* Status Colors */
.status-active { background: #dcfce7; color: #15803d; }
.status-pending_approval { background: #fef3c7; color: #d97706; }
.status-inactive { background: #f3f4f6; color: #6b7280; }
.status-suspended { background: #fecaca; color: #dc2626; }

/* Financial Info */
.financial-info {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;
    margin-bottom: 8px;
}

.financial-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.financial-item .label {
    font-size: 12px;
    color: #656d76;
}

.amount.positive { color: #1f883d; font-weight: 600; }
.amount.negative { color: #cf222e; font-weight: 600; }

.card-footer {
    border-top: 1px solid #f6f8fa;
    padding-top: 8px;
    margin-top: 8px;
}

.last-modified {
    font-size: 12px;
    color: #656d76;
}

/* Modal */
.modal {
    display: none;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0,0,0,0.4);
}

.modal-content {
    background-color: white;
    margin: 5% auto;
    padding: 0;
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
    box-shadow: 0 8px 32px rgba(0,0,0,0.12);
}

.modal-header {
    padding: 20px 24px;
    border-bottom: 1px solid #d0d7de;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    font-size: 18px;
    font-weight: 600;
}

.close-modal {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #656d76;
}

.modal form {
    padding: 24px;
}

.form-group {
    margin-bottom: 16px;
}

.form-group label {
    display: block;
    margin-bottom: 6px;
    font-weight: 500;
    font-size: 14px;
}

.form-group input,
.form-group select,
.form-group textarea {
    width: 100%;
    padding: 8px 12px;
    border: 1px solid #d0d7de;
    border-radius: 6px;
    font-size: 14px;
}

.modal-footer {
    display: flex;
    gap: 8px;
    justify-content: flex-end;
    padding-top: 16px;
    border-top: 1px solid #f6f8fa;
}

/* Mobile Layout */
.mobile-layout {
    display: none;
}

/* Media Queries */
@media (max-width: 768px) {
    .desktop-layout {
        display: none;
    }
    
    .mobile-layout {
        display: block;
    }
    
    .github-header {
        padding: 12px 16px;
    }
    
    .header-title {
        font-size: 20px;
    }
    
    .main-content {
        padding: 16px;
    }
    
    .partner-card {
        padding: 12px;
    }
    
    .search-box input {
        width: 100%;
    }
    
    .list-controls {
        flex-direction: column;
        align-items: stretch;
    }
    
    .filter-controls {
        flex-wrap: wrap;
    }
    
    .filter-controls select {
        flex: 1;
    }
}
```

**File:** `common-templates/src/main/resources/static/js/trading-partner.js`
```javascript
// Trading Partner JavaScript Functions

function selectPartner(partnerId) {
    // Highlight selected card
    document.querySelectorAll('.partner-card').forEach(card => {
        card.classList.remove('selected');
    });
    
    event.currentTarget.classList.add('selected');
    
    // Load partner details
    loadPartnerDetails(partnerId);
}

function loadPartnerDetails(partnerId) {
    // This would typically make an AJAX request to get partner details
    // For now, we'll show a placeholder
    const detailsDiv = document.getElementById('partnerDetails');
    
    detailsDiv.innerHTML = `
        <div class="partner-detail-card">
            <div class="detail-header">
                <h3>Partner Details</h3>
                <div class="detail-actions">
                    <button class="btn btn-secondary">Bearbeiten</button>
                </div>
            </div>
            <div class="detail-content">
                <p>Details für Partner ID: ${partnerId}</p>
                <p>Details werden hier geladen...</p>
            </div>
        </div>
    `;
}

function showAddPartnerModal() {
    document.getElementById('addPartnerModal').style.display = 'block';
}

function hideAddPartnerModal() {
    document.getElementById('addPartnerModal').style.display = 'none';
    document.getElementById('addPartnerForm').reset();
}

// Search and Filter Functions
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchInput');
    const typeFilter = document.getElementById('typeFilter');
    const statusFilter = document.getElementById('statusFilter');
    
    if (searchInput) {
        searchInput.addEventListener('input', filterPartners);
    }
    
    if (typeFilter) {
        typeFilter.addEventListener('change', filterPartners);
    }
    
    if (statusFilter) {
        statusFilter.addEventListener('change', filterPartners);
    }
    
    // Close modal when clicking outside
    window.addEventListener('click', function(event) {
        const modal = document.getElementById('addPartnerModal');
        if (event.target === modal) {
            hideAddPartnerModal();
        }
    });
});

function filterPartners() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    const typeFilter = document.getElementById('typeFilter').value;
    const statusFilter = document.getElementById('statusFilter').value;
    
    const cards = document.querySelectorAll('.partner-card');
    
    cards.forEach(card => {
        const name = card.querySelector('.partner-name').textContent.toLowerCase();
        const type = card.querySelector('.partner-type').classList.toString();
        const status = card.querySelector('.partner-status').classList.toString();
        
        const matchesSearch = name.includes(searchTerm);
        const matchesType = !typeFilter || type.includes(typeFilter.toLowerCase());
        const matchesStatus = !statusFilter || status.includes(statusFilter.toLowerCase());
        
        if (matchesSearch && matchesType && matchesStatus) {
            card.style.display = 'block';
        } else {
            card.style.display = 'none';
        }
    });
}
```

---

## ⚡ **NÄCHSTE SCHRITTE:**

Nach dem Setup dieser 5 Module können Sie mit **spring-web** und **spark-web** fortfahren. 

**Sollen wir mit der Konfiguration von spring-web beginnen? Das würde die erste lauffähige Web-Anwendung ergeben!** 🚀
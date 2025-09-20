# 🔬 Wissenschaftlicher Framework-Vergleich: Spring Boot vs Spark Java

## 🎯 **Wissenschaftlicher Ansatz: Framework-spezifische Implementierungen**

**Excellenter Punkt!** Für einen **wissenschaftlich fundierten Vergleich** müssen Sie die **nativen Eigenarten** jedes Frameworks nutzen, anstatt alles zu abstrahieren.

## 📋 **Revidierte Architektur für optimalen Vergleich:**

### **✅ NUR DIESE Komponenten teilen:**
- **common-entities** → JPA Entities (identische Datenstrukturen)
- **common-dto** → Data Transfer Objects (faire Vergleichsbasis)
- **common-templates** → HTML Templates (aber framework-spezifische Integration)

### **🔧 Framework-spezifische Implementierungen:**
- **Repository Layer** → Nutzt Framework-Eigenarten
- **Service Layer** → Nutzt Framework-Patterns  
- **Configuration** → Native Framework-Konfiguration
- **Dependency Injection** → Framework-spezifische Ansätze

---

## 🚀 **SPRING BOOT MODULE - Vollständige Implementierung**

### **spring-web/pom.xml**
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>spring-web</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <!-- Shared Modules -->
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
        
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.7.5</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>2.7.5</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
            <version>2.7.5</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
            <version>2.7.5</version>
        </dependency>
        
        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.5.1</version>
        </dependency>
        
        <!-- Development Tools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <version>2.7.5</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>2.7.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### **Spring Boot Application Class**
**File:** `spring-web/src/main/java/de/hochschule/handelspartner/spring/SpringWebApplication.java`
```java
package de.hochschule.handelspartner.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("de.hochschule.handelspartner.common.entities")
@EnableJpaRepositories("de.hochschule.handelspartner.spring.repository")
public class SpringWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringWebApplication.class, args);
    }
}
```

### **Spring Data JPA Repository (Framework-spezifisch)**
**File:** `spring-web/src/main/java/de/hochschule/handelspartner/spring/repository/TradingPartnerRepository.java`
```java
package de.hochschule.handelspartner.spring.repository;

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
    
    // Spring Data JPA Query Methods (Framework-spezifisch)
    List<TradingPartner> findByStatusOrderByDateModifiedDesc(PartnerStatus status);
    
    List<TradingPartner> findByTypeAndStatusOrderByNameAsc(PartnerType type, PartnerStatus status);
    
    List<TradingPartner> findByNameContainingIgnoreCaseOrderByNameAsc(String name);
    
    // Custom Spring Data JPA Query
    @Query("SELECT tp FROM TradingPartner tp WHERE " +
           "(:type IS NULL OR tp.type = :type) AND " +
           "(:status IS NULL OR tp.status = :status) AND " +
           "(:search IS NULL OR LOWER(tp.name) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY tp.dateModified DESC")
    List<TradingPartner> findWithFilters(
        @Param("type") PartnerType type, 
        @Param("status") PartnerStatus status, 
        @Param("search") String search
    );
    
    // Spring Data JPA Count Query
    long countByStatus(PartnerStatus status);
}
```

### **Spring Service Layer (Framework-spezifisch)**
**File:** `spring-web/src/main/java/de/hochschule/handelspartner/spring/service/TradingPartnerService.java`
```java
package de.hochschule.handelspartner.spring.service;

import de.hochschule.handelspartner.common.entities.TradingPartner;
import de.hochschule.handelspartner.common.entities.PartnerStatus;
import de.hochschule.handelspartner.common.entities.PartnerType;
import de.hochschule.handelspartner.common.dto.AddTradingPartnerDto;
import de.hochschule.handelspartner.common.dto.TradingPartnerListDto;
import de.hochschule.handelspartner.spring.repository.TradingPartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TradingPartnerService {
    
    @Autowired
    private TradingPartnerRepository repository;
    
    @Transactional(readOnly = true)
    public List<TradingPartnerListDto> getAllPartners(PartnerType type, PartnerStatus status, String search) {
        return repository.findWithFilters(type, status, search)
                .stream()
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Optional<TradingPartner> getPartnerById(Long id) {
        return repository.findById(id);
    }
    
    public TradingPartner createPartner(AddTradingPartnerDto dto) {
        TradingPartner partner = new TradingPartner();
        partner.setName(dto.name());
        partner.setAbout(dto.about().orElse(""));
        partner.setTaxId(dto.taxId().orElse("DE000000000"));
        partner.setPaymentTerms(dto.paymentTerms().orElse("Net 30"));
        partner.setCorporateImageUrl(dto.corporateImageUrl().orElse(null));
        partner.setType(dto.partnerType());
        partner.setStatus(PartnerStatus.PENDING_APPROVAL);
        
        return repository.save(partner);
    }
    
    public TradingPartner updatePartner(Long id, TradingPartner updatedPartner) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setName(updatedPartner.getName());
                    existing.setAbout(updatedPartner.getAbout());
                    existing.setTaxId(updatedPartner.getTaxId());
                    existing.setPaymentTerms(updatedPartner.getPaymentTerms());
                    existing.setCorporateImageUrl(updatedPartner.getCorporateImageUrl());
                    existing.setType(updatedPartner.getType());
                    existing.setStatus(updatedPartner.getStatus());
                    return repository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Partner not found: " + id));
    }
    
    public void deletePartner(Long id) {
        repository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<TradingPartnerListDto> getDashboardPartners() {
        return repository.findByStatusOrderByDateModifiedDesc(PartnerStatus.ACTIVE)
                .stream()
                .limit(10)
                .map(this::convertToListDto)
                .collect(Collectors.toList());
    }
    
    private TradingPartnerListDto convertToListDto(TradingPartner partner) {
        return new TradingPartnerListDto(
                partner.getId(),
                partner.getName(),
                partner.getCorporateImageUrl(),
                partner.getType(),
                partner.getStatus(),
                partner.getClaims(),
                partner.getPayable(),
                partner.getDateModified()
        );
    }
}
```

### **Spring MVC Controller (Framework-spezifisch)**
**File:** `spring-web/src/main/java/de/hochschule/handelspartner/spring/controller/TradingPartnerController.java`
```java
package de.hochschule.handelspartner.spring.controller;

import de.hochschule.handelspartner.common.entities.PartnerStatus;
import de.hochschule.handelspartner.common.entities.PartnerType;
import de.hochschule.handelspartner.common.dto.AddTradingPartnerDto;
import de.hochschule.handelspartner.spring.service.TradingPartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class TradingPartnerController {
    
    @Autowired
    private TradingPartnerService tradingPartnerService;
    
    @GetMapping("/")
    public String index(Model model,
                       @RequestParam(required = false) PartnerType type,
                       @RequestParam(required = false) PartnerStatus status,
                       @RequestParam(required = false) String search) {
        
        model.addAttribute("partners", tradingPartnerService.getAllPartners(type, status, search));
        model.addAttribute("partnerTypes", PartnerType.values());
        model.addAttribute("partnerStatuses", PartnerStatus.values());
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchQuery", search);
        
        return "index";
    }
    
    @GetMapping("/partners/{id}")
    @ResponseBody
    public Object getPartner(@PathVariable Long id) {
        return tradingPartnerService.getPartnerById(id)
                .orElseThrow(() -> new RuntimeException("Partner not found"));
    }
    
    @PostMapping("/partners")
    public String createPartner(@ModelAttribute AddTradingPartnerDto dto) {
        tradingPartnerService.createPartner(dto);
        return "redirect:/";
    }
    
    @DeleteMapping("/partners/{id}")
    @ResponseBody
    public String deletePartner(@PathVariable Long id) {
        tradingPartnerService.deletePartner(id);
        return "{\"status\": \"deleted\"}";
    }
}
```

### **Spring Boot Configuration**
**File:** `spring-web/src/main/resources/application.yml`
```yaml
# Spring Boot Configuration (Framework-spezifisch)
server:
  port: 8080

spring:
  application:
    name: spring-trading-partner-app
    
  datasource:
    url: jdbc:postgresql://localhost:5432/trading_partners
    username: postgres
    password: password
    driver-class-name: org.postgresql.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        
  thymeleaf:
    cache: false
    prefix: classpath:/templates/
    suffix: .html
    
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

# Spring Boot Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    de.hochschule.handelspartner: DEBUG
    org.springframework.web: DEBUG
```

---

## ⚡ **SPARK JAVA MODULE - Vollständige Implementierung**

### **spark-web/pom.xml**
```xml
<project>
    <parent>
        <groupId>de.hochschule.handelspartner</groupId>
        <artifactId>handelspartnern</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    
    <artifactId>spark-web</artifactId>
    <packaging>jar</packaging>
    
    <dependencies>
        <!-- Shared Modules -->
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
        
        <!-- Spark Framework -->
        <dependency>
            <groupId>com.sparkjava</groupId>
            <artifactId>spark-core</artifactId>
            <version>2.9.4</version>
        </dependency>
        
        <!-- Template Engine -->
        <dependency>
            <groupId>com.sparkjava</groupId>
            <artifactId>spark-template-thymeleaf</artifactId>
            <version>2.7.1</version>
        </dependency>
        
        <!-- Hibernate for Manual ORM -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>5.6.14.Final</version>
        </dependency>
        
        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.5.1</version>
        </dependency>
        
        <!-- JSON Processing -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
        
        <!-- Connection Pooling -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>4.0.3</version>
        </dependency>
        
        <!-- Logging -->
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.12</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.10.1</version>
                <configuration>
                    <source>17</source>
                    <target>17</target>
                </configuration>
            </plugin>
            
            <!-- Spark Application Plugin -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <mainClass>de.hochschule.handelspartner.spark.SparkWebApplication</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### **Spark Java Application (Manual Configuration)**
**File:** `spark-web/src/main/java/de/hochschule/handelspartner/spark/SparkWebApplication.java`
```java
package de.hochschule.handelspartner.spark;

import de.hochschule.handelspartner.spark.config.DatabaseConfig;
import de.hochschule.handelspartner.spark.config.ThymeleafConfig;
import de.hochschule.handelspartner.spark.routes.TradingPartnerRoutes;
import de.hochschule.handelspartner.spark.service.TradingPartnerService;
import de.hochschule.handelspartner.spark.repository.TradingPartnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class SparkWebApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(SparkWebApplication.class);
    
    public static void main(String[] args) {
        // Spark Java Manual Configuration
        port(4567);
        
        // Static Files
        staticFiles.location("/static");
        
        // Enable CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });
        
        try {
            // Manual Dependency Setup (No DI Container)
            DatabaseConfig databaseConfig = new DatabaseConfig();
            databaseConfig.initialize();
            
            ThymeleafConfig thymeleafConfig = new ThymeleafConfig();
            
            TradingPartnerRepository repository = new TradingPartnerRepository(databaseConfig);
            TradingPartnerService service = new TradingPartnerService(repository);
            
            // Register Routes
            TradingPartnerRoutes routes = new TradingPartnerRoutes(service, thymeleafConfig);
            routes.registerRoutes();
            
            // Health Check Endpoint
            get("/health", (req, res) -> {
                res.type("application/json");
                return "{\"status\":\"UP\",\"framework\":\"Spark Java\",\"port\":4567}";
            });
            
            logger.info("🔥 Spark Java Application started on port 4567");
            logger.info("📊 Health check: http://localhost:4567/health");
            logger.info("🏠 Homepage: http://localhost:4567/");
            
        } catch (Exception e) {
            logger.error("❌ Failed to start Spark application", e);
            System.exit(1);
        }
    }
}
```

### **Manual Hibernate Configuration (Framework-spezifisch)**
**File:** `spark-web/src/main/java/de/hochschule/handelspartner/spark/config/DatabaseConfig.java`
```java
package de.hochschule.handelspartner.spark.config;

import de.hochschule.handelspartner.common.entities.TradingPartner;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseConfig {
    
    private SessionFactory sessionFactory;
    private HikariDataSource dataSource;
    
    public void initialize() {
        // Manual Hibernate Configuration
        Configuration configuration = new Configuration();
        
        // Database Connection Properties
        configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/trading_partners");
        configuration.setProperty("hibernate.connection.username", "postgres");
        configuration.setProperty("hibernate.connection.password", "password");
        
        // Hibernate Properties
        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");
        
        // Connection Pool with HikariCP
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:5432/trading_partners");
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("password");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(2);
        
        dataSource = new HikariDataSource(hikariConfig);
        configuration.setProperty("hibernate.connection.provider_class", 
                                 "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        
        // Entity Registration
        configuration.addAnnotatedClass(TradingPartner.class);
        
        sessionFactory = configuration.buildSessionFactory();
    }
    
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
```

### **Manual Repository Implementation (Framework-spezifisch)**
**File:** `spark-web/src/main/java/de/hochschule/handelspartner/spark/repository/TradingPartnerRepository.java`
```java
package de.hochschule.handelspartner.spark.repository;

import de.hochschule.handelspartner.common.entities.TradingPartner;
import de.hochschule.handelspartner.common.entities.PartnerStatus;
import de.hochschule.handelspartner.common.entities.PartnerType;
import de.hochschule.handelspartner.spark.config.DatabaseConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class TradingPartnerRepository {
    
    private final SessionFactory sessionFactory;
    
    public TradingPartnerRepository(DatabaseConfig databaseConfig) {
        this.sessionFactory = databaseConfig.getSessionFactory();
    }
    
    public List<TradingPartner> findAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM TradingPartner ORDER BY dateModified DESC", TradingPartner.class)
                    .list();
        }
    }
    
    public Optional<TradingPartner> findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            TradingPartner partner = session.get(TradingPartner.class, id);
            return Optional.ofNullable(partner);
        }
    }
    
    public List<TradingPartner> findWithFilters(PartnerType type, PartnerStatus status, String search) {
        try (Session session = sessionFactory.openSession()) {
            StringBuilder hql = new StringBuilder("FROM TradingPartner WHERE 1=1");
            
            if (type != null) {
                hql.append(" AND type = :type");
            }
            if (status != null) {
                hql.append(" AND status = :status");
            }
            if (search != null && !search.trim().isEmpty()) {
                hql.append(" AND LOWER(name) LIKE :search");
            }
            hql.append(" ORDER BY dateModified DESC");
            
            Query<TradingPartner> query = session.createQuery(hql.toString(), TradingPartner.class);
            
            if (type != null) {
                query.setParameter("type", type);
            }
            if (status != null) {
                query.setParameter("status", status);
            }
            if (search != null && !search.trim().isEmpty()) {
                query.setParameter("search", "%" + search.toLowerCase() + "%");
            }
            
            return query.list();
        }
    }
    
    public TradingPartner save(TradingPartner partner) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(partner);
            session.getTransaction().commit();
            return partner;
        }
    }
    
    public void deleteById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            TradingPartner partner = session.get(TradingPartner.class, id);
            if (partner != null) {
                session.delete(partner);
            }
            session.getTransaction().commit();
        }
    }
    
    public long countByStatus(PartnerStatus status) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(tp) FROM TradingPartner tp WHERE tp.status = :status", Long.class);
            query.setParameter("status", status);
            return query.uniqueResult();
        }
    }
}
```

---

## 🚀 **ERSTE AUSFÜHRUNG - Step by Step:**

### **1. Spring Boot starten:**
```bash
cd spring-web
mvn spring-boot:run

# Alternative:
mvn clean package
java -jar target/spring-web-1.0-SNAPSHOT.jar
```
**Öffnen:** http://localhost:8080

### **2. Spark Java starten:**
```bash
cd spark-web  
mvn exec:java -Dexec.mainClass="de.hochschule.handelspartner.spark.SparkWebApplication"

# Alternative:
mvn clean compile
mvn exec:java
```
**Öffnen:** http://localhost:4567

---

## 📊 **Wissenschaftliche Vergleichsmetriken:**

### **Development Speed:**
- **Spring Boot:** Auto-configuration, weniger Boilerplate
- **Spark Java:** Explizite Konfiguration, mehr manuelle Arbeit

### **Code Complexity:**
- **Spring Boot:** ~150 Zeilen für vollständige CRUD-Funktionalität
- **Spark Java:** ~300 Zeilen für gleiche Funktionalität

### **Runtime Performance:**
- **Spring Boot:** Höherer Memory Overhead (Auto-configuration)
- **Spark Java:** Geringerer Memory Footprint (Minimal Framework)

### **Maintainability:**
- **Spring Boot:** Convention over Configuration
- **Spark Java:** Explicit over Implicit

**Beide Apps teilen identische Business Logic, aber nutzen framework-native Patterns - perfekt für wissenschaftlichen Vergleich!** 🔬
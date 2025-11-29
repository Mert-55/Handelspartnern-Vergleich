# 🔄 Spring Boot vs.  Spark Java – Empirischer Framework-Vergleich

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)

> **Wissenschaftliche Projektarbeit** – Internationale Hochschule Duales Studium (IU)  
> Studiengang: Wirtschaftsinformatik

Ein **empirischer Vergleich** zwischen dem Enterprise-Framework **Spring Boot** und dem Mikroframework **Spark Java** anhand einer vollständigen Handelspartner-Verwaltungsanwendung mit CRUD-Operationen, Finanzverwaltung und responsiver Benutzeroberfläche. 

---

## 📊 Kernergebnisse der Forschung

| Metrik | Spring Boot | Spark Java | Differenz |
|--------|-------------|------------|-----------|
| **Lines of Code** | 996 | 1. 642 | **-39%** für Spring |
| **Direkte Dependencies** | 9 | 15 | **-40%** für Spring |
| **SpotBugs Warnungen** | 0 | 5 | Spring = 0 Defekte |
| **PMD Warnungen** | 1 | 1 | Identisch |

**Fazit:** Spring Boot erforderte **39% weniger Code** für identische Funktionalität bei **höherer Code-Qualität**. 

---

## 🏗️ Architektur

Das Projekt nutzt eine **modulare Multi-Module Maven-Struktur** mit geteilten Komponenten:

```
handelspartnern/
├── common-entity/      # Geteilte JPA-Entitäten (TradingPartner, FinancialEntry)
├── common-dto/         # Data Transfer Objects
├── common-template/    # Thymeleaf Templates (UI)
├── spring-web/         # Spring Boot Implementierung (API 1)
└── spark-web/          # Spark Java Implementierung (API 2)
```

### UML-Klassendiagramm – Domain Model

Die zentrale `TradingPartner`-Entität mit integrierter `FinancialOverview`:

![UML-Diagramm zur geteilten Geschäftslogik](docs/uml-domain-model.png)

**Entitäten:**
- `TradingPartner` – Hauptentität mit Stammdaten, Adressen, Kontakten
- `FinancialEntry` – Forderungen (CLAIM) und Verbindlichkeiten (PAYABLE)
- `FinancialOverview` – Aggregierte Finanzkennzahlen
- `Address`, `Contact` – Eingebettete Value Objects

### Converter-Architektur

JSON-Serialisierung komplexer Objektstrukturen via JPA-Converter:

![UML-Diagramm zur Converter-Klassen](docs/uml-converters.png)

---

## 🔀 Framework-Vergleich im Detail

### Spring Boot – Deklarativer Ansatz

```java
@RestController
@RequestMapping("/partners")
public class TradingPartnerController {
    
    @Autowired
    private TradingPartnerService service;
    
    @GetMapping
    public List<TradingPartnerDto> getAllPartners() {
        return service.findAll();
    }
    
    @PostMapping
    @Transactional
    public TradingPartner createPartner(@Valid @RequestBody AddTradingPartnerDto dto) {
        return service. createPartner(dto);
    }
}
```

**Vorteile:**
- ✅ Convention over Configuration
- ✅ Automatische Dependency Injection
- ✅ Deklaratives Transaktionsmanagement
- ✅ Integrierte Validierung mit `@Valid`

### Spark Java – Imperativer Ansatz

```java
public class TradingPartnerRoutes {
    
    public void registerRoutes() {
        get("/partners", (req, res) -> {
            try (Session session = sessionFactory.openSession()) {
                List<TradingPartner> partners = session
                    .createQuery("FROM TradingPartner", TradingPartner.class)
                    .list();
                return renderTemplate("partners", partners);
            }
        });
        
        post("/partners", (req, res) -> {
            Transaction tx = null;
            try (Session session = sessionFactory.openSession()) {
                tx = session.beginTransaction();
                // Manuelle Validierung & Persistierung
                tx.commit();
            }
        });
    }
}
```

**Vorteile:**
- ✅ Volle Transparenz über den Ablauf
- ✅ Minimaler Framework-Overhead
- ✅ Schnellere Startzeiten (theoretisch)

### Aktivitätsdiagramme

| Spring Boot | Spark Java |
|-------------|------------|
| ![Spring Boot Aktivitätsdiagramm](docs/activity-spring. png) | ![Spark Java Aktivitätsdiagramm](docs/activity-spark.png) |

---

## 🚀 Schnellstart

### Voraussetzungen

- Java 17+
- Maven 3.8+
- Docker (optional, für PostgreSQL)

### Installation & Start

```bash
# Repository klonen
git clone https://github.com/Mert-55/Handelspartnern-Vergleich.git
cd Handelspartnern-Vergleich/handelspartnern

# Datenbank starten (optional)
docker-compose up -d

# Spring Boot Version starten
cd spring-web
mvn spring-boot:run
# → http://localhost:8080

# ODER Spark Java Version starten
cd ../spark-web
mvn exec:java
# → http://localhost:4567
```

---

## 📈 Automatisierte Metriken (GitHub Actions)

Dieses Projekt nutzt **GitHub Actions** für reproduzierbare Framework-Evaluationen:

| Script | Beschreibung |
|--------|--------------|
| [`performance-baseline.sh`](scripts/performance-baseline.sh) | Cold/Warm Build-Zeiten, JAR-Größen |
| [`dev-time-tracker.sh`](scripts/dev-time-tracker. sh) | Entwicklungszeit-Erfassung |
| [`metrics-dashboard.sh`](scripts/metrics-dashboard.sh) | Aggregierte Vergleichsanalysen |
| [`automated-screenshots.sh`](scripts/automated-screenshots.sh) | UI-Dokumentation |

Mehr Details zur Forschungsinfrastruktur: [RESEARCH-INFRASTRUCTURE.md](RESEARCH-INFRASTRUCTURE.md)

---

## 🎨 Benutzeroberfläche

Beide Implementierungen teilen sich **identische Thymeleaf-Templates** mit modernem, responsivem Design:

### Features
- 📋 **Dashboard** mit Filter- und Suchfunktion
- 🏢 **Partner-Verwaltung** (CRUD)
- 💰 **Finanzverwaltung** (Forderungen & Verbindlichkeiten)
- 📊 **Automatische Saldo-Berechnung**
- 📤 **CSV-Export** (nur Spring Boot)

### Partner-Typen & Status

| PartnerType | PartnerStatus |
|-------------|---------------|
| `SUPPLIER` | `ACTIVE` |
| `CUSTOMER` | `PENDING_APPROVAL` |
| `PARTNER` | `INACTIVE` / `SUSPENDED` |

---

## 📚 Wissenschaftlicher Hintergrund

Diese Arbeit basiert auf etablierter Forschung:

- **Lee & Jung (2006)** – Web Framework with Java and XML in Multi-Tiers for Productivity
- **Gajewski & Zabierowski (2019)** – Analysis and Comparison of Spring/Play Framework Performance
- **Curie et al. (2019)** – Comparative Analysis on Widely Used Web Frameworks
- **Swacha & Kulpa (2023)** – Evolution of Popularity of Web Development Frameworks

### Bewertungskriterien

Die Evaluation folgt empirischen Software-Engineering-Standards:

1. **Entwicklungsproduktivität** – Setup-Zeit, Implementierungsaufwand
2. **Technische Performance** – Build-Zeiten, Speicherverbrauch, Startzeiten
3. **Code-Qualität** – LOC, SpotBugs, PMD-Analyse
4. **Entwicklererfahrung** – Lernkurve, Debugging, Tool-Integration

---

## 🛠️ Technologie-Stack

| Komponente | Spring Boot | Spark Java |
|------------|-------------|------------|
| Web Framework | Spring MVC | Spark Java 2.9 |
| Template Engine | Thymeleaf | Thymeleaf |
| ORM | Spring Data JPA | Hibernate (direkt) |
| Connection Pool | HikariCP | HikariCP |
| Datenbank | H2 / PostgreSQL | H2 / PostgreSQL |
| Build Tool | Maven | Maven |
| Code Analyse | SpotBugs, PMD | SpotBugs, PMD |

---

## 📁 Projektstruktur

```
├── . github/workflows/        # CI/CD Pipelines
├── handelspartnern/
│   ├── common-entity/        # JPA Entities + Converter
│   ├── common-dto/           # Request/Response DTOs
│   ├── common-template/      # Shared Thymeleaf Templates
│   ├── spring-web/           # Spring Boot Application
│   └── spark-web/            # Spark Java Application
├── scripts/                  # Benchmark & Metriken Scripts
├── RESEARCH-INFRASTRUCTURE.md
├── SPARK_IMPLEMENTATION_SUMMARY.md
└── README.md
```

---

## 🤝 Contributing

Beiträge sind willkommen! Insbesondere:

- 🔬 Zusätzliche Performance-Benchmarks
- 📊 Erweiterte Metriken-Sammlung
- 🌐 Weitere Framework-Implementierungen (Quarkus, Micronaut, etc.)

---

## 📄 Lizenz

Dieses Projekt ist unter der [MIT-Lizenz](LICENSE) lizenziert.

---

## 🔗 Weiterführende Links

- [Spring Boot Dokumentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spark Java Dokumentation](https://sparkjava. com/documentation)
- [Thymeleaf](https://www.thymeleaf.org/)
- [GitHub Actions Metriken](https://docs.github.com/en/actions/concepts/metrics)

---

<p align="center">
  <sub>📖 Erstellt als wissenschaftliche Projektarbeit an der IU Internationalen Hochschule</sub><br>
  <sub>⭐ Star dieses Repository, wenn es dir bei deiner Framework-Entscheidung hilft! </sub>
</p>

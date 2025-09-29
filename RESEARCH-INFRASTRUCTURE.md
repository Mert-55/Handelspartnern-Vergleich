# 🔬 Empirical Research Infrastructure & CI/CD

This repository contains automated research infrastructure for continuous framework metrics collection, specifically designed to compare Spring Boot vs Spark Java frameworks through empirical data analysis.

## 📋 Overview

The research infrastructure provides:

- **Automated CI/CD Pipeline** for continuous metrics collection
- **Performance Baseline Measurements** for both frameworks
- **Development Time Tracking** integration
- **Code Complexity Analysis** using Checkstyle, PMD, and SpotBugs
- **Automated Screenshot Generation** for documentation
- **Interactive Dashboard** for real-time framework comparison
- **Research Data Export** for external analysis tools

## 🏗️ Architecture

```
├── .github/workflows/
│   └── research-metrics.yml        # GitHub Actions CI/CD pipeline
├── scripts/
│   ├── performance-baseline.sh     # Performance measurement tool
│   ├── dev-time-tracker.sh        # Development time tracking
│   ├── automated-screenshots.sh   # Screenshot generation
│   └── metrics-dashboard.sh       # Dashboard and report generator
├── metrics/                        # Collected metrics data
├── research-dashboard/             # Generated dashboards and reports
└── screenshots/                    # Automated screenshots
```

## 🚀 Quick Start

### 1. Performance Baseline Measurement

Establish performance baselines for both frameworks:

```bash
./scripts/performance-baseline.sh
```

**Output:**
- Build time comparison (cold vs warm builds)
- Memory usage analysis
- JAR size measurements
- Dependency analysis
- Baseline summary report

### 2. Development Time Tracking

Track development productivity across frameworks:

```bash
# Start development session
./scripts/dev-time-tracker.sh start spring-boot development

# Log development events
./scripts/dev-time-tracker.sh log build_error "Maven compilation failed" spring-boot

# Check current session
./scripts/dev-time-tracker.sh status

# Stop session
./scripts/dev-time-tracker.sh stop

# Generate reports
./scripts/dev-time-tracker.sh report week
```

### 3. Automated Screenshot Generation

Capture visual documentation of both applications:

```bash
# Start applications manually, then:
./scripts/automated-screenshots.sh

# Or start applications automatically:
./scripts/automated-screenshots.sh --start-apps
```

### 4. Metrics Dashboard Generation

Create comprehensive analysis dashboard:

```bash
./scripts/metrics-dashboard.sh
```

**Generates:**
- Interactive HTML dashboard with charts
- Comprehensive research report (Markdown)
- Research data export (CSV)
- R analysis script

## 📊 CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/research-metrics.yml`) automatically:

1. **Builds both frameworks** with performance tracking
2. **Analyzes source code complexity** using multiple tools
3. **Measures JAR sizes and dependencies**
4. **Runs tests with execution time tracking**
5. **Generates comparison reports**
6. **Exports data for external analysis**

### Triggers

- **Push/PR** to main branches
- **Daily schedule** at 02:00 UTC for baseline data
- **Manual dispatch** for on-demand analysis

### Artifacts

- Framework comparison reports (365-day retention)
- Research data exports (365-day retention)
- Build and complexity metrics (90-day retention)

## 📈 Metrics Collected

### Build Performance
- Cold build time (no cache)
- Warm build time (with cache)
- Memory usage during build
- CPU utilization
- Maximum memory consumption

### Code Complexity
- Source Lines of Code (SLOC)
- Effective lines (excluding comments/blanks)
- Number of classes and methods
- Annotation usage patterns
- Framework-specific patterns

### Artifact Analysis
- JAR file sizes
- Direct vs transitive dependencies
- Framework-specific dependency counts
- Dependency tree analysis

### Development Productivity
- Session duration tracking
- Task completion times
- Error and success event logging
- Framework-specific development patterns

## 🔬 Research Applications

### Academic Research
- **Empirical Software Engineering** studies
- **Framework comparison** research
- **Developer productivity** analysis
- **Code complexity** studies

### Industry Applications
- **Technology selection** decision support
- **Performance benchmarking**
- **Development cost estimation**
- **Team productivity tracking**

## 📊 Data Export & Analysis

### CSV Export Format
```csv
timestamp,framework,metric_category,metric_name,value,unit,context
2025-09-22T22:00:00Z,Spring Boot,build,build_time_sec,15.2,seconds,cold_build
2025-09-22T22:00:00Z,Spark Java,build,build_time_sec,8.3,seconds,cold_build
```

### R Analysis Integration
```r
# Load research data
library(readr)
data <- read_csv("research-export-20250922.csv")

# Framework comparison
data %>%
  filter(metric_category == "build") %>%
  ggplot(aes(x = framework, y = value, fill = framework)) +
  geom_boxplot() +
  labs(title = "Build Performance Comparison")
```

### SPSS Integration
The exported CSV files can be directly imported into SPSS for statistical analysis.

## 🔧 Configuration

### Maven Build Integration

The root `pom.xml` includes:
- **Checkstyle** for code style analysis
- **PMD** for code quality checks
- **SpotBugs** for bug pattern detection
- **JaCoCo** for test coverage analysis

### Customizing Metrics Collection

Edit the GitHub Actions workflow to:
- Add new metric types
- Modify collection frequency
- Change retention policies
- Add custom analysis steps

## 📋 Requirements

### System Requirements
- **Java 17+**
- **Maven 3.6+**
- **Chrome/Chromium** (for screenshots)
- **bc** (for mathematical calculations)

### For CI/CD
- GitHub repository with Actions enabled
- Appropriate permissions for artifact storage

## 🎯 Research Insights

### Expected Findings

1. **Build Performance**
   - Spring Boot: Higher initial overhead, better warm builds
   - Spark Java: Faster cold builds, consistent performance

2. **Code Complexity**
   - Spring Boot: Fewer lines, more annotations
   - Spark Java: More explicit code, manual configuration

3. **Deployment Artifacts**
   - Spring Boot: Larger JARs, more dependencies
   - Spark Java: Smaller footprint, minimal dependencies

4. **Development Productivity**
   - Spring Boot: Faster initial development, learning curve
   - Spark Java: More predictable, explicit control

## 🔮 Future Enhancements

### Planned Features
- **Runtime performance monitoring** (memory, CPU, response times)
- **Load testing integration** with JMeter
- **Database performance comparison**
- **Containerization metrics** (Docker image sizes)
- **Security vulnerability scanning**

### Research Extensions
- **User study integration** for developer experience
- **Maintenance cost analysis** over time
- **Third-party library compatibility** studies
- **Performance under different JVM configurations**

## 📚 References

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spark Java Documentation](http://sparkjava.com/)
- [Empirical Software Engineering Guidelines](https://www.empirical-software.engineering/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

## 🤝 Contributing

1. **Extend metrics collection** by adding new measurement scripts
2. **Improve analysis tools** with additional statistical methods
3. **Add visualization features** to the dashboard
4. **Document research findings** and validation studies

## 📄 License

This research infrastructure is available under the MIT License. See [LICENSE](LICENSE) for details.

---

**For Research Purposes:** This infrastructure is designed for empirical software engineering research. All collected data supports reproducible research and peer review.

**Last Updated:** 2025-09-22  
**Version:** 1.0.0
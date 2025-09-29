#!/bin/bash

# 🔬 Performance Baseline Measurement Script
# This script establishes performance baselines for both Spring Boot and Spark Java frameworks

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
METRICS_DIR="$PROJECT_ROOT/metrics"
BASELINE_DIR="$METRICS_DIR/baselines"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔬 Starting Performance Baseline Measurement${NC}"
echo "===========================================" 

# Create directories
mkdir -p "$BASELINE_DIR"
mkdir -p "$METRICS_DIR/spring-web"
mkdir -p "$METRICS_DIR/spark-web"

# Function to measure build performance
measure_build_performance() {
    local module=$1
    local framework=$2
    
    echo -e "${YELLOW}📊 Measuring build performance for $framework...${NC}"
    
    cd "$PROJECT_ROOT/handelspartnern"
    
    # Clean workspace
    mvn clean -pl "$module" -q
    
    # Measure cold build (no cache)
    start_time=$(date +%s.%N)
    memory_before=$(free -m | awk 'NR==2{print $3}')
    
    /usr/bin/time -v mvn compile -pl "$module" -am -Dcheckstyle.skip=true -q 2> "$BASELINE_DIR/${module}-cold-build.log"
    
    end_time=$(date +%s.%N)
    memory_after=$(free -m | awk 'NR==2{print $3}')
    
    cold_build_time=$(echo "$end_time - $start_time" | bc)
    memory_used=$(echo "$memory_after - $memory_before" | bc)
    
    # Extract metrics from time command
    max_memory=$(grep "Maximum resident set size" "$BASELINE_DIR/${module}-cold-build.log" | awk '{print $6}')
    cpu_time=$(grep "User time" "$BASELINE_DIR/${module}-cold-build.log" | awk '{print $4}')
    
    # Measure warm build (with cache)
    start_time=$(date +%s.%N)
    mvn compile -pl "$module" -am -Dcheckstyle.skip=true -q
    end_time=$(date +%s.%N)
    
    warm_build_time=$(echo "$end_time - $start_time" | bc)
    
    # Store baseline metrics
    echo "timestamp,framework,cold_build_sec,warm_build_sec,memory_used_mb,max_memory_kb,cpu_time_sec" > "$BASELINE_DIR/${module}-baseline.csv"
    echo "$(date -Iseconds),$framework,$cold_build_time,$warm_build_time,$memory_used,$max_memory,$cpu_time" >> "$BASELINE_DIR/${module}-baseline.csv"
    
    echo -e "${GREEN}✅ $framework baseline established${NC}"
    echo "   Cold build: ${cold_build_time}s"
    echo "   Warm build: ${warm_build_time}s"
    echo "   Memory used: ${memory_used}MB"
    echo "   Max memory: ${max_memory}KB"
}

# Function to analyze JAR size and dependencies
analyze_artifacts() {
    local module=$1
    local framework=$2
    
    echo -e "${YELLOW}📦 Analyzing artifacts for $framework...${NC}"
    
    cd "$PROJECT_ROOT/handelspartnern"
    
    # Build and package
    mvn package -pl "$module" -am -q
    
    # Measure JAR size
    jar_file="$module/target/$module-1.0-SNAPSHOT.jar"
    if [ -f "$jar_file" ]; then
        jar_size=$(stat -c%s "$jar_file")
        jar_size_mb=$(echo "scale=2; $jar_size / 1024 / 1024" | bc)
        
        echo -e "${GREEN}✅ $framework JAR size: ${jar_size_mb}MB${NC}"
    else
        jar_size=0
        jar_size_mb=0
        echo -e "${YELLOW}⚠️  $framework JAR not found${NC}"
    fi
    
    # Count dependencies
    direct_deps=$(mvn dependency:list -pl "$module" -q | grep -E "^\[INFO\].*:.*:.*:.*:compile" | wc -l)
    total_deps=$(mvn dependency:tree -pl "$module" -q | grep -E "^\[INFO\].*[\+\\\]\-" | wc -l)
    
    echo "   Dependencies: $direct_deps direct, $total_deps total"
    
    # Store artifact metrics
    echo "timestamp,framework,jar_size_bytes,jar_size_mb,direct_deps,total_deps" > "$BASELINE_DIR/${module}-artifacts.csv"
    echo "$(date -Iseconds),$framework,$jar_size,$jar_size_mb,$direct_deps,$total_deps" >> "$BASELINE_DIR/${module}-artifacts.csv"
}

# Main execution
echo -e "${BLUE}🔧 Building applications...${NC}"
cd "$PROJECT_ROOT/handelspartnern"
mvn clean compile -Dcheckstyle.skip=true -q

echo ""
echo -e "${BLUE}📊 Spring Boot Baseline Measurements${NC}"
echo "====================================="
measure_build_performance "spring-web" "Spring Boot"
analyze_artifacts "spring-web" "Spring Boot"

echo ""
echo -e "${BLUE}⚡ Spark Java Baseline Measurements${NC}"
echo "=================================="
measure_build_performance "spark-web" "Spark Java"
analyze_artifacts "spark-web" "Spark Java" 

echo ""
echo -e "${GREEN}🎉 Baseline measurements completed!${NC}"
echo "Results stored in: $BASELINE_DIR"

# Generate baseline summary report
echo -e "${BLUE}📋 Generating baseline summary report...${NC}"
cat > "$BASELINE_DIR/baseline-summary.md" << EOF
# 🔬 Performance Baseline Summary

**Generated:** $(date -Iseconds)
**Script:** performance-baseline.sh

## 📊 Build Performance Comparison

### Spring Boot
$(cat "$BASELINE_DIR/spring-web-baseline.csv" 2>/dev/null || echo "No data available")

### Spark Java  
$(cat "$BASELINE_DIR/spark-web-baseline.csv" 2>/dev/null || echo "No data available")

## 📦 Artifact Analysis

### Spring Boot Artifacts
$(cat "$BASELINE_DIR/spring-web-artifacts.csv" 2>/dev/null || echo "No data available")

### Spark Java Artifacts
$(cat "$BASELINE_DIR/spark-web-artifacts.csv" 2>/dev/null || echo "No data available")

---
*This baseline will be used for empirical research comparison.*
EOF

echo -e "${GREEN}✅ Baseline summary report generated${NC}"
echo "Report location: $BASELINE_DIR/baseline-summary.md"
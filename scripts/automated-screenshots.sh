#!/bin/bash

# 📸 Automated Screenshot Generation for Research Documentation
# This script captures screenshots of both applications for empirical research

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
SCREENSHOTS_DIR="$PROJECT_ROOT/screenshots"
DATE=$(date +%Y%m%d-%H%M%S)

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}📸 Starting Automated Screenshot Generation${NC}"
echo "==========================================="

# Create screenshots directory
mkdir -p "$SCREENSHOTS_DIR/spring-boot"
mkdir -p "$SCREENSHOTS_DIR/spark-java"
mkdir -p "$SCREENSHOTS_DIR/comparison"

# Check if applications are running
check_app_status() {
    local port=$1
    local name=$2
    
    if curl -s "http://localhost:$port" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ $name is running on port $port${NC}"
        return 0
    else
        echo -e "${RED}❌ $name is not running on port $port${NC}"
        return 1
    fi
}

# Function to take screenshot using headless browser
take_screenshot() {
    local url=$1
    local output_file=$2
    local description=$3
    
    echo -e "${YELLOW}📸 Capturing: $description${NC}"
    
    # Use Chrome in headless mode to take screenshot
    if command -v google-chrome >/dev/null 2>&1; then
        google-chrome --headless --disable-gpu --window-size=1920,1080 --screenshot="$output_file" "$url" 2>/dev/null
        echo -e "${GREEN}✅ Screenshot saved: $output_file${NC}"
    elif command -v chromium-browser >/dev/null 2>&1; then
        chromium-browser --headless --disable-gpu --window-size=1920,1080 --screenshot="$output_file" "$url" 2>/dev/null
        echo -e "${GREEN}✅ Screenshot saved: $output_file${NC}"
    else
        echo -e "${YELLOW}⚠️  Chrome/Chromium not available, using curl to test endpoint${NC}"
        curl -s "$url" > "${output_file%.png}.html"
        echo -e "${BLUE}📄 HTML content saved: ${output_file%.png}.html${NC}"
    fi
}

# Function to capture application screenshots
capture_app_screenshots() {
    local framework=$1
    local port=$2
    local base_path=$3
    
    echo -e "${BLUE}📸 Capturing $framework screenshots...${NC}"
    
    local screenshot_dir="$SCREENSHOTS_DIR/${framework,,}"
    
    # Main homepage
    take_screenshot "http://localhost:$port/" \
        "$screenshot_dir/homepage-$DATE.png" \
        "$framework Homepage"
    
    # Health endpoint
    if [ "$framework" = "Spring Boot" ]; then
        take_screenshot "http://localhost:$port/actuator/health" \
            "$screenshot_dir/health-$DATE.png" \
            "$framework Health Check"
            
        take_screenshot "http://localhost:$port/actuator/info" \
            "$screenshot_dir/info-$DATE.png" \
            "$framework Application Info"
    else
        take_screenshot "http://localhost:$port/health" \
            "$screenshot_dir/health-$DATE.png" \
            "$framework Health Check"
            
        take_screenshot "http://localhost:$port/info" \
            "$screenshot_dir/info-$DATE.png" \
            "$framework Application Info"
    fi
    
    # API endpoints
    take_screenshot "http://localhost:$port/api/partners" \
        "$screenshot_dir/api-partners-$DATE.png" \
        "$framework Partners API"
    
    # Additional application-specific pages if they exist
    for endpoint in "/partners" "/dashboard" "/admin"; do
        if curl -s "http://localhost:$port$endpoint" >/dev/null 2>&1; then
            take_screenshot "http://localhost:$port$endpoint" \
                "$screenshot_dir${endpoint//\//-}-$DATE.png" \
                "$framework $endpoint"
        fi
    done
}

# Function to generate comparison HTML
generate_comparison_html() {
    local html_file="$SCREENSHOTS_DIR/comparison/framework-comparison-$DATE.html"
    
    cat > "$html_file" << 'EOF'
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Spring Boot vs Spark Java - Visual Comparison</title>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f6f8fa;
        }
        .container {
            max-width: 1400px;
            margin: 0 auto;
        }
        .header {
            text-align: center;
            margin-bottom: 40px;
        }
        .comparison-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
            margin-bottom: 40px;
        }
        .framework-section {
            background: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }
        .framework-title {
            color: #0969da;
            margin-bottom: 15px;
            font-size: 24px;
            font-weight: 600;
        }
        .screenshot {
            width: 100%;
            border: 1px solid #d0d7de;
            border-radius: 6px;
            margin-bottom: 10px;
        }
        .screenshot-caption {
            font-size: 14px;
            color: #656d76;
            text-align: center;
            margin-bottom: 20px;
        }
        .metrics-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .metrics-table th,
        .metrics-table td {
            padding: 8px 12px;
            text-align: left;
            border-bottom: 1px solid #d0d7de;
        }
        .metrics-table th {
            background-color: #f6f8fa;
            font-weight: 600;
        }
        .timestamp {
            color: #656d76;
            font-size: 12px;
            text-align: center;
            margin-top: 40px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔬 Framework Visual Comparison</h1>
            <p>Automated screenshot comparison between Spring Boot and Spark Java implementations</p>
        </div>
        
        <div class="comparison-grid">
            <div class="framework-section">
                <h2 class="framework-title">🚀 Spring Boot</h2>
                <p>Convention over Configuration - Auto-configured web framework</p>
                <div class="screenshots">
                    <!-- Spring Boot screenshots will be inserted here -->
                </div>
            </div>
            
            <div class="framework-section">
                <h2 class="framework-title">⚡ Spark Java</h2>
                <p>Manual Configuration - Lightweight micro-framework</p>
                <div class="screenshots">
                    <!-- Spark Java screenshots will be inserted here -->
                </div>
            </div>
        </div>
        
        <div class="metrics-comparison">
            <h2>📊 Comparison Metrics</h2>
            <table class="metrics-table">
                <thead>
                    <tr>
                        <th>Metric</th>
                        <th>Spring Boot</th>
                        <th>Spark Java</th>
                        <th>Analysis</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Configuration Style</td>
                        <td>Auto-configuration with @SpringBootApplication</td>
                        <td>Manual setup with explicit routing</td>
                        <td>Spring Boot reduces boilerplate</td>
                    </tr>
                    <tr>
                        <td>Default Port</td>
                        <td>8080</td>
                        <td>4567</td>
                        <td>Different default configurations</td>
                    </tr>
                    <tr>
                        <td>Health Endpoint</td>
                        <td>/actuator/health</td>
                        <td>/health</td>
                        <td>Spring Boot provides more comprehensive actuator</td>
                    </tr>
                    <tr>
                        <td>Dependency Injection</td>
                        <td>Built-in with annotations</td>
                        <td>Manual dependency management</td>
                        <td>Spring Boot offers automatic DI container</td>
                    </tr>
                </tbody>
            </table>
        </div>
        
        <div class="timestamp">
            Generated on: DATE_PLACEHOLDER by automated screenshot tool
        </div>
    </div>
</body>
</html>
EOF

    # Replace placeholder with actual date
    sed -i "s/DATE_PLACEHOLDER/$(date -Iseconds)/" "$html_file"
    
    echo -e "${GREEN}📄 Comparison HTML generated: $html_file${NC}"
}

# Function to create screenshot metadata
create_metadata() {
    local metadata_file="$SCREENSHOTS_DIR/metadata-$DATE.json"
    
    cat > "$metadata_file" << EOF
{
    "capture_session": {
        "timestamp": "$(date -Iseconds)",
        "date": "$DATE",
        "script_version": "1.0",
        "purpose": "Empirical research documentation"
    },
    "applications": {
        "spring_boot": {
            "port": 8080,
            "framework": "Spring Boot",
            "health_endpoint": "/actuator/health",
            "screenshots_dir": "spring-boot/"
        },
        "spark_java": {
            "port": 4567,
            "framework": "Spark Java", 
            "health_endpoint": "/health",
            "screenshots_dir": "spark-java/"
        }
    },
    "screenshot_types": [
        "homepage",
        "health_check",
        "api_endpoints",
        "application_info"
    ],
    "technical_details": {
        "resolution": "1920x1080",
        "browser": "Chrome Headless",
        "format": "PNG"
    }
}
EOF

    echo -e "${GREEN}📋 Metadata file created: $metadata_file${NC}"
}

# Main execution
echo -e "${BLUE}🔍 Checking application status...${NC}"

# Check if we should start applications
start_apps=false
if [ "$1" = "--start-apps" ]; then
    start_apps=true
    echo -e "${YELLOW}🚀 Starting applications...${NC}"
    
    # Start Spring Boot in background
    cd "$PROJECT_ROOT/handelspartnern"
    mvn spring-boot:run -pl spring-web &
    spring_pid=$!
    
    # Start Spark Java in background  
    mvn exec:java -pl spark-web &
    spark_pid=$!
    
    echo -e "${BLUE}⏳ Waiting for applications to start...${NC}"
    sleep 30
fi

# Check application status
spring_running=false
spark_running=false

if check_app_status 8080 "Spring Boot"; then
    spring_running=true
fi

if check_app_status 4567 "Spark Java"; then
    spark_running=true
fi

# Capture screenshots if applications are running
if [ "$spring_running" = true ]; then
    capture_app_screenshots "Spring Boot" 8080 "/spring"
fi

if [ "$spark_running" = true ]; then
    capture_app_screenshots "Spark Java" 4567 "/spark"
fi

# Generate comparison artifacts
if [ "$spring_running" = true ] || [ "$spark_running" = true ]; then
    generate_comparison_html
    create_metadata
    
    echo ""
    echo -e "${GREEN}🎉 Screenshot generation completed!${NC}"
    echo "Results stored in: $SCREENSHOTS_DIR"
    echo ""
    echo "Generated files:"
    find "$SCREENSHOTS_DIR" -name "*$DATE*" -type f | sort
else
    echo -e "${RED}❌ No applications running. Start applications first:${NC}"
    echo "   Spring Boot: mvn spring-boot:run -pl spring-web"
    echo "   Spark Java:  mvn exec:java -pl spark-web"
    echo ""
    echo "Or run with --start-apps flag to automatically start applications"
fi

# Clean up if we started the applications
if [ "$start_apps" = true ]; then
    echo -e "${YELLOW}🧹 Cleaning up started applications...${NC}"
    if [ ! -z "$spring_pid" ]; then
        kill $spring_pid 2>/dev/null || true
    fi
    if [ ! -z "$spark_pid" ]; then
        kill $spark_pid 2>/dev/null || true
    fi
fi
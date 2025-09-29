#!/bin/bash

# 🕐 Development Time Tracking Integration
# This script tracks development time and productivity metrics for empirical research

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
TIME_LOG="$PROJECT_ROOT/metrics/development-time.log"
SESSION_FILE="$PROJECT_ROOT/metrics/.current-session"

# Colors
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

# Ensure metrics directory exists
mkdir -p "$PROJECT_ROOT/metrics"

# Function to start development session
start_session() {
    local framework=$1
    local task_type=$2
    
    if [ -f "$SESSION_FILE" ]; then
        echo -e "${YELLOW}⚠️  Active session found. Stopping previous session...${NC}"
        stop_session
    fi
    
    local session_id=$(date +%s)
    local start_time=$(date -Iseconds)
    
    echo "$session_id,$framework,$task_type,$start_time," > "$SESSION_FILE"
    
    echo -e "${GREEN}🚀 Development session started${NC}"
    echo "   Framework: $framework"
    echo "   Task: $task_type"
    echo "   Started: $start_time"
    echo "   Session ID: $session_id"
    
    # Log session start
    echo "$(date -Iseconds),SESSION_START,$framework,$task_type,$session_id" >> "$TIME_LOG"
}

# Function to stop development session
stop_session() {
    if [ ! -f "$SESSION_FILE" ]; then
        echo -e "${RED}❌ No active session found${NC}"
        return 1
    fi
    
    local session_data=$(cat "$SESSION_FILE")
    IFS=',' read -r session_id framework task_type start_time _ <<< "$session_data"
    
    local end_time=$(date -Iseconds)
    local start_epoch=$(date -d "$start_time" +%s)
    local end_epoch=$(date +%s)
    local duration=$((end_epoch - start_epoch))
    
    # Update session file with end time
    echo "$session_id,$framework,$task_type,$start_time,$end_time,$duration" > "$SESSION_FILE.completed"
    rm "$SESSION_FILE"
    
    echo -e "${GREEN}⏹️  Development session completed${NC}"
    echo "   Duration: ${duration} seconds ($(($duration / 60)) minutes)"
    echo "   Task: $task_type"
    echo "   Framework: $framework"
    
    # Log session end
    echo "$(date -Iseconds),SESSION_END,$framework,$task_type,$session_id,$duration" >> "$TIME_LOG"
    
    # Archive completed session
    cat "$SESSION_FILE.completed" >> "$PROJECT_ROOT/metrics/completed-sessions.csv"
    rm "$SESSION_FILE.completed"
}

# Function to log development events
log_event() {
    local event_type=$1
    local description=$2
    local framework=${3:-"unknown"}
    
    echo "$(date -Iseconds),EVENT,$framework,$event_type,$description" >> "$TIME_LOG"
    echo -e "${BLUE}📝 Event logged: $event_type - $description${NC}"
}

# Function to show current session status
status() {
    if [ -f "$SESSION_FILE" ]; then
        local session_data=$(cat "$SESSION_FILE")
        IFS=',' read -r session_id framework task_type start_time _ <<< "$session_data"
        
        local start_epoch=$(date -d "$start_time" +%s)
        local current_epoch=$(date +%s)
        local elapsed=$((current_epoch - start_epoch))
        
        echo -e "${GREEN}🕐 Active Development Session${NC}"
        echo "   Session ID: $session_id"
        echo "   Framework: $framework"
        echo "   Task: $task_type"
        echo "   Started: $start_time"
        echo "   Elapsed: ${elapsed} seconds ($(($elapsed / 60)) minutes)"
    else
        echo -e "${YELLOW}⏸️  No active development session${NC}"
    fi
}

# Function to generate time tracking report
report() {
    local period=${1:-"day"}
    
    echo -e "${BLUE}📊 Development Time Report ($period)${NC}"
    echo "================================="
    
    if [ ! -f "$TIME_LOG" ]; then
        echo "No time tracking data available"
        return
    fi
    
    local cutoff_date
    case $period in
        "day")
            cutoff_date=$(date -d "1 day ago" -Iseconds)
            ;;
        "week")
            cutoff_date=$(date -d "1 week ago" -Iseconds)
            ;;
        "month")
            cutoff_date=$(date -d "1 month ago" -Iseconds)
            ;;
        *)
            cutoff_date=$(date -d "1 day ago" -Iseconds)
            ;;
    esac
    
    # Session summary
    echo ""
    echo "Session Summary:"
    awk -F',' -v cutoff="$cutoff_date" '
    $1 >= cutoff && $2 == "SESSION_END" {
        framework[$3] += $6
        tasks[$4]++
        total_time += $6
        sessions++
    }
    END {
        print "  Total sessions: " sessions
        print "  Total time: " int(total_time/60) " minutes"
        print ""
        print "  By Framework:"
        for (fw in framework) {
            print "    " fw ": " int(framework[fw]/60) " minutes"
        }
        print ""
        print "  By Task Type:"
        for (task in tasks) {
            print "    " task ": " tasks[task] " sessions"
        }
    }' "$TIME_LOG"
    
    # Recent events
    echo ""
    echo "Recent Events:"
    awk -F',' -v cutoff="$cutoff_date" '
    $1 >= cutoff && $2 == "EVENT" {
        print "  " $1 " - " $4 ": " $5
    }' "$TIME_LOG" | tail -10
}

# Function to export time data for research
export_data() {
    local output_file="$PROJECT_ROOT/metrics/time-tracking-export-$(date +%Y%m%d).csv"
    
    echo "timestamp,type,framework,task_or_event,description_or_duration" > "$output_file"
    cat "$TIME_LOG" >> "$output_file"
    
    echo -e "${GREEN}📤 Time tracking data exported to: $output_file${NC}"
}

# Main command handling
case "$1" in
    "start")
        if [ -z "$2" ] || [ -z "$3" ]; then
            echo "Usage: $0 start <framework> <task_type>"
            echo "  framework: spring-boot | spark-java"
            echo "  task_type: development | testing | debugging | documentation | configuration"
            exit 1
        fi
        start_session "$2" "$3"
        ;;
    "stop")
        stop_session
        ;;
    "status")
        status
        ;;
    "log")
        if [ -z "$2" ] || [ -z "$3" ]; then
            echo "Usage: $0 log <event_type> <description> [framework]"
            echo "  event_type: build_error | test_failure | feature_complete | bug_fix | refactor"
            exit 1
        fi
        log_event "$2" "$3" "$4"
        ;;
    "report")
        report "$2"
        ;;
    "export")
        export_data
        ;;
    *)
        echo "🕐 Development Time Tracking Tool"
        echo ""
        echo "Commands:"
        echo "  start <framework> <task_type>  - Start development session"
        echo "  stop                          - Stop current session"
        echo "  status                        - Show current session"
        echo "  log <event> <description>     - Log development event"
        echo "  report [day|week|month]       - Generate time report"
        echo "  export                        - Export data for research"
        echo ""
        echo "Examples:"
        echo "  $0 start spring-boot development"
        echo "  $0 log build_error 'Maven compilation failed' spring-boot"
        echo "  $0 stop"
        echo "  $0 report week"
        ;;
esac
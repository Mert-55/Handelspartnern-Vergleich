# R Script for Empirical Research Data Analysis
# Framework Comparison: Spring Boot vs Spark Java

# Load required libraries
library(readr)
library(dplyr)
library(ggplot2)
library(tidyr)

# Import data
data <- read_csv("research-export-20250922-220519.csv")

# Basic descriptive statistics
summary(data)

# Framework comparison plots
data %>%
  filter(metric_category == "build") %>%
  ggplot(aes(x = framework, y = value, fill = framework)) +
  geom_boxplot() +
  facet_wrap(~metric_name, scales = "free") +
  labs(title = "Build Performance Comparison",
       x = "Framework", y = "Value") +
  theme_minimal()

# SLOC comparison
data %>%
  filter(metric_category == "sloc") %>%
  ggplot(aes(x = framework, y = value, fill = framework)) +
  geom_col(position = "dodge") +
  facet_wrap(~metric_name, scales = "free") +
  labs(title = "Source Lines of Code Comparison",
       x = "Framework", y = "Lines of Code") +
  theme_minimal()

# Statistical tests
# t-test for build times
build_data <- data %>% 
  filter(metric_category == "build", metric_name == "build_time_sec")

spring_build <- build_data %>% filter(framework == "Spring Boot") %>% pull(value)
spark_build <- build_data %>% filter(framework == "Spark Java") %>% pull(value)

if(length(spring_build) > 1 && length(spark_build) > 1) {
  t.test(spring_build, spark_build)
}

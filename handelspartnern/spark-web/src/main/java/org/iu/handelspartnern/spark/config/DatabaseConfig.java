package org.iu.handelspartnern.spark.config;

import org.iu.handelspartnern.common.entity.TradingPartner;
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
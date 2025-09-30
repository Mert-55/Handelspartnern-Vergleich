# Lizenzübersicht – Modul `spring-web`

Dieses Dokument fasst alle externen Bibliotheken zusammen, die im Modul `spring-web` verwendet werden, und dokumentiert deren Lizenzen. Alle aufgeführten Lizenzen erlauben eine kommerzielle Nutzung.

| Dependency | Version | Lizenz | Quelle |
|------------|---------|--------|--------|
| org.springframework.boot:spring-boot-starter-web | 2.7.5 | Apache License 2.0 | https://github.com/spring-projects/spring-boot/blob/v2.7.5/LICENSE.txt |
| org.springframework.boot:spring-boot-starter-data-jpa | 2.7.5 | Apache License 2.0 | https://github.com/spring-projects/spring-boot/blob/v2.7.5/LICENSE.txt |
| org.springframework.boot:spring-boot-starter-thymeleaf | 2.7.5 | Apache License 2.0 | https://github.com/spring-projects/spring-boot/blob/v2.7.5/LICENSE.txt |
| org.springframework.boot:spring-boot-starter-actuator | 2.7.5 | Apache License 2.0 | https://github.com/spring-projects/spring-boot/blob/v2.7.5/LICENSE.txt |
| org.postgresql:postgresql | 42.5.1 | PostgreSQL License (BSD-ähnlich) | https://github.com/pgjdbc/pgjdbc/blob/REL42.5.1/LICENSE |
| org.springframework.boot:spring-boot-devtools | 2.7.5 | Apache License 2.0 | https://github.com/spring-projects/spring-boot/blob/v2.7.5/LICENSE.txt |
| org.springframework.boot:spring-boot-maven-plugin | 2.7.5 | Apache License 2.0 | https://github.com/spring-projects/spring-boot/blob/v2.7.5/LICENSE.txt |

## Hinweise

- Alle verwendeten Spring Boot Artefakte stehen unter der Apache License 2.0 und sind für proprietäre sowie kommerzielle Projekte freigegeben.
- Der PostgreSQL JDBC Treiber nutzt die PostgreSQL Lizenz, die der MIT/BSD-Familie ähnelt und keine Copyleft-Anforderungen stellt.
- Interne Module (`common-entity`, `common-dto`, `common-template`) sind projektspezifisch und werden separat lizenziert.

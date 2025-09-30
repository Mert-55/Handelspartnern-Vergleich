# Lizenzübersicht – Modul `spark-web`

Dieses Dokument fasst alle externen Abhängigkeiten des Moduls `spark-web` zusammen und bestätigt, dass die jeweiligen Lizenzen eine kommerzielle Nutzung erlauben.

| Dependency | Version | Lizenz | Quelle |
|------------|---------|--------|--------|
| com.sparkjava:spark-core | 2.9.4 | Apache License 2.0 | https://github.com/perwendel/spark/blob/master/LICENSE
| org.thymeleaf:thymeleaf-spring5 | 3.0.15.RELEASE | Apache License 2.0 | https://github.com/thymeleaf/thymeleaf-spring/blob/3.0.15.RELEASE/LICENSE.txt
| org.thymeleaf.extras:thymeleaf-extras-java8time | 3.0.4.RELEASE | Apache License 2.0 | https://github.com/thymeleaf/thymeleaf-extras-java8time/blob/3.0.4.RELEASE/LICENSE.txt
| org.springframework:spring-context | 5.3.23 | Apache License 2.0 | https://github.com/spring-projects/spring-framework/blob/v5.3.23/LICENSE.txt
| org.hibernate:hibernate-core | 5.6.14.Final | GNU LGPL v2.1 | https://github.com/hibernate/hibernate-orm/blob/5.6.14.Final/lgpl.txt
| org.postgresql:postgresql | 42.5.1 | PostgreSQL License | https://github.com/pgjdbc/pgjdbc/blob/REL42.5.1/LICENSE
| com.google.code.gson:gson | 2.10.1 | Apache License 2.0 | https://github.com/google/gson/blob/gson-parent-2.10.1/LICENSE
| com.fasterxml.jackson.core:jackson-databind | 2.17.2 | Apache License 2.0 | https://github.com/FasterXML/jackson-databind/blob/jackson-databind-2.17.2/LICENSE
| com.fasterxml.jackson.datatype:jackson-datatype-jsr310 | 2.17.2 | Apache License 2.0 | https://github.com/FasterXML/jackson-modules-java8/blob/jackson-modules-java8-2.17.2/LICENSE
| com.zaxxer:HikariCP | 4.0.3 | Apache License 2.0 | https://github.com/brettwooldridge/HikariCP/blob/rel-4.0.3/LICENSE
| org.hibernate:hibernate-hikaricp | 5.6.14.Final | GNU LGPL v2.1 | https://github.com/hibernate/hibernate-orm/blob/5.6.14.Final/lgpl.txt
| ch.qos.logback:logback-classic | 1.2.12 | Eclipse Public License v1.0 oder GNU LGPL v2.1 | https://github.com/qos-ch/logback/blob/v_1.2.12/LICENSE.txt
| org.codehaus.mojo:exec-maven-plugin | 3.5.1 | Apache License 2.0 | https://github.com/mojohaus/exec-maven-plugin/blob/exec-maven-plugin-3.5.1/LICENSE

## Hinweise

- Alle Apache-2.0-lizenzierten Bibliotheken erlauben Nutzung, Modifikation und Verbreitung in proprietären und kommerziellen Produkten.
- Hibernate-Komponenten sowie Logback sind unter der LGPL-2.1 verfügbar. Die vollständigen Lizenztexte liegen bei; es bestehen keine Copyleft-Pflichten für reine Nutzung in proprietärer Software, solange keine Modifikationen an den Bibliotheken selbst verbreitet werden.
- Logback bietet eine alternative EPL-1.0-Lizenzierung, deren Bedingungen ebenfalls beigefügt sind.
- Projektinterne Module (`common-entity`, `common-dto`, `common-template`) verfügen über eigene Lizenzdokumentation in den entsprechenden Verzeichnissen.

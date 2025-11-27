Требования :
Java 17+, 
PostgreSQL,
Maven

Инструкция по запуску

1. Запустить PostgreSQL, создать базу данных
2. Скачать проект
3. В проекте используется liquibase, он автоматически создаст необходимые таблицы
4. Прописать в application.yml конфиги созданной бд ( у меня указаны url: jdbc:postgresql://localhost:5433/master_detail_db, username: postgres, password: rootroot)
5. Запустить приложение mvn spring-boot:run

В проекте используется swagger, поэтому протестировать работу функционала можно по адресу http://localhost:8080/swagger-ui/index.html

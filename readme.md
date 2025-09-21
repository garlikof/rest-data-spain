## Настройка окружения

Перед запуском приложения задайте значения переменных окружения с параметрами доступа к БД и Keycloak. Пример:

```shell
export DB_URL="jdbc:postgresql://db-host:5432/db"
export DB_USERNAME="app-user"
export DB_PASSWORD="change-me"
export DB_SCHEMA="public"
export KEYCLOAK_ISSUER_URI="https://id.example.com/realms/rest-data"
export KEYCLOAK_JWK_SET_URI="https://id.example.com/realms/rest-data/protocol/openid-connect/certs"
export KEYCLOAK_CLIENT_ID="rest-data-backend"
export KEYCLOAK_CLIENT_SECRET="set-secure-secret"
```

## Liquibase

Для создания таблиц выполните команду:

```shell
./gradlew update -Purl=jdbc:postgresql://server:5432/db?currentSchema=shema -Pusername=User -Ppassword=password
```

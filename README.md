# Тестовое Банковское API

Данный проект представляет собой Spring Boot приложение, предоставляющее банковские услуги, такие как управление счетами, транзакции и переводы денег. Включает документацию API, базу данных PostgreSQL и поддержку Docker для упрощённого развертывания.

---

## Возможности

### Управление Счетами
- Создание, обновление, получение и удаление счетов через эндпоинты `/api/account`.

### Транзакции
- Пополнение и снятие средств через эндпоинты `/api/transaction`.
- Проверка баланса счета.
- Получение истории транзакций.

### Денежные Переводы
- Перевод средств между счетами с использованием эндпоинта `/api/transfer`.

---

## Конфигурация

### Свойства приложения
```properties
spring.application.name=testBankApi

spring.datasource.url=jdbc:postgresql://localhost:5431/testDB
spring.datasource.username=test
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.jdbc.time_zone=UTC
spring.jpa.show-sql=true
```

### Docker Compose
Приложение может быть развернуто с использованием Docker Compose и PostgreSQL.

#### `docker-compose.yml`
```yaml
version: '3.9'
services:
  db:
    image: postgres:16
    container_name: postgres_container
    environment:
      POSTGRES_DB: testDB
      POSTGRES_USER: test
      POSTGRES_PASSWORD: password
    ports:
      - "5431:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - app-network

  app:
    image: olegshinkevich/testbankapi:latest
    networks:
      - app-network
    container_name: app_container
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres_container:5432/testDB
      SPRING_DATASOURCE_USERNAME: test
      SPRING_DATASOURCE_PASSWORD: password
    ports:
      - "8080:8080"

volumes:
  postgres_data:
networks:
  app-network:
    driver: bridge
```

---

## Запуск приложения

### Требования
1. **Java 17+**: Убедитесь, что Java установлена.
2. **Gradle**: Для сборки приложения.
3. **Docker & Docker Compose**: Для контейнеризированного развертывания.

### Шаги

#### 1. Сборка и запуск локально
```bash
gradle clean build
java -jar build/libs/testBankApi-0.0.1-SNAPSHOT.jar
```

#### 2. Запуск с использованием Docker Compose
```bash
docker-compose up --build
```

---

## Эндпоинты API

### TransactionController
| Эндпоинт                  | Метод | Описание                         |
|---------------------------|-------|-----------------------------------|
| `/api/transaction/deposit` | POST  | Пополнение счёта.                |
| `/api/transaction/withdraw`| POST  | Снятие средств со счёта.         |
| `/api/transaction/balance` | GET   | Получение баланса счёта.         |
| `/api/transaction/transactions` | GET | Получение истории транзакций.    |

### AccountController
| Эндпоинт                        | Метод | Описание                         |
|----------------------------------|-------|-----------------------------------|
| `/api/account/createAccount`     | POST  | Создание нового счёта.           |
| `/api/account/updateAccount/{id}`| PUT   | Обновление информации о счёте.   |
| `/api/account/getAccountInfo/{id}`| GET  | Получение информации о счёте.    |
| `/api/account/delete/{id}`       | DELETE| Удаление счёта.                  |

### TransferController
| Эндпоинт       | Метод | Описание                |
|-----------------|-------|-------------------------|
| `/api/transfer` | POST  | Перевод средств между счетами. |

---

## Примеры Запросов

### Пополнение Счёта
**Запрос:**
```http
POST /api/transaction/deposit
Content-Type: application/json

{
  "accountNumber": "12345678",
  "amount": 500.00
}
```

**Ответ:**
```json
{
  "accountNumber": "12345678",
  "balance": 1500.00
}
```

---

## Используемые Технологии
- **Spring Boot** для создания приложения.
- **PostgreSQL** в качестве базы данных.
- **Spring Data JPA** для ORM.
- **Lombok** для сокращения шаблонного кода.
- **Docker** для контейнеризации.

---

## Участники
Разработано Oleg Shinkevich.



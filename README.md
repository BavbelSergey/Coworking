Данный проект представляет собой систему для аренды помещений для коворкинга.
Репозиторий содержит исходный код приложения для автоматизации работы коворкинга. Система призвана решать задачи бронирования рабочих мест, управления помещениями и учёта клиентов.

## Project layout

- `backend/` - Spring Boot API
- `frontend/` - React/Vite client
- `docker-compose.yml` - PostgreSQL, backend, and frontend services

## Docker

Run the full application:

```bash
docker compose up --build
```

Frontend: http://localhost:3000

Backend API: http://localhost:8080

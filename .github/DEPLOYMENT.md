# CI/CD setup

The `CI/CD` GitHub Actions workflow runs on pull requests and pushes to `main` or `master`.

## GitHub secrets

Set these repository secrets in GitHub:

- `RAILWAY_TOKEN` - Railway account or project token.
- `RAILWAY_PROJECT_ID` - Railway project id.
- `RAILWAY_ENVIRONMENT` - Railway environment name or id, for example `production`.
- `RAILWAY_BACKEND_SERVICE` - Railway backend service name or id.
- `RAILWAY_FRONTEND_SERVICE` - Railway frontend service name or id.
- `BACKEND_HEALTH_URL` - Public backend health URL, for example `https://backend.up.railway.app/actuator/health`.
- `FRONTEND_URL` - Public frontend URL, for example `https://frontend.up.railway.app`.

## Railway service variables

Backend service:

```env
PORT=8080
SPRING_DATASOURCE_URL=jdbc:postgresql://...
SPRING_DATASOURCE_USERNAME=...
SPRING_DATASOURCE_PASSWORD=...
JWT_SECRET=...
CORS_ALLOWED_ORIGIN_PATTERNS=https://your-frontend.up.railway.app
```

Frontend service when using nginx proxy to backend private networking:

```env
PORT=80
BACKEND_HOST=${{backend.RAILWAY_PRIVATE_DOMAIN}}
BACKEND_PORT=8080
```

Leave `VITE_API_URL` unset for the proxy setup. If you choose direct browser calls to the public backend instead, set:

```env
VITE_API_URL=https://your-backend.up.railway.app/api
```


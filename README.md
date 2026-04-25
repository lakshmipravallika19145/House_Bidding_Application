# House Bidding Application (Monorepo)

Single repository containing both frontend and backend.

## Project Structure

- `frontend/` - React + Vite app
- `backend/` - Spring Boot API + WebSocket server

## Prerequisites

- Node.js 18+
- npm
- Java 17
- MySQL
- Git

## Run Locally

### 1) Start backend

```powershell
cd "backend"
copy "src\main\resources\application.example.yml" "src\main\resources\application.yml"
# edit application.yml with your local DB and mail credentials
.\mvnw.cmd spring-boot:run
```

Backend runs at: `http://localhost:8081`

### 2) Start frontend (new terminal)

```powershell
cd "frontend"
npm install
npm run dev
```

Frontend runs at: `http://localhost:5173`

## How Frontend and Backend Connect

- Frontend calls backend REST APIs at: `http://localhost:8081/api/...`
- Frontend connects to backend WebSocket endpoint at: `http://localhost:8081/ws-auction`
- CORS is configured in backend to allow frontend origin (`http://localhost:5173`) for local development.

## Push This Monorepo to GitHub

1. Create a new empty GitHub repository (for example: `house-bidding-application`).
2. From this root folder, run:

```powershell
git init
git add .
git commit -m "Initial monorepo commit: frontend + backend"
git branch -M main
git remote add origin https://github.com/<your-username>/house-bidding-application.git
git push -u origin main
```

## Security Notes

- Never commit real secrets.
- `backend/src/main/resources/application.yml` is ignored.
- Keep secrets only in your local `application.yml`.

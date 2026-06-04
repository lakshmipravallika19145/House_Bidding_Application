# Deployment Guide (Beginner Friendly)

Deploy this monorepo in **3 parts**:

1. **MySQL database** (cloud)
2. **Backend** (Spring Boot on Render)
3. **Frontend** (React on Vercel)

Repo: https://github.com/lakshmipravallika19145/House_Bidding_Application

---

## Before You Deploy

Your code now uses environment variables:

- Frontend: `VITE_API_BASE_URL` (example: `https://your-backend.onrender.com`)
- Backend: database, mail, JWT, and `APP_CORS_ALLOWED_ORIGINS`

Local development still works without changes (defaults to `http://localhost:8081`).

---

## Part 1: Cloud MySQL Database

Use any hosted MySQL (examples: **Railway**, **PlanetScale**, **Aiven**, **Render PostgreSQL won't work** — you need MySQL).

1. Create a MySQL database.
2. Note these values:
   - Host
   - Port
   - Database name (example: `house_auction_db`)
   - Username
   - Password
3. Build JDBC URL:

```text
jdbc:mysql://<HOST>:<PORT>/<DB_NAME>?createDatabaseIfNotExist=true&useSSL=true&requireSSL=true&serverTimezone=UTC
```

---

## Part 2: Deploy Backend (Render)

### A) Create Web Service

1. Go to [Render](https://render.com) and sign in with GitHub.
2. **New +** → **Web Service**.
3. Connect repo: `House_Bidding_Application`.
4. Settings:
   - **Name:** `house-bidding-api`
   - **Root Directory:** `backend`
   - **Runtime:** Java
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/demo-0.0.1-SNAPSHOT.jar`
   - **Instance type:** Free (for testing)

### B) Environment Variables (Render → Environment)

Set these (replace placeholders):

| Key | Example value |
|-----|----------------|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://...` |
| `SPRING_DATASOURCE_USERNAME` | `your_db_user` |
| `SPRING_DATASOURCE_PASSWORD` | `your_db_password` |
| `SPRING_MAIL_USERNAME` | `your_email@gmail.com` |
| `SPRING_MAIL_PASSWORD` | `gmail_app_password` |
| `APP_JWT_SECRET` | long random secret |
| `APP_ADMIN_EMAIL` | `admin@example.com` |
| `APP_CORS_ALLOWED_ORIGINS` | `https://your-frontend.vercel.app,http://localhost:5173` |

Spring Boot maps env vars like `APP_CORS_ALLOWED_ORIGINS` to `app.cors-allowed-origins`.

For local-style keys in YAML, you can also use:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### C) Deploy and copy backend URL

After deploy, copy URL, for example:

`https://house-bidding-api.onrender.com`

Test:

`https://house-bidding-api.onrender.com/test`

---

## Part 3: Deploy Frontend (Vercel)

### A) Import project

1. Go to [Vercel](https://vercel.com) and sign in with GitHub.
2. **Add New Project** → select `House_Bidding_Application`.
3. Settings:
   - **Framework Preset:** Vite
   - **Root Directory:** `frontend`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`

### B) Environment variable

Add in Vercel project settings:

| Name | Value |
|------|-------|
| `VITE_API_BASE_URL` | `https://house-bidding-api.onrender.com` |

(No trailing slash)

### C) Deploy

Click **Deploy**.  
Your frontend URL will look like:

`https://house-bidding-application.vercel.app`

### D) Update backend CORS

Go back to Render backend env vars and set:

`APP_CORS_ALLOWED_ORIGINS=https://house-bidding-application.vercel.app,http://localhost:5173`

Redeploy backend after changing CORS.

---

## Final Checklist

- [ ] Backend `/test` opens in browser
- [ ] Frontend loads login page
- [ ] Register/login works (mail config correct)
- [ ] Property image upload works
- [ ] Live auction websocket connects

---

## Important Limitations (Free Tier)

1. **Render free backend sleeps** after inactivity (first request may be slow).
2. **Uploaded files** stored in `uploads/` on server disk may be lost when service restarts/redeploys.
   - For production, move uploads to S3/Cloudinary later.
3. **Gmail SMTP** may block sending from cloud IPs; use app password and allow less secure/app access rules.
4. **WebSockets** work on Render web services, but cold starts can delay first live room connection.

---

## Optional: Redeploy After Code Changes

From project root:

```powershell
git add .
git commit -m "your message"
git push
```

Render and Vercel auto-redeploy from GitHub `main` branch.

---

## Quick Troubleshooting

| Problem | Fix |
|---------|-----|
| Frontend cannot reach API | Check `VITE_API_BASE_URL` in Vercel |
| CORS error in browser console | Add exact frontend URL to `APP_CORS_ALLOWED_ORIGINS` |
| 401 on all requests | JWT secret changed after users registered; register again |
| DB connection failed | Verify JDBC URL, username, password, and DB host allow external connections |
| WebSocket fails | Confirm backend URL in frontend and backend is awake |

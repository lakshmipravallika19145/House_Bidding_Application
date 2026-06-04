# Render Deploy Failed — Fix Checklist

If you see **"Exited with status 1"** or **"No open ports detected"**, use this list.

## 1) Render service settings

| Setting | Correct value |
|---------|----------------|
| Runtime | **Docker** |
| Root Directory | `backend` |
| Dockerfile Path | `Dockerfile` |
| Build Command | *(empty)* |
| Start Command | *(empty)* |

## 2) Environment variables (copy exactly)

Use Railway **MYSQL_PUBLIC_URL** values (NOT `mysql.railway.internal`).

| Key | Example / rule |
|-----|----------------|
| `SPRING_DATASOURCE_URL` | See JDBC line below |
| `SPRING_DATASOURCE_USERNAME` | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Railway `MYSQLPASSWORD` |
| `APP_JWT_SECRET` | plain text, **at least 32 characters** (not Base64) |
| `APP_ADMIN_EMAIL` | your email |
| `APP_CORS_ALLOWED_ORIGINS` | `http://localhost:5173` |
| `SPRING_MAIL_USERNAME` | Gmail address (for OTP) |
| `SPRING_MAIL_PASSWORD` | Gmail App Password |

### JDBC URL template (Railway public host)

Replace host/port/database with your Railway public values:

```text
jdbc:mysql://HOST:PORT/railway?createDatabaseIfNotExist=true&useSSL=true&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

**Wrong (will fail on Render):**

```text
jdbc:mysql://mysql.railway.internal:3306/railway
```

## 3) Push latest code (you run git — your author only)

```powershell
cd "C:\Users\klaks\OneDrive\Documents\House_Bidding_application"
git add .
git commit -m "Fix Render port binding and database startup"
git push
```

## 4) Redeploy

Render → **Manual Deploy** → **Deploy latest commit**

## 5) Success test

When status is **Live**, open:

```text
https://YOUR-SERVICE.onrender.com/test
```

Expected text: `Backend is running perfectly!`

## 6) JwtUtil / jwt bean error

If logs show `Error creating bean with name 'jwtUtil'`:

1. Set `APP_JWT_SECRET` to a long plain password, e.g. `HouseAuctionMySecretKey2026ForProduction`
2. **Delete** `APP_JWT_EXPIRATION` from Render if it is empty
3. Redeploy after pushing latest code (JWT no longer requires Base64)

## 7) If still failed

In Render **Logs**, search for:

- `Communications link failure` → DB URL/password wrong
- `Access denied for user` → wrong username/password
- `Unknown database` → fix database name in JDBC URL
- `JAVA_HOME` → runtime is still Node (switch to Docker)

Copy the **last 15 red log lines** and share them for exact help.

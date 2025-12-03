# 🚀 Hướng dẫn Deploy lên Render.com

## 📋 Yêu cầu
- Docker Hub account (đã có image: `letmehear/project-tracker-backend:latest`)
- Render.com account
- MySQL database (có thể dùng Render hoặc external)

---

## 🔧 Bước 1: Tạo Web Service trên Render

1. Đăng nhập vào https://render.com
2. Click **"New"** → **"Web Service"**
3. Chọn **"Deploy an existing image from a registry"**
4. Nhập Docker image URL: `letmehear/project-tracker-backend:latest`
5. Đặt tên service: `project-tracker-backend`
6. Chọn **Region**: Singapore (gần Việt Nam nhất)
7. Chọn **Instance Type**: Free (hoặc Starter $7/month cho production)

---

## ⚙️ Bước 2: Cấu hình Environment Variables

Trong tab **"Environment"**, thêm các biến sau:

### 🗄️ Database Configuration
```bash
DATA_SOURCE_URL=jdbc:mysql://YOUR_DATABASE_HOST:3306/YOUR_DATABASE_NAME?useUnicode=true&characterEncoding=UTF-8&createDatabaseIfNotExist=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

### 📧 Email Configuration
```bash
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-gmail-app-password
```
> **Lưu ý**: Tạo App Password tại https://myaccount.google.com/apppasswords

### 🌐 URL Configuration
```bash
FRONT_END_URL=https://your-frontend-domain.com
BACK_END_URL=https://your-render-app.onrender.com
```

### 🔐 Security
```bash
SECRET_KEY=your-very-long-secret-key-at-least-256-bits
GOOGLE_CLIENT_SECRET=your-google-oauth-client-secret
```

### ☁️ Cloudinary
```bash
CLOUDINARY_NAME=your-cloudinary-name
CLOUDINARY_KEY=your-cloudinary-key
CLOUDINARY_SECRET=your-cloudinary-secret
```

### 🔔 Firebase Push Notification
```bash
FIREBASE_SERVICE_ACCOUNT_JSON={"type":"service_account","project_id":"your-project-id","private_key_id":"your-key-id","private_key":"-----BEGIN PRIVATE KEY-----\nYOUR-PRIVATE-KEY-HERE\n-----END PRIVATE KEY-----\n","client_email":"your-service-account@your-project.iam.gserviceaccount.com"}
```
> **Quan trọng**: 
> - Paste toàn bộ JSON ở dạng single-line (không xuống dòng)
> - Get this from Firebase Console > Project Settings > Service Accounts > Generate New Private Key
> - **NEVER commit real credentials to Git!**

### 🗂️ File Upload
```bash
PROJECT_UPLOAD_FILE_BASE_URI=uploads/
```

### 📦 Redis (Optional - nếu dùng Redis trên Render)
```bash
REDIS_HOST=your-redis-host.render.com
REDIS_PORT=6379
REDIS_PASSWORD=your-redis-password
```

---

## 🏗️ Bước 3: Deploy

1. Click **"Create Web Service"**
2. Render sẽ tự động pull image từ Docker Hub
3. Chờ deploy hoàn tất (~2-3 phút)

---

## ✅ Bước 4: Kiểm tra Logs

Sau khi deploy xong, check logs để xác nhận:

### Logs thành công:
```
✅ Initializing Firebase from environment variable (FIREBASE_SERVICE_ACCOUNT_JSON)
✅ Firebase Admin SDK initialized successfully
✅ Started BlogApplication in X.XXX seconds
```

### Nếu thấy warning (không sao):
```
⚠️  spring.jpa.open-in-view is enabled by default...
```
→ Đây chỉ là warning, không ảnh hưởng

---

## 🔄 Cập nhật Image mới

Khi bạn build image mới và push lên Docker Hub:

```bash
# Build và push image mới
cd backend
./mvnw clean package -DskipTests
docker build -t letmehear/project-tracker-backend:latest .
docker push letmehear/project-tracker-backend:latest
```

Trên Render:
1. Vào service → **"Manual Deploy"** → **"Deploy latest commit"**
2. Hoặc chờ auto-deploy (nếu bật)

---

## 🗄️ Tạo MySQL Database trên Render

Nếu chưa có database:

1. Click **"New"** → **"PostgreSQL"** (hoặc dùng external MySQL)
2. Nếu muốn dùng MySQL external, có thể dùng:
   - **PlanetScale** (Free tier available)
   - **Railway** (Free $5/month credit)
   - **Aiven** (Free tier 1GB)

---

## 🎯 Kiểm tra API

Sau khi deploy thành công:

```bash
# Health check
curl https://your-app.onrender.com/actuator/health

# Swagger UI
https://your-app.onrender.com/swagger-ui.html

# API Docs
https://your-app.onrender.com/v3/api-docs
```

---

## 🐛 Troubleshooting

### Vấn đề: "Application failed to start"
- Check logs để xem lỗi cụ thể
- Kiểm tra DATABASE_URL có đúng không
- Kiểm tra PORT environment variable (Render tự động set)

### Vấn đề: "No open ports detected"
- Đã fix trong Dockerfile: `ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-9090} -jar app.jar"]`

### Vấn đề: Firebase không khởi tạo
- Kiểm tra `FIREBASE_SERVICE_ACCOUNT_JSON` có đúng format single-line không
- Copy lại từ output của lệnh: `cat firebase-service-account.json | tr -d '\n'`

---

## 📝 Notes

- **Free tier** của Render: Service sẽ sleep sau 15 phút không hoạt động
- **Cold start**: Lần request đầu tiên sau khi sleep sẽ mất ~30s để wake up
- **Upgrade lên Starter ($7/month)**: Không sleep, faster performance
- **Logs retention**: Free tier chỉ giữ logs 7 ngày

---

## 🎉 Done!

Bây giờ backend của bạn đã chạy trên production với:
- ✅ JVM optimization
- ✅ Firebase Push Notifications
- ✅ Dynamic PORT binding
- ✅ Environment-based configuration
- ✅ No hardcoded credentials

**Docker Image**: `letmehear/project-tracker-backend:latest`
**Size**: ~142MB (optimized với JRE Alpine)

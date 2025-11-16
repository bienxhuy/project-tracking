# 🔥 Firebase Web Config & VAPID Key - Quick Reference

## 📍 Nơi lấy thông tin

### 1️⃣ **Firebase Web Config**

**Đường dẫn:**
```
Firebase Console → ⚙️ Settings → Project settings → Your apps → Web app (</>) → Config tab
```

**Nếu chưa có Web App:**
```
Firebase Console → ⚙️ Settings → Project settings → Your apps → Click </> icon → Register app
```

**Config sẽ có dạng:**
```javascript
{
  apiKey: "AIzaSy...",
  authDomain: "your-project.firebaseapp.com",
  projectId: "your-project-id",
  storageBucket: "your-project.appspot.com",
  messagingSenderId: "123456789012",
  appId: "1:123456789012:web:xxx",
  measurementId: "G-XXXXXXXXXX"
}
```

---

### 2️⃣ **VAPID Key (Web Push Certificate)**

**Đường dẫn:**
```
Firebase Console → ⚙️ Settings → Project settings → Cloud Messaging tab → Web Push certificates
```

**Nếu chưa có key:**
- Click **"Generate key pair"** button
- Copy key được tạo ra (bắt đầu với `B...`)

**VAPID Key có dạng:**
```
BPxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

---

## 📝 **Step-by-Step với Screenshots**

### **Bước 1: Vào Firebase Console**
1. Truy cập: https://console.firebase.google.com/
2. Chọn project của bạn

### **Bước 2: Vào Project Settings**
```
Click vào ⚙️ (gear icon) → Project settings
```

### **Bước 3: Lấy Web Config**
```
Tab: General
Section: Your apps
→ Click vào Web app (</>)
→ Tab: SDK setup and configuration
→ Select: Config
→ Copy toàn bộ config object
```

### **Bước 4: Lấy VAPID Key**
```
Tab: Cloud Messaging (ở menu trên cùng)
Scroll xuống: Web Push certificates
→ Nếu chưa có: Click "Generate key pair"
→ Copy key (chuỗi bắt đầu với 'B')
```

---

## ✅ **Checklist**

- [ ] Truy cập Firebase Console
- [ ] Chọn đúng project
- [ ] Vào Project Settings
- [ ] Lấy Web Config từ tab "General" > "Your apps"
- [ ] Lấy VAPID Key từ tab "Cloud Messaging" > "Web Push certificates"
- [ ] Cập nhật vào `frontend/.env`
- [ ] Cập nhật vào `frontend/public/firebase-messaging-sw.js`

---

## 🎯 **Sử dụng Config**

### **1. Cập nhật `.env`**

```env
VITE_FIREBASE_API_KEY=<apiKey>
VITE_FIREBASE_AUTH_DOMAIN=<authDomain>
VITE_FIREBASE_PROJECT_ID=<projectId>
VITE_FIREBASE_STORAGE_BUCKET=<storageBucket>
VITE_FIREBASE_MESSAGING_SENDER_ID=<messagingSenderId>
VITE_FIREBASE_APP_ID=<appId>
VITE_FIREBASE_MEASUREMENT_ID=<measurementId>
VITE_FIREBASE_VAPID_KEY=<your-vapid-key>
```

### **2. Cập nhật Service Worker**

File: `public/firebase-messaging-sw.js`

```javascript
firebase.initializeApp({
  apiKey: "YOUR_ACTUAL_API_KEY",           // ← Thay bằng apiKey thật
  authDomain: "YOUR_ACTUAL_AUTH_DOMAIN",   // ← Thay bằng authDomain thật
  projectId: "YOUR_ACTUAL_PROJECT_ID",     // ← Thay bằng projectId thật
  storageBucket: "YOUR_ACTUAL_STORAGE_BUCKET",
  messagingSenderId: "YOUR_ACTUAL_SENDER_ID",
  appId: "YOUR_ACTUAL_APP_ID"
});
```

---

## 🆘 **Troubleshooting**

### ❌ Không thấy Web App
**Solution:** Tạo mới Web App:
- Project settings → Your apps → Click `</>` icon
- Đặt tên app → Register

### ❌ Không thấy VAPID Key
**Solution:** Generate new key:
- Cloud Messaging tab → Web Push certificates → Generate key pair

### ❌ Tab "Cloud Messaging" bị disable
**Solution:** Enable Cloud Messaging API:
- Google Cloud Console → APIs & Services → Enable APIs → Search "Cloud Messaging" → Enable

---

## 📸 **Visual Guide**

```
Firebase Console Homepage
└── Select Your Project
    └── ⚙️ (Settings Icon)
        └── Project settings
            ├── Tab: General
            │   └── Section: Your apps
            │       └── Web app (</>)
            │           └── SDK setup and configuration
            │               └── Config tab ← Copy this
            │
            └── Tab: Cloud Messaging
                └── Web Push certificates
                    └── Key pair ← Copy VAPID key
```

---

## 🔗 **Useful Links**

- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Web Setup Docs](https://firebase.google.com/docs/web/setup)
- [Cloud Messaging Web Setup](https://firebase.google.com/docs/cloud-messaging/js/client)
- [VAPID Key Guide](https://firebase.google.com/docs/cloud-messaging/js/client#configure_web_credentials_with)

---

**💡 Tip:** Lưu Firebase config vào một file riêng để dễ quản lý và không bao giờ commit file `.env` lên Git!

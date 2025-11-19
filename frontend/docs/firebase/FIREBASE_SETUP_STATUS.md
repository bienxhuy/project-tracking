# ✅ Firebase Configuration Setup Complete!

## 📝 Đã Cập Nhật

### 1. **.env** file
✅ Đã cập nhật với Firebase config thực tế:
- API Key: `AIzaSyBsxB6qhJQxA7IX_kJurnhiv-8sSvbCBQU`
- Project ID: `pose-project-tracking`
- App ID: `1:408081046822:web:445d8d05c905551681b5e7`

### 2. **firebase-messaging-sw.js**
✅ Đã cập nhật Service Worker với Firebase config

## ⚠️ QUAN TRỌNG - Cần Làm Ngay

### 🔑 Lấy VAPID Key

Bạn **CẦN PHẢI** lấy VAPID Key để push notification hoạt động:

**Các bước:**
1. Truy cập: https://console.firebase.google.com/
2. Chọn project: **pose-project-tracking**
3. Click ⚙️ → **Project settings**
4. Chọn tab **"Cloud Messaging"** (ở menu trên)
5. Scroll xuống **"Web Push certificates"**
6. Click **"Generate key pair"** (nếu chưa có)
7. Copy key (bắt đầu với `B...`)

**Cập nhật vào `.env`:**
```env
VITE_FIREBASE_VAPID_KEY=BPxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

---

## 🧪 Test Setup

### 1. Kiểm tra Environment Variables

Tạo file test: `src/test-env.ts`

```typescript
console.log('Firebase Config Check:');
console.log('API Key:', import.meta.env.VITE_FIREBASE_API_KEY ? '✅ Set' : '❌ Missing');
console.log('Project ID:', import.meta.env.VITE_FIREBASE_PROJECT_ID);
console.log('VAPID Key:', import.meta.env.VITE_FIREBASE_VAPID_KEY ? '✅ Set' : '❌ Missing');
```

### 2. Run Development Server

```bash
npm run dev
```

Mở browser console (F12) để kiểm tra logs.

---

## ✅ Checklist

- [x] Cập nhật `.env` với Firebase config
- [x] Cập nhật `firebase-messaging-sw.js`
- [ ] **Lấy VAPID Key** ← Cần làm ngay!
- [ ] Restart dev server (`npm run dev`)
- [ ] Test request notification permission
- [ ] Kiểm tra FCM token trong console

---

## 🚀 Next Steps

1. **Lấy VAPID Key** (quan trọng nhất!)
2. Restart dev server
3. Mở http://localhost:3000
4. Click "Bật thông báo"
5. Check console để lấy FCM token
6. Test gửi notification từ backend

---

## 📞 Firebase Console Quick Links

- **Project:** https://console.firebase.google.com/project/pose-project-tracking
- **Cloud Messaging:** https://console.firebase.google.com/project/pose-project-tracking/settings/cloudmessaging

---

## 🐛 Nếu Gặp Lỗi

### "Firebase: Error (auth/api-key-not-valid)"
- Kiểm tra lại API Key trong `.env`
- Restart dev server

### "Messaging: We are unable to register the default service worker"
- Kiểm tra `firebase-messaging-sw.js` đã có config chưa
- Clear browser cache

### "This browser doesn't support push notifications"
- Dùng Chrome, Firefox hoặc Edge
- Không dùng Incognito mode

---

**🎉 Setup hoàn tất! Chỉ cần lấy VAPID Key là có thể test ngay!**

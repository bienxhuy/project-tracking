# 🔄 Service Worker Auto-Generation System

## 📋 Tổng Quan

Project này sử dụng **template-based system** để generate Service Worker với Firebase config từ environment variables.

---

## 📁 File Structure

```
frontend/
├── scripts/
│   ├── generate-sw.mjs                      # Script để generate SW
│   └── firebase-messaging-sw.template.js    # Template file (commit to Git)
├── public/
│   └── firebase-messaging-sw.js             # Generated file (gitignored)
├── .env                                      # Firebase credentials (gitignored)
└── .env.example                              # Template for .env (commit to Git)
```

---

## 🔄 Workflow

### **1. Template File** (scripts/firebase-messaging-sw.template.js)
```javascript
// Chứa placeholders
firebase.initializeApp({
  apiKey: "{{VITE_FIREBASE_API_KEY}}",  // ← Placeholder
  projectId: "{{VITE_FIREBASE_PROJECT_ID}}"
});
```

**Mục đích:**
- ✅ Version control (commit lên Git)
- ✅ Dễ maintain và update
- ✅ Không chứa sensitive data

---

### **2. Environment File** (.env)
```env
VITE_FIREBASE_API_KEY=AIzaSyBsxB6qhJQxA7IX_kJurnhiv-8sSvbCBQU
VITE_FIREBASE_PROJECT_ID=pose-project-tracking
```

**Mục đích:**
- ✅ Chứa config thực
- ✅ Khác nhau giữa dev/prod
- ❌ KHÔNG commit lên Git (gitignored)

---

### **3. Generate Script** (scripts/generate-sw.mjs)
```javascript
// Đọc .env
// Đọc template
// Replace placeholders → Write to public/
```

**Chạy tự động khi:**
- `npm run dev` → Build & start dev server
- `npm run build` → Build for production
- `npm run generate-sw` → Manual generation

---

### **4. Generated File** (public/firebase-messaging-sw.js)
```javascript
// Chứa giá trị thực từ .env
firebase.initializeApp({
  apiKey: "AIzaSyBsxB6qhJQxA7IX_kJurnhiv-8sSvbCBQU",  // ← Real value
  projectId: "pose-project-tracking"
});
```

**Mục đích:**
- ✅ Browser sử dụng file này
- ✅ Auto-generated mỗi lần build
- ❌ KHÔNG commit lên Git (gitignored)

---

## 🎯 Khi Nào Làm Gì?

### **Thay đổi Firebase Config:**
```bash
# 1. Sửa file .env
VITE_FIREBASE_API_KEY=new-key

# 2. Generate lại SW
npm run generate-sw

# 3. Restart dev server
npm run dev
```

---

### **Thay đổi Service Worker Logic:**
```bash
# 1. Sửa template
scripts/firebase-messaging-sw.template.js

# 2. Generate lại
npm run generate-sw

# 3. Test
npm run dev
```

---

### **Setup Environment Mới:**
```bash
# 1. Copy env example
cp .env.example .env

# 2. Điền Firebase credentials
# Edit .env file

# 3. Generate SW
npm run generate-sw

# 4. Run
npm run dev
```

---

## ✅ Advantages

### **1. Security**
- ✅ Secrets chỉ ở trong `.env` (gitignored)
- ✅ Template không chứa sensitive data → An toàn commit
- ✅ Team members dùng credentials riêng

### **2. Flexibility**
- ✅ Dev/Staging/Prod có config khác nhau
- ✅ CI/CD dễ dàng inject environment variables
- ✅ Không cần hardcode

### **3. Maintainability**
- ✅ Chỉ sửa 1 chỗ (template)
- ✅ Auto-generate cho mọi environment
- ✅ Consistent structure

### **4. DX (Developer Experience)**
- ✅ Tự động generate khi `npm run dev`
- ✅ Không cần nhớ rebuild SW
- ✅ Clear separation: template vs generated

---

## 🚨 Important Rules

### **✅ DO:**
- ✅ Edit `scripts/firebase-messaging-sw.template.js` khi cần thay đổi logic
- ✅ Edit `.env` khi cần thay đổi config
- ✅ Commit template file
- ✅ Commit `.env.example`

### **❌ DON'T:**
- ❌ Edit `public/firebase-messaging-sw.js` trực tiếp (sẽ bị overwrite)
- ❌ Commit `.env` file
- ❌ Commit `public/firebase-messaging-sw.js`
- ❌ Hardcode secrets vào template

---

## 📝 Template Syntax

### **Placeholders:**
```javascript
{{VITE_FIREBASE_API_KEY}}        // → Replaced with process.env.VITE_FIREBASE_API_KEY
{{VITE_FIREBASE_PROJECT_ID}}     // → Replaced with process.env.VITE_FIREBASE_PROJECT_ID
```

### **Add New Placeholder:**

**1. Trong template:**
```javascript
// scripts/firebase-messaging-sw.template.js
firebase.initializeApp({
  apiKey: "{{VITE_FIREBASE_API_KEY}}",
  newField: "{{VITE_NEW_FIELD}}"  // ← Add new
});
```

**2. Trong generate script:**
```javascript
// scripts/generate-sw.mjs
const content = template
  .replace('{{VITE_FIREBASE_API_KEY}}', process.env.VITE_FIREBASE_API_KEY || '')
  .replace('{{VITE_NEW_FIELD}}', process.env.VITE_NEW_FIELD || ''); // ← Add new
```

**3. Trong .env:**
```env
VITE_NEW_FIELD=value
```

---

## 🧪 Testing

### **Verify Generated File:**
```bash
# Generate
npm run generate-sw

# Check output
cat public/firebase-messaging-sw.js

# Should see real values, not {{PLACEHOLDERS}}
```

### **Test in Browser:**
```bash
npm run dev

# Open DevTools → Application → Service Workers
# Should see firebase-messaging-sw.js registered
```

---

## 🔍 Troubleshooting

### **Problem: Placeholders không được replace**
```javascript
// File vẫn có {{VITE_FIREBASE_API_KEY}}
```

**Solution:**
1. Check `.env` file exists
2. Check variable name match exactly
3. Run `npm run generate-sw` manually
4. Check console for errors

---

### **Problem: Old config vẫn được dùng**
```javascript
// Browser dùng config cũ
```

**Solution:**
1. Unregister old service worker:
   - DevTools → Application → Service Workers → Unregister
2. Clear cache
3. Generate lại: `npm run generate-sw`
4. Hard refresh: Ctrl+Shift+R

---

### **Problem: Script không chạy khi `npm run dev`**
```bash
# Generate script không được gọi
```

**Solution:**
Check `package.json`:
```json
{
  "scripts": {
    "dev": "npm run generate-sw && vite",  // ← Should have this
    "generate-sw": "node scripts/generate-sw.mjs"
  }
}
```

---

## 🎓 Understanding the Flow

```
Developer updates .env
        ↓
npm run dev (auto runs generate-sw)
        ↓
generate-sw.mjs reads:
  - .env (for values)
  - firebase-messaging-sw.template.js (for structure)
        ↓
Replaces {{PLACEHOLDERS}} with actual values
        ↓
Writes to public/firebase-messaging-sw.js
        ↓
Vite serves this file
        ↓
Browser registers Service Worker
        ↓
✅ Notifications work!
```

---

## 📚 Related Files

- `scripts/generate-sw.mjs` - Generation script
- `scripts/firebase-messaging-sw.template.js` - Template
- `public/firebase-messaging-sw.js` - Generated (gitignored)
- `.env` - Credentials (gitignored)
- `.env.example` - Template for credentials
- `package.json` - Scripts configuration

---

**💡 Key Takeaway:**
Template = Mẫu (commit được)
Generated = File thực (không commit)

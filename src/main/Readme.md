# 🛒 Bozor Market

O'zbekiston savdo platformasi — mahalliy do'konlar va xaridorlarni bog'lovchi ilova.

---

## 📦 Loyiha tarkibi

```
bozor-market/
├── bozor-backend/     # Spring Boot REST API
├── bozor-frontend/    # Next.js veb panel
└── bozor-mobile/      # Flutter mobil ilova
```

---

## 🛠 Texnologiyalar

| Qatlam     | Texnologiya                              |
|------------|------------------------------------------|
| Backend    | Java 21, Spring Boot 3.2, PostgreSQL     |
| Frontend   | Next.js 14, TypeScript, Tailwind CSS     |
| Mobile     | Flutter 3, Dart                          |
| AI         | Groq API (llama-3.3-70b-versatile)       |
| Auth       | JWT (access + refresh token)             |

---

## ⚙️ Muhit sozlamalari

### Backend — `application.yml`

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bozor_db
    username: postgres
    password: YOUR_PASSWORD          # ← o'zgartiring

app:
  jwt:
    secret: YOUR_JWT_SECRET          # ← o'zgartiring
  groq:
    api-key: YOUR_GROQ_API_KEY       # ← https://console.groq.com
```

### Frontend — `.env.local`

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Mobile — `lib/utils/constants.dart`

```dart
// Chrome (brauzer):
const String kApiUrl = 'http://localhost:8080/api';

// Android emulator:
// const String kApiUrl = 'http://10.0.2.2:8080/api';

// Haqiqiy telefon (kompyuter IP):
// const String kApiUrl = 'http://192.168.1.XXX:8080/api';
```

---

## 🚀 Ishga tushirish

### 1. PostgreSQL sozlash

```sql
CREATE DATABASE bozor_db;
```

### 2. Backend

```bash
cd bozor-backend
# application.yml da parol va kalitlarni sozlang
./mvnw spring-boot:run
# yoki IntelliJ'da BozorApplication.java ni Run qiling
```

Backend ishga tushganda avtomatik:
- Admin hisob yaratiladi: `+998900000000` / `admin123`
- Jadvallar (`products`, `shops`, `reviews` va h.k.) avtomatik yaratiladi

### 3. Frontend

```bash
cd bozor-frontend
npm install
npm run dev
# http://localhost:3000 da ochiladi
```

### 4. Flutter (mobil)

```bash
cd bozor-mobile
flutter pub get

# Chrome'da:
flutter run -d chrome

# Android telefonida:
flutter run
```

---

## 👤 Rollar

| Rol      | Imkoniyatlar                                              |
|----------|-----------------------------------------------------------|
| `BUYER`  | Mahsulot qidirish, buyurtma berish, fikr qoldirish, AI   |
| `SELLER` | Do'kon, mahsulot, aksiya, buyurtma, moliya boshqaruvi    |
| `ADMIN`  | Barcha foydalanuvchi, do'kon va buyurtmalarni boshqarish  |

---

## 🔑 Asosiy funksiyalar

### Oluvchi (BUYER)
- 🔍 Mahsulot qidirish (kategoriya filtri, real-time)
- 🤖 AI yordamchi — Groq orqali aqlli tavsiya (narx, brend, baho asosida)
- 🏷️ Aksiyalar va chegirmalar
- 🛒 Buyurtma berish (48 soat muddat)
- ⭐ Fikr-mulohaza (yulduz + izoh + rasm)
- 🗺️ Google Maps yo'l ko'rsatish

### Sotuvchi (SELLER)
- 🏪 Do'kon yaratish (GPS joylashuv, rasm, ish vaqti)
- 📦 Mahsulot boshqaruvi (CRUD, rasm yuklash)
- 🏷️ Aksiya yaratish (foiz/summa chegirma, muddatli)
- 📋 Buyurtmalarni tasdiqlash/yakunlash/bekor qilish
- 💰 Moliyaviy hisobot (daromad, foyda, xarajat)

### Admin
- 👥 Foydalanuvchilarni boshqarish (faollashtirish/bloklash)
- 🏪 Do'konlarni boshqarish
- 📊 Umumiy statistika

---

## 📡 API Endpointlar (asosiy)

```
POST   /api/auth/login              # Kirish
POST   /api/auth/register           # Ro'yxatdan o'tish

GET    /api/buyer/search            # Mahsulot qidirish
POST   /api/buyer/orders            # Buyurtma berish
GET    /api/buyer/orders            # Mening buyurtmalarim

POST   /api/seller/shop             # Do'kon yaratish
GET    /api/seller/products         # Mening mahsulotlarim
POST   /api/seller/products         # Mahsulot qo'shish

POST   /api/reviews                 # Fikr qoldirish
GET    /api/reviews/product/{id}    # Mahsulot fikrlari

POST   /api/ai/ask                  # AI yordamchi

POST   /api/images/upload           # Rasm yuklash
GET    /api/uploads/**              # Rasm ko'rish
```

---

## 🗄️ Ma'lumotlar bazasi jadvallari

```
users          — Foydalanuvchilar (BUYER, SELLER, ADMIN)
shops          — Do'konlar (kategoriya, joylashuv, rasm)
products       — Mahsulotlar (narx, stok, brend, kategoriya)
orders         — Buyurtmalar (48 soat muddat)
order_items    — Buyurtma tarkibi
discounts      — Aksiyalar (foiz/summa, muddatli)
reviews        — Fikr-mulohazalar (yulduz, izoh, rasm)
```

---

## 🔒 .gitignore

Quyidagi fayllarni GitHub'ga yuklamang:

```
# Backend
bozor-backend/src/main/resources/application.yml
bozor-backend/target/

# Frontend
bozor-frontend/.env.local
bozor-frontend/node_modules/
bozor-frontend/.next/

# Mobile
bozor-mobile/.dart_tool/
bozor-mobile/build/
```

---

## 📱 Screenshots

> Loyiha ishga tushgandan keyin screenshot qo'shing

---

## 📄 Litsenziya

MIT License — erkin foydalaning.
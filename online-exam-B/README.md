# فاز A - مدیریت کاربران و دوره‌ها

## 📋 معرفی

فاز A شامل **احراز هویت**، **مدیریت کاربران** و **مدیریت دوره‌ها** می‌باشد که پایه و اساس سیستم مدیریت آزمون آنلاین است.

---

## 🎯 اهداف فاز A

1. ✅ **احراز هویت کاربران** با JWT
2. ✅ **ثبت‌نام** استادان و دانشجویان
3. ✅ **تأیید کاربران** توسط مدیر
4. ✅ **مدیریت CRUD کاربران**
5. ✅ **جستجو و فیلتر** کاربران
6. ✅ **تعریف دوره** توسط مدیر
7. ✅ **اختصاص استاد** به دوره (فقط یک استاد)
8. ✅ **اختصاص دانشجویان** به دوره (چند دانشجو)
9. ✅ **مشاهده اعضای دوره**

---

## 👥 نقش‌های کاربری

### 1️⃣ مدیر (ADMIN)
- تأیید/رد ثبت‌نام کاربران
- مدیریت کاربران (CRUD)
- تعریف دوره
- اختصاص استاد و دانشجویان به دوره
- مشاهده تمام اطلاعات سیستم

### 2️⃣ استاد (INSTRUCTOR)
- مشاهده دوره‌های خود
- ایجاد و مدیریت آزمون (فاز B)

### 3️⃣ دانشجو (STUDENT)
- مشاهده دوره‌های خود
- شرکت در آزمون (فاز D)

---

## 🏗️ معماری سیستم

### لایه‌های معماری (Layered Architecture)

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│    (Controllers + Thymeleaf Views)      │
├─────────────────────────────────────────┤
│         Service Layer                   │
│    (Business Logic - Interface + Impl)  │
├─────────────────────────────────────────┤
│         Repository Layer                │
│       (Spring Data JPA)                 │
├─────────────────────────────────────────┤
│         Database Layer                  │
│          (H2 Database)                  │
└─────────────────────────────────────────┘
```

---

## 📦 تکنولوژی‌های استفاده شده

### Backend
- **Spring Boot 3.2.0** - Framework اصلی
- **Spring Data JPA** - دسترسی به دیتابیس
- **Spring Security** - احراز هویت و امنیت
- **JWT (jjwt 0.12.3)** - Token-based Authentication
- **Lombok** - کاهش Boilerplate Code
- **Bean Validation (JSR-380)** - اعتبارسنجی
- **H2 Database** - دیتابیس در حافظه

### Frontend
- **Thymeleaf** - Template Engine
- **Bootstrap 5.3 RTL** - CSS Framework
- **Font Awesome 6.4** - آیکون‌ها
- **Vanilla JavaScript** - تعاملات کاربری

---

## 📁 ساختار پروژه

```
online-exam-system/
├── src/main/java/com/exammanagement/
│   ├── domain/                          # Entity ها
│   │   ├── User.java                    ✅
│   │   └── Course.java                  ✅
│   ├── dto/                             # Data Transfer Objects
│   │   ├── UserRegistrationDTO.java     ✅
│   │   ├── UserResponseDTO.java         ✅
│   │   ├── UserUpdateDTO.java           ✅
│   │   ├── LoginRequestDTO.java         ✅
│   │   ├── LoginResponseDTO.java        ✅
│   │   ├── CourseDTO.java               ✅
│   │   └── CourseResponseDTO.java       ✅
│   ├── repository/                      # Data Access Layer
│   │   ├── UserRepository.java          ✅
│   │   └── CourseRepository.java        ✅
│   ├── service/                         # Business Logic (Interface)
│   │   ├── UserService.java             ✅
│   │   └── CourseService.java           ✅
│   ├── service/impl/                    # Business Logic (Implementation)
│   │   ├── UserServiceImpl.java         ✅
│   │   └── CourseServiceImpl.java       ✅
│   ├── controller/                      # REST API Controllers
│   │   ├── AuthController.java          ✅
│   │   ├── UserController.java          ✅
│   │   └── CourseController.java        ✅
│   ├── controller/web/                  # Web Controllers (Thymeleaf)
│   │   ├── HomeController.java          ✅
│   │   ├── AdminWebController.java      ✅
│   │   ├── InstructorWebController.java ✅
│   │   └── StudentWebController.java    ✅
│   ├── security/                        # Security Components
│   │   ├── JwtTokenProvider.java        ✅
│   │   ├── JwtAuthenticationFilter.java ✅
│   │   └── CustomUserDetailsService.java✅
│   ├── config/                          # Configuration Classes
│   │   ├── SecurityConfig.java          ✅
│   │   ├── LocalizationConfig.java      ✅
│   │   └── DataInitializer.java         ✅
│   ├── exception/                       # Exception Handling
│   │   ├── ResourceNotFoundException.java    ✅
│   │   ├── UnauthorizedException.java        ✅
│   │   ├── BadRequestException.java          ✅
│   │   ├── DuplicateResourceException.java   ✅
│   │   ├── ErrorResponse.java                ✅
│   │   └── GlobalExceptionHandler.java       ✅
│   ├── enums/                           # Enumerations
│   │   ├── UserRole.java                ✅
│   │   └── UserStatus.java              ✅
│   ├── specification/                   # JPA Specifications
│   │   └── UserSpecification.java       ✅
│   └── OnlineExamSystemApplication.java ✅
│
├── src/main/resources/
│   ├── templates/                       # Thymeleaf Templates
│   │   ├── index.html                   ✅ صفحه اصلی
│   │   ├── auth/
│   │   │   ├── login.html               ✅ ورود
│   │   │   └── register.html            ✅ ثبت‌نام
│   │   ├── admin/
│   │   │   ├── dashboard.html           ✅ داشبورد مدیر
│   │   │   ├── users.html               ✅ مدیریت کاربران
│   │   │   ├── pending-users.html       ✅ کاربران در انتظار
│   │   │   ├── courses.html             ✅ مدیریت دوره‌ها
│   │   │   ├── course-form.html         ✅ فرم ایجاد دوره
│   │   │   └── course-detail.html       ✅ جزئیات دوره
│   │   ├── instructor/
│   │   │   └── dashboard.html           ✅ داشبورد استاد
│   │   └── student/
│   │       └── dashboard.html           ✅ داشبورد دانشجو
│   ├── application.properties           ✅ تنظیمات اصلی
│   ├── messages_fa.properties           ✅ پیام‌های فارسی
│   └── messages_en.properties           ✅ پیام‌های انگلیسی
│
├── pom.xml                              ✅ Maven Dependencies
└── README.md                            ✅ این فایل
```

---

## 🗄️ مدل دیتابیس (ERD)

### Entity Relationship Diagram

```
┌─────────────────────┐
│        User         │
├─────────────────────┤
│ id (PK)             │
│ username (UNIQUE)   │
│ email (UNIQUE)      │
│ password            │
│ fullName            │
│ role (ENUM)         │
│ status (ENUM)       │
│ phoneNumber         │
│ createdAt           │
│ updatedAt           │
└─────────────────────┘
         │
         │ Many-to-Many
         │
         ↓
┌─────────────────────┐          ┌──────────────────────┐
│       Course        │          │  course_instructors  │
├─────────────────────┤          ├──────────────────────┤
│ id (PK)             │◄─────────┤ course_id (FK)       │
│ title               │          │ instructor_id (FK)   │
│ courseCode (UNIQUE) │          └──────────────────────┘
│ description         │
│ startDate           │          ┌──────────────────────┐
│ endDate             │          │   course_students    │
│ createdAt           │◄─────────┤ course_id (FK)       │
└─────────────────────┘          │ student_id (FK)      │
                                 └──────────────────────┘
```

### روابط:
- **User ←→ Course (Instructors):** Many-to-Many (یک دوره یک استاد دارد، یک استاد چند دوره دارد)
- **User ←→ Course (Students):** Many-to-Many (یک دوره چند دانشجو دارد، یک دانشجو چند دوره دارد)

---

## 🔐 احراز هویت و امنیت

### 1. JWT Authentication Flow

```
1. کاربر وارد شود (POST /api/auth/login)
   ↓
2. سرور username/password را بررسی می‌کند
   ↓
3. در صورت موفقیت، JWT Token تولید می‌شود
   ↓
4. Token به کلاینت ارسال می‌شود
   ↓
5. کلاینت Token را در Header ذخیره می‌کند
   ↓
6. هر Request بعدی Token را ارسال می‌کند
   Authorization: Bearer {token}
```

### 2. دسترسی‌های نقش‌ها

| نقش | دسترسی‌ها |
|-----|----------|
| **ADMIN** | همه چیز ✅ |
| **INSTRUCTOR** | دوره‌های خود، آزمون‌های خود |
| **STUDENT** | دوره‌های خود، آزمون‌های خود |

### 3. Security Rules

```java
/api/auth/**              → Public (همه)
/admin/**                 → ADMIN only
/instructor/**            → INSTRUCTOR only
/student/**               → STUDENT only
/api/users/**             → ADMIN only (REST API)
/api/courses/** (GET)     → Authenticated
/api/courses/** (WRITE)   → ADMIN only
```

---

## 🚀 نصب و راه‌اندازی

### پیش‌نیازها:
- ✅ Java 17 یا بالاتر
- ✅ Maven 3.6+
- ✅ IDE (IntelliJ IDEA یا Eclipse)

### مراحل نصب:

#### 1. Clone کردن پروژه
```bash
git clone <repository-url>
cd online-exam-system
```

#### 2. Build کردن پروژه
```bash
mvn clean install
```

#### 3. اجرای پروژه
```bash
mvn spring-boot:run
```

#### 4. دسترسی به برنامه
- **URL اصلی:** `http://localhost:8080`
- **H2 Console:** `http://localhost:8080/h2-console`
    - JDBC URL: `jdbc:h2:mem:examdb`
    - Username: `admin`
    - Password: `admin`

---

## 👤 کاربران پیش‌فرض

سیستم با 3 کاربر پیش‌فرض راه‌اندازی می‌شود:

| نقش | Username | Password | نام کامل | وضعیت |
|-----|----------|----------|----------|--------|
| **مدیر** | `admin` | `admin123` | مدیر سیستم | APPROVED ✅ |
| **استاد** | `instructor1` | `instructor123` | دکتر احمدی | APPROVED ✅ |
| **دانشجو** | `student1` | `student123` | علی محمدی | APPROVED ✅ |

---

## 📡 API Endpoints

### 🔓 Authentication (Public)

#### ثبت‌نام
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "ali123",
  "email": "ali@example.com",
  "password": "123456",
  "fullName": "علی محمدی",
  "role": "STUDENT",
  "phoneNumber": "09121234567"
}
```

#### ورود
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "user": {
    "id": 1,
    "username": "admin",
    "role": "ADMIN",
    ...
  }
}
```

---

### 👥 User Management (ADMIN Only)

#### دریافت همه کاربران
```http
GET /api/users
Authorization: Bearer {token}
```

#### جستجو و فیلتر کاربران
```http
GET /api/users/filter?keyword=علی&role=STUDENT&status=APPROVED
Authorization: Bearer {token}
```

#### دریافت یک کاربر
```http
GET /api/users/{id}
Authorization: Bearer {token}
```

#### ویرایش کاربر
```http
PUT /api/users/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "fullName": "علی محمدی",
  "email": "ali.new@example.com",
  "phoneNumber": "09121234567",
  "role": "STUDENT"
}
```

#### تأیید کاربر
```http
PUT /api/users/{id}/approve
Authorization: Bearer {token}
```

#### رد کاربر
```http
PUT /api/users/{id}/reject
Authorization: Bearer {token}
```

#### حذف کاربر
```http
DELETE /api/users/{id}
Authorization: Bearer {token}
```

---

### 📚 Course Management

#### ایجاد دوره (ADMIN)
```http
POST /api/courses
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "ریاضیات پیشرفته",
  "courseCode": "MATH401",
  "description": "دوره ریاضیات برای کارشناسی",
  "startDate": "2024-09-01",
  "endDate": "2024-12-31"
}
```

#### دریافت همه دوره‌ها
```http
GET /api/courses
Authorization: Bearer {token}
```

#### دریافت یک دوره
```http
GET /api/courses/{id}
Authorization: Bearer {token}
```

#### ویرایش دوره (ADMIN)
```http
PUT /api/courses/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "ریاضیات پیشرفته 1",
  "courseCode": "MATH401",
  "description": "توضیحات جدید",
  "startDate": "2024-09-01",
  "endDate": "2025-01-31"
}
```

#### حذف دوره (ADMIN)
```http
DELETE /api/courses/{id}
Authorization: Bearer {token}
```

#### اضافه کردن استاد به دوره (ADMIN)
```http
POST /api/courses/{courseId}/instructors/{instructorId}
Authorization: Bearer {token}
```

#### حذف استاد از دوره (ADMIN)
```http
DELETE /api/courses/{courseId}/instructors/{instructorId}
Authorization: Bearer {token}
```

#### اضافه کردن دانشجو به دوره (ADMIN)
```http
POST /api/courses/{courseId}/students/{studentId}
Authorization: Bearer {token}
```

#### اضافه کردن چند دانشجو (ADMIN)
```http
POST /api/courses/{courseId}/students/batch
Authorization: Bearer {token}
Content-Type: application/json

[1, 2, 3, 4, 5]
```

#### حذف دانشجو از دوره (ADMIN)
```http
DELETE /api/courses/{courseId}/students/{studentId}
Authorization: Bearer {token}
```

#### دریافت استادان دوره
```http
GET /api/courses/{courseId}/instructors
Authorization: Bearer {token}
```

#### دریافت دانشجویان دوره
```http
GET /api/courses/{courseId}/students
Authorization: Bearer {token}
```

---

## 🌐 مسیرهای وب (Thymeleaf)

### عمومی
- `/` - صفحه اصلی
- `/login` - ورود
- `/register` - ثبت‌نام

### مدیر
- `/admin/dashboard` - داشبورد مدیر
- `/admin/users` - مدیریت کاربران (با فیلتر)
- `/admin/users/pending` - کاربران در انتظار تأیید
- `/admin/courses` - مدیریت دوره‌ها
- `/admin/courses/create` - فرم ایجاد دوره
- `/admin/courses/{id}` - جزئیات دوره + مدیریت اعضا

### استاد
- `/instructor/dashboard` - داشبورد استاد
- `/instructor/courses` - دوره‌های استاد

### دانشجو
- `/student/dashboard` - داشبورد دانشجو
- `/student/courses` - دوره‌های دانشجو

---

## 🎨 ویژگی‌های UI

### 1. Responsive Design
- ✅ موبایل (< 768px)
- ✅ تبلت (768px - 1024px)
- ✅ دسکتاپ (> 1024px)

### 2. طراحی
- ✅ RTL کامل برای فارسی
- ✅ گرادیانت بنفش-آبی مدرن
- ✅ کارت‌های شناور با سایه
- ✅ انیمیشن Hover
- ✅ Modal های زیبا

### 3. تعاملات
- ✅ جستجوی زنده (Live Search)
- ✅ فیلتر کاربران (نقش + وضعیت)
- ✅ Alert های رنگی
- ✅ Confirmation Dialog برای حذف
- ✅ Auto-refresh بعد از عملیات

---

## 🔍 جستجو و فیلتر

### جستجوی کاربران
استفاده از **JPA Specification** برای جستجوی پیشرفته:

```java
UserSpecification.searchUsers(keyword, role, status)
```

**فیلترهای موجود:**
- ✅ کلمه کلیدی (نام، ایمیل، نام کاربری)
- ✅ نقش (ADMIN, INSTRUCTOR, STUDENT)
- ✅ وضعیت (PENDING, APPROVED, REJECTED)

**مثال:**
```
/admin/users?keyword=علی&role=STUDENT&status=APPROVED
```

---

## 🐛 مدیریت خطاها

### Exception Hierarchy
```
RuntimeException
├── ResourceNotFoundException (404)
├── UnauthorizedException (401)
├── BadRequestException (400)
└── DuplicateResourceException (409)
```

### Global Exception Handler
تمام خطاها با `GlobalExceptionHandler` مدیریت می‌شوند و JSON زیر برمی‌گردد:

```json
{
  "timestamp": "2024-12-24T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "کاربر یافت نشد",
  "path": "/api/users/999",
  "validationErrors": null
}
```

---

## 🧪 تست‌ها

### تست با Postman

1. **Login**
```
POST http://localhost:8080/api/auth/login
Body: {"username": "admin", "password": "admin123"}
```

2. **کپی Token از Response**

3. **تست API ها با Token**
```
GET http://localhost:8080/api/users
Headers: Authorization: Bearer {token}
```

### تست UI

1. برو به `http://localhost:8080/login`
2. Login با: `admin / admin123`
3. مسیرهای مختلف را تست کن

---

## 📊 جریان کار (Workflow)

### ثبت‌نام و تأیید کاربر

```
کاربر ثبت‌نام می‌کند (استاد/دانشجو)
    ↓
وضعیت: PENDING
    ↓
مدیر وارد می‌شود
    ↓
/admin/users/pending
    ↓
کلیک روی "تأیید"
    ↓
وضعیت: APPROVED ✅
    ↓
کاربر می‌تواند لاگین کند
```

### ایجاد دوره و اختصاص اعضا

```
مدیر ایجاد دوره می‌کند
    ↓
/admin/courses/create
    ↓
وارد کردن اطلاعات دوره
    ↓
ذخیره دوره
    ↓
/admin/courses/{id}
    ↓
اضافه کردن یک استاد
    ↓
اضافه کردن چند دانشجو
    ↓
مشاهده لیست اعضا ✅
```

---

## 💡 نکات مهم

### 1. امنیت
- ⚠️ **هرگز** رمز عبور را در Log قرار ندهید
- ⚠️ JWT Secret را در production تغییر دهید
- ⚠️ HTTPS را در production فعال کنید

### 2. دیتابیس
- ⚠️ H2 فقط برای Development است
- ⚠️ در Production از MySQL/PostgreSQL استفاده کنید
- ⚠️ `ddl-auto=create-drop` را در production تغییر دهید به `validate`

### 3. Validation
- ✅ تمام DTO ها دارای Bean Validation هستند
- ✅ Service Layer نیز Validation دارد
- ✅ Exception ها به صورت مناسب handle می‌شوند

### 4. Performance
- ✅ استفاده از `@Transactional(readOnly = true)` برای Query ها
- ✅ Lazy Loading برای روابط Many-to-Many
- ✅ Pagination (قابل اضافه شدن)

## 📝 لاگ‌ها

### فعال‌سازی Debug Logging

در `application.properties`:
```properties
logging.level.com.exammanagement=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### مشاهده لاگ‌ها
```bash
tail -f logs/application.log
```

---

## 🤝 مشارکت

1. Fork کردن پروژه
2. ایجاد Branch جدید (`git checkout -b feature/AmazingFeature`)
3. Commit تغییرات (`git commit -m 'Add some AmazingFeature'`)
4. Push به Branch (`git push origin feature/AmazingFeature`)
5. ایجاد Pull Request

---
### مشکلات رایج:

**1. خطای 401 Unauthorized**
- بررسی کنید Token صحیح ارسال شده
- مطمئن شوید کاربر APPROVED است

**2. خطای 403 Forbidden**
- بررسی کنید نقش کاربر مناسب است
- مطمئن شوید `@PreAuthorize` صحیح است

**3. خطای در Login**
- Password صحیح است؟
- کاربر APPROVED است؟
- دیتابیس راه‌اندازی شده؟

---

## ✨ ویژگی‌های برجسته

- ✅ **Architecture:** Clean & Layered
- ✅ **Security:** JWT-based Authentication
- ✅ **UI:** Responsive & Modern
- ✅ **Code Quality:** SOLID Principles
- ✅ **Documentation:** کامل و جامع
- ✅ **i18n:** پشتیبانی چند زبانه
- ✅ **Exception Handling:** Global & Standardized

---

## 🎓 یادگیری

این پروژه مناسب برای:
- یادگیری Spring Boot
- یادگیری Spring Security
- یادگیری JWT Authentication
- یادگیری Thymeleaf
- یادگیری RESTful API Design
- یادگیری JPA Relationships
- یادگیری Exception Handling

---

## 📚 منابع

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/)
- [Spring Security JWT](https://jwt.io/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/)
- [Bootstrap RTL](https://getbootstrap.com/)



---
# فاز B - مدیریت آزمون توسط استاد

## 📋 خلاصه فاز B

این فاز شامل قابلیت **تعریف، ویرایش و حذف آزمون** توسط استاد می‌باشد.

---

## ✅ موارد پیاده‌سازی شده

### 1️⃣ **Backend (Java + Spring Boot)**

#### 📦 Entity
- `Exam.java` - موجودیت آزمون با رابطه به Course و User

#### 📊 DTO
- `ExamDTO.java` - برای ایجاد آزمون
- `ExamResponseDTO.java` - برای نمایش اطلاعات آزمون
- `ExamUpdateDTO.java` - برای ویرایش آزمون

#### 🗄️ Repository
- `ExamRepository.java` - شامل Query های سفارشی:
    - `findByCourseId` - آزمون‌های یک دوره
    - `findByCreatedById` - آزمون‌های یک استاد
    - `findByCourseIdAndInstructorId` - آزمون‌های استاد در یک دوره خاص
    - `countByCourseId` - تعداد آزمون‌های یک دوره

#### 🔧 Service
- **Interface:** `ExamService.java`
- **Implementation:** `ExamServiceImpl.java`
- **متدها:**
    - `createExam()` - ایجاد آزمون جدید
    - `getExamById()` - دریافت یک آزمون
    - `getExamsByCourse()` - لیست آزمون‌های یک دوره
    - `getExamsByInstructor()` - لیست آزمون‌های یک استاد
    - `updateExam()` - ویرایش آزمون
    - `deleteExam()` - حذف آزمون

#### 🌐 REST Controller
- `ExamController.java` - API های کامل:
    - `POST /api/exams` - ایجاد آزمون
    - `GET /api/exams/{id}` - دریافت یک آزمون
    - `GET /api/exams/course/{courseId}` - آزمون‌های دوره
    - `GET /api/exams/my-exams` - آزمون‌های استاد فعلی
    - `PUT /api/exams/{id}` - ویرایش آزمون
    - `DELETE /api/exams/{id}` - حذف آزمون

---

### 2️⃣ **Frontend (Thymeleaf + Bootstrap)**

#### 📄 Web Controller
- `InstructorWebController.java` - کنترلر صفحات استاد

#### 🎨 صفحات Thymeleaf
1. **`instructor/dashboard.html`**
    - نمایش آمار دوره‌ها و آزمون‌ها
    - دسترسی سریع به بخش‌های مختلف

2. **`instructor/courses.html`**
    - لیست دوره‌های استاد
    - دکمه ورود به آزمون‌های هر دوره

3. **`instructor/course-exams.html`** ⭐ (اصلی‌ترین صفحه)
    - نمایش اطلاعات دوره
    - لیست آزمون‌های دوره
    - دکمه ایجاد آزمون جدید
    - Modal ایجاد آزمون با فیلدها:
        - عنوان آزمون (الزامی)
        - توضیحات (اختیاری)
        - مدت زمان به دقیقه (الزامی)
    - Modal ویرایش آزمون
    - دکمه حذف آزمون

4. **`instructor/exams.html`**
    - لیست تمام آزمون‌های استاد در تمام دوره‌ها

---

## 🔐 امنیت و دسترسی

### بررسی‌های امنیتی:
1. ✅ **فقط استاد** می‌تواند آزمون ایجاد کند
2. ✅ استاد فقط می‌تواند برای **دوره‌های خودش** آزمون بسازد
3. ✅ استاد فقط می‌تواند **آزمون‌های خودش** را ویرایش/حذف کند
4. ✅ استفاده از `@PreAuthorize` در Controller ها
5. ✅ Validation کامل در Service Layer

---

## 📁 ساختار فایل‌ها

```
src/main/java/com/exammanagement/
├── domain/
│   └── Exam.java                      ✅ جدید
├── dto/
│   ├── ExamDTO.java                   ✅ جدید
│   ├── ExamResponseDTO.java           ✅ جدید
│   └── ExamUpdateDTO.java             ✅ جدید
├── repository/
│   └── ExamRepository.java            ✅ جدید
├── service/
│   ├── ExamService.java               ✅ جدید
│   └── impl/
│       └── ExamServiceImpl.java       ✅ جدید
├── controller/
│   ├── ExamController.java            ✅ جدید (REST API)
│   └── web/
│       └── InstructorWebController.java  ✅ آپدیت شده

src/main/resources/templates/instructor/
├── dashboard.html                     ✅ جدید
├── courses.html                       ✅ جدید
├── course-exams.html                  ✅ جدید (اصلی)
└── exams.html                         ✅ جدید
```

---

## 🚀 نحوه استفاده

### 1. اضافه کردن فایل‌ها
کپی کردن تمام کدها در مسیرهای صحیح

### 2. اجرای پروژه
```bash
mvn clean install
mvn spring-boot:run
```

### 3. ورود به سیستم
- URL: `http://localhost:8080/login`
- Username: `instructor1`
- Password: `instructor123`

### 4. مسیرهای دسترسی استاد
- **داشبورد:** `/instructor/dashboard`
- **دوره‌ها:** `/instructor/courses`
- **آزمون‌های یک دوره:** `/instructor/courses/{courseId}/exams`
- **همه آزمون‌ها:** `/instructor/exams`

---

## 🧪 تست API ها

### ایجاد آزمون جدید
```bash
POST /api/exams
Content-Type: application/json
Authorization: Bearer {token}

{
  "courseId": 1,
  "title": "آزمون میان‌ترم",
  "description": "آزمون فصل 1 تا 5",
  "durationMinutes": 90
}
```

### دریافت آزمون‌های یک دوره
```bash
GET /api/exams/course/1
Authorization: Bearer {token}
```

### ویرایش آزمون
```bash
PUT /api/exams/1
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "آزمون میان‌ترم (اصلاح شده)",
  "description": "آزمون فصل 1 تا 6",
  "durationMinutes": 120
}
```

### حذف آزمون
```bash
DELETE /api/exams/1
Authorization: Bearer {token}
```

---

## 🎨 ویژگی‌های UI

1. ✅ **Responsive Design** - سازگار با موبایل، تبلت و دسکتاپ
2. ✅ **Modal های زیبا** - برای ایجاد و ویرایش
3. ✅ **Alert های رنگی** - پیام‌های موفقیت/خطا
4. ✅ **آیکون‌های Font Awesome** - UI جذاب
5. ✅ **گرادیانت بنفش** - طراحی مدرن
6. ✅ **RTL کامل** - پشتیبانی فارسی

---

## 🔄 جریان کار (Workflow)

```
استاد وارد می‌شود
    ↓
مشاهده داشبورد (لیست دوره‌ها و آزمون‌ها)
    ↓
انتخاب یک دوره
    ↓
مشاهده لیست آزمون‌های آن دوره
    ↓
کلیک روی "ایجاد آزمون جدید"
    ↓
وارد کردن: عنوان، توضیحات، مدت زمان
    ↓
ذخیره آزمون
    ↓
لیست به‌روزرسانی می‌شود ✅
    ↓
امکان ویرایش/حذف آزمون‌ها
```

---

## 📊 نمونه داده

### دوره نمونه:
```
عنوان: ریاضیات پیشرفته
کد: MATH401
استاد: دکتر احمدی
```

### آزمون‌های نمونه:
```
1. آزمون میان‌ترم - 90 دقیقه
2. آزمون پایان‌ترم - 120 دقیقه
3. کوییز فصل 1 - 30 دقیقه
```

---

## 🐛 مدیریت خطاها

تمام خطاها با `GlobalExceptionHandler` مدیریت می‌شوند:
- `ResourceNotFoundException` - 404
- `UnauthorizedException` - 401
- `BadRequestException` - 400
- `DuplicateResourceException` - 409

---

## 📝 نکات مهم

1. ⚠️ **یک استاد فقط می‌تواند برای دوره‌های خودش آزمون بسازد**
2. ⚠️ **آزمون فقط توسط سازنده‌اش قابل ویرایش/حذف است**
3. ⚠️ **مدت زمان آزمون باید حداقل 1 دقیقه باشد**
4. ✅ **تمام فیلدهای الزامی باید پر شوند**
5. ✅ **پس از هر عملیات، صفحه به‌روزرسانی می‌شود**
## Smart Attendance System (PWA + Spring Boot)

### Prerequisites
- Java 17+
- Maven 3.9+
- Any static server (VS Code Live Server, or `npx serve`)

### 1) Run the Backend (Spring Boot)
```
cd backend
mvn spring-boot:run
```
- API base: `http://localhost:8080`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:attendance`, user: `sa`, password: blank)

Default users:
- Admin — username: `admin`, password: `admin123`
- Lecturer — username: `lecturer`, password: `lect123`
- Student — username: `student`, password: `stud123`

### 2) Serve the Frontend (PWA)
Open the `frontend/` folder with a static server. Examples:

- VS Code Live Server: Open `frontend/index.html` and click "Go Live".
- Node serve:
```
npx serve frontend -l 5173
```
Then visit `http://localhost:5173/index.html`.

Service Worker & Install:
- The app includes `manifest.json` and `service-worker.js` for installability and offline shell caching.

### 3) Using the App
1. Login with one of the default users.
2. Admin can create courses (provide lecturer ID, e.g., `2`).
3. Lecturer selects a course, generates a QR, and sees live attendance update after scans.
4. Student enters course ID, their user ID (e.g., `3`), and QR token to submit attendance.

### API (High-Level)
- Auth: `POST /api/auth/register`, `POST /api/auth/login`
- Courses: `GET /api/courses`, `POST /api/courses`, `GET /api/courses/{id}`
- Attendance: `POST /api/attendance/qr`, `POST /api/attendance/scan`, `POST /api/attendance/face`, `PUT /api/attendance/manual`
- Reports: `GET /api/reports/student/{studentId}`, `GET /api/reports/course/{courseId}`

### Notes
- This demo uses H2 in-memory DB and stubbed QR/face flows. Replace with persistent DB and provider integrations for production.


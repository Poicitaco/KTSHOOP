# OOPSH UI Redesign Plan

## 🎯 Mục tiêu

Xóa toàn bộ giao diện hiện tại và thiết kế lại UI từ đầu với JavaFX thuần túy.

## ✅ Backend Status

- **100% HOÀN THÀNH** - Tất cả chức năng backend đã được phát triển đầy đủ
- Models, DAOs, Controllers, Utils đều hoạt động tốt
- Data persistence với XML hoạt động ổn định
- Authentication và Session management hoàn chỉnh

## 🗂️ Files cần XÓA (UI/FXML files)

### 1. Main Layout Files

- [ ] `src/main/resources/com/pocitaco/oopsh/main-layout.fxml`
- [ ] `src/main/resources/com/pocitaco/oopsh/login.fxml`

### 2. Admin UI Files

- [ ] `src/main/resources/com/pocitaco/oopsh/admin/*.fxml` (tất cả)
  - audit-logs.fxml
  - backup-restore.fxml
  - dashboard-overview.fxml
  - dashboard-stats.fxml
  - exam-create.fxml
  - exam-edit.fxml
  - exam-management.fxml
  - exam-results.fxml
  - exam-schedule.fxml
  - exam-schedules.fxml
  - exam-types.fxml
  - performance-report.fxml
  - permission-create.fxml
  - permission-edit.fxml
  - revenue-report.fxml
  - statistics-report.fxml
  - user-create.fxml
  - user-edit.fxml
  - user-management.fxml

### 3. Candidate UI Files

- [ ] `src/main/resources/com/pocitaco/oopsh/candidate/*.fxml` (tất cả)
  - available-exams.fxml
  - current-session.fxml
  - exam-info.fxml
  - my-certificates.fxml
  - my-registrations.fxml
  - payment-history.fxml
  - profile.fxml
  - register-exam.fxml
  - session-history.fxml

### 4. Examiner UI Files

- [ ] `src/main/resources/com/pocitaco/oopsh/examiner/*.fxml` (tất cả)
  - grading-history.fxml
  - session-management.fxml
  - today-schedule.fxml

### 5. CSS Files (giữ lại để tham khảo)

- [ ] `src/main/resources/com/pocitaco/oopsh/styles/*.css` (backup, không xóa)

## 🏗️ Files cần GIỮ LẠI (Backend)

### ✅ Giữ nguyên hoàn toàn:

- `src/main/java/com/pocitaco/oopsh/models/*` - Tất cả model classes
- `src/main/java/com/pocitaco/oopsh/dao/*` - Tất cả DAO classes
- `src/main/java/com/pocitaco/oopsh/enums/*` - Tất cả enum classes
- `src/main/java/com/pocitaco/oopsh/interfaces/*` - Tất cả interfaces
- `src/main/java/com/pocitaco/oopsh/utils/*` - Utility classes
- `data/*.xml` - Tất cả data files
- `pom.xml` - Maven configuration

### 🔄 Files cần SỬA:

- `src/main/java/com/pocitaco/oopsh/App.java` - Entry point, cần thiết kế lại UI bootstrap
- `src/main/java/com/pocitaco/oopsh/controllers/*` - Một số controller có thể cần điều chỉnh

## 📋 Kế hoạch UI mới

### Phase 1: Clean Up (Hiện tại)

1. ✅ Backup toàn bộ project
2. ❌ Xóa tất cả file FXML
3. ❌ Xóa navigation strategy classes (không cần thiết cho UI mới)
4. ❌ Clean up MainLayoutController

### Phase 2: New UI Architecture

1. 🎨 Thiết kế UI với Java code (không dùng FXML)
2. 🏗️ Tạo modern component-based architecture
3. 🎯 Responsive design với JavaFX controls
4. 🌟 Clean và intuitive user experience

### Phase 3: Implementation

1. 🔐 Login screen mới
2. 📊 Dashboard layouts cho từng role
3. 📋 CRUD forms cho các chức năng
4. 📈 Reports và statistics views
5. ⚙️ Settings và admin panels

## 🛠️ Technical Stack cho UI mới

- **JavaFX** - Pure Java code, không FXML
- **CSS** - Material Design inspired styling
- **Component Architecture** - Reusable UI components
- **MVC Pattern** - Maintain separation of concerns
- **Responsive** - Adaptive layouts

## 🎯 Next Steps

1. Confirm backend functionality testing
2. Remove old UI files
3. Start new UI development
4. Progressive enhancement approach

---

**Note**: Backend is 100% ready - focus purely on UI development from here.

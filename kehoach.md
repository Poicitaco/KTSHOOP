# KẾ HOẠCH CHI TIẾT: HỆ THỐNG SÁT HẠCH BẰNG LÁI XE

## 1. TỔNG QUAN DỰ ÁN

### 1.1 Mục tiêu

Xây dựng hệ thống quản lý sát hạch bằng lái xe với 3 role chính:

- **Admin**: Quản trị viên hệ thống
- **Giám thị**: Cán bộ coi thi và chấm điểm
- **Thí sinh**: Người đăng ký và tham gia thi

### 1.2 Công nghệ sử dụng

- **JDK**: Version 23
- **IDE**: Apache NetBeans 23
- **Framework**: JavaFX với FXML
- **Database**: XML files
- **Build tool**: Maven

---

## 2. PHÂN TÍCH HỆ THỐNG

### 2.1 Chức năng theo Role

#### 2.1.1 ADMIN (Quản trị viên)

- **Quản lý người dùng**:

  - Tạo tài khoản giám thị
  - Quản lý thông tin thí sinh
  - Phân quyền người dùng

- **Quản lý kỳ thi**:

  - Tạo/sửa/xóa kỳ thi
  - Thiết lập lịch thi
  - Phân công giám thị cho từng ca thi

- **Quản lý hạng bằng lái**:

  - A1, A2, A3, A4 (xe máy)
  - B1, B2 (ô tô)
  - C, D, E, F (xe tải, xe khách)

- **Thống kê và báo cáo**:

  - Tổng số thí sinh đăng ký
  - Tỷ lệ đậu/rớt theo từng hạng bằng
  - Doanh thu từ phí thi
  - Thống kê theo thời gian

- **Cấu hình hệ thống**:
  - Thiết lập phí thi
  - Cấu hình điểm đậu
  - Quản lý câu hỏi thi

#### 2.1.2 GIÁM THỊ (Examiner)

- **Đăng nhập hệ thống**:

  - Xác thực bằng tài khoản riêng
  - Xem thông tin cá nhân

- **Quản lý ca thi**:

  - Xem lịch làm việc được phân công
  - Danh sách thí sinh trong ca thi
  - Thông tin phòng thi

- **Chấm thi**:

  - Nhập điểm thi lý thuyết
  - Nhập điểm thi thực hành
  - Ghi chú đánh giá

- **Báo cáo**:
  - Báo cáo kết quả ca thi
  - Thống kê thí sinh đậu/rớt

#### 2.1.3 THÍ SINH (Candidate)

- **Đăng ký tài khoản**:

  - Tạo tài khoản mới
  - Cập nhật thông tin cá nhân
  - Upload ảnh đại diện

- **Đăng ký thi**:

  - Chọn hạng bằng lái muốn thi
  - Chọn ngày/ca thi
  - Thanh toán phí thi

- **Thi cử**:

  - Xem thông tin kỳ thi
  - Tham gia thi (nếu có thi online)
  - Xem lịch thi của mình

- **Kết quả**:
  - Xem điểm thi
  - Xem kết quả đậu/rớt
  - In giấy chứng nhận (nếu đậu)

### 2.2 Cấu trúc dữ liệu XML

#### 2.2.1 File `users.xml` - Quản lý tài khoản

```xml
<users>
    <user>
        <id>1</id>
        <username>admin</username>
        <password>admin123</password>
        <role>ADMIN</role>
        <fullName>Quản trị viên</fullName>
        <email>admin@satheach.com</email>
        <createdDate>2025-07-31</createdDate>
        <status>ACTIVE</status>
    </user>
    <user>
        <id>2</id>
        <username>giamthi001</username>
        <password>gt123456</password>
        <role>EXAMINER</role>
        <fullName>Nguyễn Văn A</fullName>
        <email>giamthi001@satheach.com</email>
        <employeeId>GT001</employeeId>
        <experience>5</experience>
        <createdDate>2025-07-31</createdDate>
        <status>ACTIVE</status>
    </user>
    <user>
        <id>3</id>
        <username>thisinh001</username>
        <password>ts123456</password>
        <role>CANDIDATE</role>
        <fullName>Trần Thị B</fullName>
        <email>thisinh001@email.com</email>
        <cccd>123456789012</cccd>
        <birthday>1995-01-15</birthday>
        <phone>0901234567</phone>
        <address>Hà Nội</address>
        <createdDate>2025-07-31</createdDate>
        <status>ACTIVE</status>
    </user>
</users>
```

#### 2.2.2 File `examtypes.xml` - Loại bằng lái

```xml
<examTypes>
    <examType>
        <id>1</id>
        <code>A1</code>
        <name>Bằng lái xe máy A1</name>
        <description>Xe mô tô 2 bánh có dung tích xi lanh đến 175cm3</description>
        <fee>200000</fee>
        <theoryPassScore>21</theoryPassScore>
        <practicePassScore>80</practicePassScore>
        <status>ACTIVE</status>
    </examType>
    <examType>
        <id>2</id>
        <code>B1</code>
        <name>Bằng lái xe ô tô B1</name>
        <description>Xe ô tô không quá 9 chỗ ngồi</description>
        <fee>500000</fee>
        <theoryPassScore>21</theoryPassScore>
        <practicePassScore>80</practicePassScore>
        <status>ACTIVE</status>
    </examType>
</examTypes>
```

#### 2.2.3 File `exams.xml` - Kỳ thi

```xml
<exams>
    <exam>
        <id>1</id>
        <examCode>KT2025001</examCode>
        <examTypeId>1</examTypeId>
        <examName>Kỳ thi sát hạch A1 - Tháng 8/2025</examName>
        <startDate>2025-08-15</startDate>
        <endDate>2025-08-20</endDate>
        <registrationStartDate>2025-07-31</registrationStartDate>
        <registrationEndDate>2025-08-10</registrationEndDate>
        <maxCandidates>100</maxCandidates>
        <currentCandidates>0</currentCandidates>
        <status>REGISTRATION_OPEN</status>
        <note>Kỳ thi đầu tiên trong tháng 8</note>
        <createdBy>1</createdBy>
        <createdDate>2025-07-31</createdDate>
    </exam>
</exams>
```

#### 2.2.4 File `examschedules.xml` - Lịch thi

```xml
<examSchedules>
    <schedule>
        <id>1</id>
        <examId>1</examId>
        <examDate>2025-08-15</examDate>
        <timeSlot>MORNING</timeSlot>
        <startTime>08:00</startTime>
        <endTime>10:00</endTime>
        <examType>THEORY</examType>
        <room>Phòng 101</room>
        <maxCandidates>30</maxCandidates>
        <currentCandidates>0</currentCandidates>
        <examinerId>2</examinerId>
        <status>SCHEDULED</status>
    </schedule>
    <schedule>
        <id>2</id>
        <examId>1</examId>
        <examDate>2025-08-15</examDate>
        <timeSlot>AFTERNOON</timeSlot>
        <startTime>14:00</startTime>
        <endTime>16:00</endTime>
        <examType>PRACTICE</examType>
        <room>Sân tập lái A</room>
        <maxCandidates>10</maxCandidates>
        <currentCandidates>0</currentCandidates>
        <examinerId>2</examinerId>
        <status>SCHEDULED</status>
    </schedule>
</examSchedules>
```

#### 2.2.5 File `registrations.xml` - Đăng ký thi

```xml
<registrations>
    <registration>
        <id>1</id>
        <candidateId>3</candidateId>
        <examId>1</examId>
        <scheduleId>1</scheduleId>
        <registrationDate>2025-07-31</registrationDate>
        <paymentStatus>PAID</paymentStatus>
        <paymentDate>2025-07-31</paymentDate>
        <paymentAmount>200000</paymentAmount>
        <status>REGISTERED</status>
        <note>Đăng ký thành công</note>
    </registration>
</registrations>
```

#### 2.2.6 File `results.xml` - Kết quả thi

```xml
<results>
    <result>
        <id>1</id>
        <registrationId>1</registrationId>
        <candidateId>3</candidateId>
        <examId>1</examId>
        <scheduleId>1</scheduleId>
        <examType>THEORY</examType>
        <score>25</score>
        <totalQuestions>30</totalQuestions>
        <correctAnswers>25</correctAnswers>
        <examDate>2025-08-15</examDate>
        <examinerId>2</examinerId>
        <status>PASSED</status>
        <note>Hoàn thành tốt</note>
        <gradedDate>2025-08-15</gradedDate>
    </result>
</results>
```

---

## 3. THIẾT KẾ GIAO DIỆN

### 3.1 Màn hình đăng nhập

- Form login với username/password
- Dropdown chọn role (Admin/Giám thị/Thí sinh)
- Nút đăng ký cho thí sinh mới
- Quên mật khẩu

### 3.2 Dashboard theo Role

#### 3.2.1 Admin Dashboard

- **Thống kê tổng quan**:

  - Số kỳ thi đang mở đăng ký
  - Tổng số thí sinh đã đăng ký
  - Doanh thu tháng hiện tại
  - Tỷ lệ đậu/rớt

- **Menu chính**:
  - Quản lý kỳ thi
  - Quản lý người dùng
  - Quản lý lịch thi
  - Thống kê báo cáo
  - Cấu hình hệ thống

#### 3.2.2 Examiner Dashboard

- **Lịch làm việc hôm nay**
- **Danh sách ca thi được phân công**
- **Thống kê cá nhân**:
  - Số ca thi đã coi
  - Số thí sinh đã chấm
  - Tỷ lệ đậu/rớt của ca thi

#### 3.2.3 Candidate Dashboard

- **Thông tin cá nhân**
- **Danh sách kỳ thi có thể đăng ký**
- **Lịch thi của tôi**
- **Kết quả thi**
- **Giấy chứng nhận**

### 3.3 Các màn hình chức năng

#### 3.3.1 Quản lý kỳ thi (Admin)

- TableView hiển thị danh sách kỳ thi
- Form thêm/sửa kỳ thi
- ComboBox chọn loại bằng lái
- DatePicker cho ngày thi
- Validation dữ liệu đầu vào

#### 3.3.2 Đăng ký thi (Candidate)

- ComboBox chọn loại bằng lái
- TableView hiển thị các kỳ thi available
- Form đăng ký với thông tin cá nhân
- Thanh toán phí thi
- Xác nhận đăng ký

#### 3.3.3 Chấm thi (Examiner)

- TableView danh sách thí sinh trong ca thi
- Form nhập điểm cho từng thí sinh
- Dropdown kết quả Đậu/Rớt
- TextArea ghi chú đánh giá

---

## 4. THIẾT KẾ CLASSES

### 4.1 Model Classes

#### 4.1.1 User Management

```java
// User.java
public class User {
    private int id;
    private String username;
    private String password;
    private UserRole role;
    private String fullName;
    private String email;
    private LocalDate createdDate;
    private UserStatus status;

    // Specific fields for each role
    private String employeeId; // For Examiner
    private int experience; // For Examiner
    private String cccd; // For Candidate
    private LocalDate birthday; // For Candidate
    private String phone; // For Candidate
    private String address; // For Candidate
}

// UserRole.java (Enum)
public enum UserRole {
    ADMIN, EXAMINER, CANDIDATE
}

// UserStatus.java (Enum)
public enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}
```

#### 4.1.2 Exam Management

```java
// ExamType.java
public class ExamType {
    private int id;
    private String code;
    private String name;
    private String description;
    private double fee;
    private int theoryPassScore;
    private int practicePassScore;
    private ExamTypeStatus status;
}

// Exam.java
public class Exam {
    private int id;
    private String examCode;
    private int examTypeId;
    private String examName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private int maxCandidates;
    private int currentCandidates;
    private ExamStatus status;
    private String note;
    private int createdBy;
    private LocalDate createdDate;
}

// ExamSchedule.java
public class ExamSchedule {
    private int id;
    private int examId;
    private LocalDate examDate;
    private TimeSlot timeSlot;
    private LocalTime startTime;
    private LocalTime endTime;
    private ExamSubType examType;
    private String room;
    private int maxCandidates;
    private int currentCandidates;
    private int examinerId;
    private ScheduleStatus status;
}
```

#### 4.1.3 Registration & Results

```java
// Registration.java
public class Registration {
    private int id;
    private int candidateId;
    private int examId;
    private int scheduleId;
    private LocalDate registrationDate;
    private PaymentStatus paymentStatus;
    private LocalDate paymentDate;
    private double paymentAmount;
    private RegistrationStatus status;
    private String note;
}

// Result.java
public class Result {
    private int id;
    private int registrationId;
    private int candidateId;
    private int examId;
    private int scheduleId;
    private ExamSubType examType;
    private int score;
    private int totalQuestions;
    private int correctAnswers;
    private LocalDate examDate;
    private int examinerId;
    private ResultStatus status;
    private String note;
    private LocalDateTime gradedDate;
}
```

### 4.2 DAO Classes

```java
// UserDAO.java
public class UserDAO {
    public List<User> getAllUsers();
    public User getUserById(int id);
    public User getUserByUsername(String username);
    public void addUser(User user);
    public void updateUser(User user);
    public void deleteUser(int id);
    public List<User> getUsersByRole(UserRole role);
    public boolean authenticateUser(String username, String password, UserRole role);
}

// ExamDAO.java
public class ExamDAO {
    public List<Exam> getAllExams();
    public List<Exam> getActiveExams();
    public List<Exam> getExamsByStatus(ExamStatus status);
    public void addExam(Exam exam);
    public void updateExam(Exam exam);
    public void deleteExam(int id);
    public List<Exam> searchExams(String keyword);
}
```

### 4.3 Controller Classes

```java
// LoginController.java
public class LoginController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<UserRole> cboRole;

    @FXML
    private void handleLogin(ActionEvent event);

    @FXML
    private void handleRegister(ActionEvent event);
}

// AdminDashboardController.java
public class AdminDashboardController {
    @FXML private Label lblTotalExams;
    @FXML private Label lblTotalCandidates;
    @FXML private Label lblMonthlyRevenue;
    @FXML private TableView<Exam> tableRecentExams;

    @FXML
    private void initialize();

    @FXML
    private void handleManageExams(ActionEvent event);
}
```

---

## 5. KẾ HOẠCH TRIỂN KHAI

### 5.1 Giai đoạn 1: Thiết lập dự án (2 ngày)

**Ngày 1:**

- [ ] Tạo Maven project với JavaFX
- [ ] Cấu hình dependencies trong pom.xml
- [ ] Thiết lập cấu trúc package
- [ ] Tạo các file XML mẫu

**Ngày 2:**

- [ ] Tạo các enum classes (UserRole, ExamStatus, etc.)
- [ ] Tạo các model classes cơ bản
- [ ] Test đọc/ghi XML cơ bản

### 5.2 Giai đoạn 2: Authentication & User Management (3 ngày)

**Ngày 3:**

- [ ] Implement UserDAO
- [ ] Tạo màn hình đăng nhập
- [ ] Implement authentication logic

**Ngày 4:**

- [ ] Tạo dashboard cho 3 role
- [ ] Implement session management
- [ ] Tạo menu navigation

**Ngày 5:**

- [ ] User management cho Admin
- [ ] Validation và error handling
- [ ] Test authentication flow

### 5.3 Giai đoạn 3: Exam Management (4 ngày)

**Ngày 6:**

- [ ] Implement ExamDAO, ExamTypeDAO
- [ ] Quản lý loại bằng lái (Admin)
- [ ] CRUD operations cho exam types

**Ngày 7:**

- [ ] Quản lý kỳ thi (Admin)
- [ ] Form tạo/sửa kỳ thi
- [ ] Validation business rules

**Ngày 8:**

- [ ] Quản lý lịch thi
- [ ] Phân công giám thị
- [ ] Schedule management

**Ngày 9:**

- [ ] Integration testing
- [ ] UI improvements
- [ ] Bug fixes

### 5.4 Giai đoạn 4: Registration & Results (4 ngày)

**Ngày 10:**

- [ ] Đăng ký thi cho thí sinh
- [ ] Payment simulation
- [ ] Registration validation

**Ngày 11:**

- [ ] Chấm thi cho giám thị
- [ ] Results management
- [ ] Score calculation

**Ngày 12:**

- [ ] Xem kết quả cho thí sinh
- [ ] Certificate generation
- [ ] Result statistics

**Ngày 13:**

- [ ] Integration testing
- [ ] Performance optimization
- [ ] UI/UX improvements

### 5.5 Giai đoạn 5: Search & Statistics (3 ngày)

**Ngày 14:**

- [ ] Tìm kiếm đa tiêu chí
- [ ] Search by string (fuzzy matching)
- [ ] Search by number range

**Ngày 15:**

- [ ] Thống kê và báo cáo
- [ ] Charts và graphs
- [ ] Export functionality

**Ngày 16:**

- [ ] Advanced statistics
- [ ] Performance reports
- [ ] Data visualization

### 5.6 Giai đoạn 6: Finalization (3 ngày)

**Ngày 17:**

- [ ] Complete testing
- [ ] Bug fixes
- [ ] Code refactoring

**Ngày 18:**

- [ ] Build executable JAR
- [ ] Create installer
- [ ] Write README documentation

**Ngày 19:**

- [ ] Final testing on different machines
- [ ] Prepare submission package
- [ ] Create presentation materials

---

## 6. YÊU CẦU KỸ THUẬT

### 6.1 Validation Rules

- **Username**: Unique, 4-20 characters
- **Password**: Minimum 6 characters
- **CCCD**: 12 digits, unique for candidates
- **Phone**: Valid Vietnamese phone format
- **Email**: Valid email format
- **Exam dates**: Start date < End date
- **Scores**: 0-30 for theory, 0-100 for practice

### 6.2 Business Rules

- Thí sinh chỉ có thể đăng ký thi khi trong thời gian mở đăng ký
- Phải thanh toán phí thi trước khi được xác nhận đăng ký
- Giám thị không thể chấm điểm cho ca thi không được phân công
- Admin có thể override mọi business rule
- Điểm đậu: Lý thuyết >= 21/30, Thực hành >= 80/100

### 6.3 Data Format

- **Tiền tệ**: 1,000,000 VND (NumberFormat)
- **Ngày tháng**: dd/MM/yyyy
- **Giờ**: HH:mm
- **ID**: Auto-increment integer

### 6.4 Error Handling

- Input validation với clear error messages
- Duplicate data prevention
- Concurrent access handling
- Graceful degradation

---

## 7. TESTING PLAN

### 7.1 Unit Testing

- Test các DAO methods
- Test validation logic
- Test business rules

### 7.2 Integration Testing

- Test authentication flow
- Test registration process
- Test exam workflow

### 7.3 User Acceptance Testing

- Test với 3 role khác nhau
- Test các use case chính
- Performance testing

---

## 8. DELIVERABLES

### 8.1 Source Code

- Complete NetBeans project
- Well-documented code
- Unit tests

### 8.2 Executable Software

- JAR file với all dependencies
- Installer (optional)
- Sample data files

### 8.3 Documentation

- README.md với hướng dẫn sử dụng
- User manual cho 3 role
- Technical documentation
- Default login credentials

### 8.4 Report

- Báo cáo theo format đã yêu cầu
- Screenshots của các chức năng
- Class diagrams
- Database design

---

## 9. DEFAULT ACCOUNTS

### 9.1 Admin Account

- Username: `admin`
- Password: `admin123`
- Role: ADMIN

### 9.2 Examiner Account

- Username: `giamthi001`
- Password: `gt123456`
- Role: EXAMINER

### 9.3 Candidate Account

- Username: `thisinh001`
- Password: `ts123456`
- Role: CANDIDATE

---

## 10. RISK MANAGEMENT

### 10.1 Technical Risks

- **XML parsing errors**: Implement robust error handling
- **JavaFX compatibility**: Test on target JDK version
- **Performance issues**: Optimize data loading

### 10.2 Timeline Risks

- **Feature creep**: Stick to core requirements
- **Integration issues**: Start integration early
- **Testing delays**: Parallel development and testing

### 10.3 Mitigation Strategies

- Daily code reviews
- Incremental development
- Regular backups
- Version control (Git)

---

## 11. SUCCESS CRITERIA

### 11.1 Functional Requirements

- ✅ All 3 roles can login successfully
- ✅ Complete exam registration workflow
- ✅ Functional search with 2+ criteria
- ✅ Statistical reports generation
- ✅ Data persistence in XML
- ✅ Proper error handling

### 11.2 Technical Requirements

- ✅ Runs on JDK 23 + NetBeans 23
- ✅ Executable JAR file works on other machines
- ✅ Responsive UI with proper validation
- ✅ Clean, maintainable code structure

### 11.3 Documentation Requirements

- ✅ Complete user manual
- ✅ Technical documentation
- ✅ Formatted report per requirements
- ✅ README with setup instructions

---

**Ghi chú**: Kế hoạch này có thể được điều chỉnh dựa trên tiến độ thực tế và feedback trong quá trình phát triển.

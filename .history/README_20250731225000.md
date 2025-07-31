# OOPSH - Hệ thống Quản lý Kỳ thi Sát hạch

## 📋 Mô tả hệ thống

OOPSH là hệ thống quản lý kỳ thi sát hạch được phát triển bằng Java 17 và JavaFX. Hệ thống hỗ trợ quản lý toàn diện các kỳ thi sát hạch từ đăng ký, lên lịch thi, chấm điểm đến thống kê kết quả.

## 🎯 Chức năng chính

### 👤 Quản lý người dùng

- Quản lý thông tin thí sinh, giám thị, admin
- Phân quyền theo vai trò (CANDIDATE, EXAMINER, ADMIN)
- Tìm kiếm và lọc người dùng

### 📝 Quản lý kỳ thi

- Tạo và quản lý các loại thi khác nhau
- Lên lịch thi với DatePicker và ComboBox
- Quản lý trạng thái kỳ thi (OPEN, SCHEDULED, COMPLETED)

### 📊 Quản lý kết quả

- Chấm điểm lý thuyết và thực hành
- Thống kê tỷ lệ đậu/rớt
- Xuất báo cáo kết quả

### 💰 Quản lý thanh toán

- Theo dõi phí đăng ký thi
- Thống kê doanh thu
- Quản lý trạng thái thanh toán

### 📈 Thống kê và báo cáo

- Dashboard tổng quan với biểu đồ
- Thống kê theo tháng
- Báo cáo hoạt động gần đây

## 🚀 Cách chạy ứng dụng

### Yêu cầu hệ thống

- Java 17 hoặc cao hơn
- Maven 3.6+
- Windows/Linux/macOS

### Bước 1: Clone hoặc tải project

```bash
git clone <repository-url>
cd OOPSH
```

### Bước 2: Build project

```bash
mvn clean compile
```

### Bước 3: Chạy ứng dụng

```bash
mvn javafx:run
```

## 🔐 Thông tin đăng nhập

### Tài khoản Admin

```
Username: admin
Password: admin123
```

### Tài khoản Examiner (Giám thị)

```
Username: examiner
Password: examiner123
```

### Tài khoản Candidate (Thí sinh)

```
Username: candidate
Password: candidate123
```

## 📁 Cấu trúc project

```
OOPSH/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pocitaco/oopsh/
│   │   │       ├── controllers/     # JavaFX Controllers
│   │   │       ├── dao/            # Data Access Objects
│   │   │       ├── models/         # Entity classes
│   │   │       ├── enums/          # Enum classes
│   │   │       └── utils/          # Utility classes
│   │   └── resources/
│   │       └── com/pocitaco/oopsh/
│   │           ├── admin/          # Admin FXML files
│   │           ├── examiner/       # Examiner FXML files
│   │           ├── candidate/      # Candidate FXML files
│   │           └── styles/         # CSS files
├── data/                          # XML data files
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## 🎨 Giao diện người dùng

### Tính năng giao diện nâng cao

- **DatePicker**: Chọn ngày thi dễ dàng
- **ComboBox**: Dropdown lists cho loại thi, trạng thái
- **TableView**: Hiển thị dữ liệu dạng bảng chuyên nghiệp
- **Search Interface**: Tìm kiếm đa tiêu chí

### Tìm kiếm nâng cao

- **Fuzzy Search**: Tìm kiếm gần đúng theo tên, mã
- **Score Range Search**: Tìm kiếm theo khoảng điểm
- **Date Range Search**: Tìm kiếm theo khoảng thời gian

## 💾 Cơ sở dữ liệu

Hệ thống sử dụng file XML để lưu trữ dữ liệu:

- `data/users.xml` - Thông tin người dùng
- `data/exam-types.xml` - Loại thi
- `data/exam-schedules.xml` - Lịch thi
- `data/registrations.xml` - Đăng ký thi
- `data/results.xml` - Kết quả thi
- `data/payments.xml` - Thanh toán

## 🔧 Công nghệ sử dụng

- **Java 17**: Ngôn ngữ lập trình chính
- **JavaFX**: Framework giao diện người dùng
- **Maven**: Quản lý dependencies và build
- **XML**: Lưu trữ dữ liệu
- **DOM Parser**: Đọc/ghi file XML

## 📊 Thống kê hệ thống

Hệ thống cung cấp thống kê chi tiết:

- Tổng số thí sinh, giám thị, admin
- Số lượng kỳ thi và lịch thi
- Tỷ lệ đậu/rớt
- Doanh thu từ phí thi
- Hoạt động gần đây

## 🛡️ Bảo mật và validation

- Validation đầy đủ cho tất cả input
- Kiểm tra định dạng email, số điện thoại
- Ngăn trùng lặp username, email
- Phân quyền theo vai trò người dùng
- Thông báo lỗi bằng tiếng Việt

## 📈 Tính năng nổi bật

### Đáp ứng yêu cầu đề bài

✅ **Cơ sở dữ liệu XML** - Không sử dụng database khác  
✅ **Calendar picker** - DatePicker cho ngày thi  
✅ **Dropdown lists** - ComboBox cho loại thi, trạng thái  
✅ **Table display** - Tất cả dữ liệu hiển thị dạng bảng  
✅ **String search** - Tìm kiếm gần đúng theo tên, mã  
✅ **Number search** - Tìm kiếm theo khoảng điểm, phí thi  
✅ **Currency formatting** - Hiển thị "1,000,000 VNĐ"  
✅ **ID auto-increment** - Tất cả ID là số nguyên tăng dần  
✅ **Error handling** - Validation đầy đủ, ngăn trùng lặp  
✅ **Statistics** - Thống kê tổng số, tỷ lệ đậu/rớt, doanh thu

## 🐛 Xử lý lỗi thường gặp

### Lỗi Java version

```
Error: Java version not supported
```

**Giải pháp**: Cài đặt Java 17 hoặc cao hơn

### Lỗi Maven

```
Error: Maven not found
```

**Giải pháp**: Cài đặt Maven và thêm vào PATH

### Lỗi JavaFX

```
Error: JavaFX runtime components are missing
```

**Giải pháp**: Chạy bằng lệnh `mvn javafx:run`

## 📞 Hỗ trợ

Nếu gặp vấn đề, vui lòng:

1. Kiểm tra Java version: `java -version`
2. Kiểm tra Maven: `mvn -version`
3. Clean và rebuild: `mvn clean compile`
4. Chạy lại: `mvn javafx:run`

## 📝 Ghi chú

- Hệ thống được thiết kế theo mô hình MVC
- Sử dụng DAO pattern cho truy cập dữ liệu
- Giao diện responsive và user-friendly
- Hỗ trợ đa ngôn ngữ (hiện tại: Tiếng Việt)

---

**OOPSH - Hệ thống Quản lý Kỳ thi Sát hạch**  
_Phát triển bởi: Nhóm OOP N02_  
_Giáo viên hướng dẫn: Hà Thị Kim Dung_

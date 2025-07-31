# OOPSH - Online Office Practice and Safety Health

## **📋 Mô tả phần mềm**

OOPSH là hệ thống quản lý các kỳ thi sát hạch trực tuyến, được phát triển bằng JavaFX với giao diện Material Design hiện đại. Hệ thống hỗ trợ 3 vai trò người dùng: Admin, Examiner và Candidate.

## **🚀 Các chức năng chính**

### **Admin Dashboard:**

- Quản lý người dùng (thêm, sửa, xóa, phân quyền)
- Quản lý loại thi (A1, A2, B1, B2, C, D, E, F)
- Quản lý lịch thi và lịch trình
- Xem kết quả thi và thống kê
- Quản lý chứng chỉ
- Báo cáo doanh thu và hiệu suất

### **Examiner Dashboard:**

- Chấm điểm thi lý thuyết và thực hành
- Xem danh sách thí sinh
- Quản lý phiên thi
- Báo cáo phiên thi
- Thống kê hiệu suất

### **Candidate Dashboard:**

- Đăng ký thi
- Xem lịch thi
- Xem kết quả thi
- Xem chứng chỉ
- Thanh toán phí thi

## **🔐 Thông tin tài khoản LOGIN**

### **Admin Account:**

```
Username: admin
Password: admin123
```

### **Examiner Account:**

```
Username: examiner
Password: examiner123
```

### **Candidate Account:**

```
Username: candidate
Password: candidate123
```

## **💻 Cách chạy chương trình**

### **Phương pháp 1: Chạy từ source code**

```bash
# Clone repository
git clone <repository-url>
cd OOPSH-main

# Compile và chạy
mvn clean javafx:run
```

### **Phương pháp 2: Chạy JAR file**

```bash
# Chạy JAR file đã đóng gói
java -jar target/OOPSH-1.0-SNAPSHOT.jar
```

### **Phương pháp 3: Double-click JAR file**

- Mở thư mục `target/`
- Double-click vào file `OOPSH-1.0-SNAPSHOT.jar`

## **📋 Yêu cầu hệ thống**

- **Java**: JDK 17 hoặc cao hơn
- **RAM**: Tối thiểu 2GB
- **OS**: Windows, macOS, Linux
- **Maven**: 3.6+ (để build từ source)

## **🏗️ Cấu trúc dự án**

```
OOPSH-main/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/pocitaco/oopsh/
│   │   │       ├── controllers/     # JavaFX Controllers
│   │   │       ├── dao/            # Data Access Objects
│   │   │       ├── models/         # Entity classes
│   │   │       ├── enums/          # Enumerations
│   │   │       ├── interfaces/     # Interfaces
│   │   │       └── utils/          # Utility classes
│   │   └── resources/
│   │       └── com/pocitaco/oopsh/
│   │           ├── admin/          # Admin FXML files
│   │           ├── candidate/      # Candidate FXML files
│   │           ├── examiner/       # Examiner FXML files
│   │           └── styles/         # CSS files
├── data/                          # XML data files
├── target/                        # Compiled files
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## **🗄️ Cơ sở dữ liệu**

Hệ thống sử dụng file XML để lưu trữ dữ liệu:

- `users.xml` - Thông tin người dùng
- `exam_types.xml` - Loại thi
- `exam_schedules.xml` - Lịch thi
- `registrations.xml` - Đăng ký thi
- `results.xml` - Kết quả thi
- `certificates.xml` - Chứng chỉ
- `session_reports.xml` - Báo cáo phiên thi
- `study_materials.xml` - Tài liệu học tập

## **🔧 Tính năng kỹ thuật**

### **Giao diện:**

- ✅ DatePicker cho chọn ngày tháng năm
- ✅ ComboBox dropdown list cho các items
- ✅ TableView hiển thị kết quả dưới dạng bảng
- ✅ MaterialFX design hiện đại

### **Tìm kiếm:**

- ✅ Tìm kiếm gần đúng theo String
- ✅ Tìm kiếm theo khoảng số
- ✅ Tìm kiếm theo ngày

### **Định dạng:**

- ✅ Tiền tệ: `1,000,000 VNĐ` với dấu phẩy tách 3 số
- ✅ ID tự động tăng dần
- ✅ Xử lý lỗi toàn diện

### **Thống kê:**

- ✅ Tổng số, lớn nhất, nhỏ nhất
- ✅ Dashboard với real-time data
- ✅ Export dữ liệu

## **🚨 Xử lý lỗi**

Hệ thống có các cơ chế xử lý lỗi:

- Input validation
- Duplicate prevention
- ID conflict handling
- Error dialogs
- Fallback data

## **📊 Thống kê hệ thống**

- **Tổng số người dùng**: Hiển thị theo vai trò
- **Tổng số kỳ thi**: Đã lên lịch và hoàn thành
- **Tỷ lệ đậu**: Thống kê theo thời gian
- **Doanh thu**: Theo tháng và năm

## **🔒 Bảo mật**

- Mã hóa mật khẩu
- Phân quyền theo vai trò
- Session management
- Input sanitization

## **📱 Responsive Design**

Giao diện được thiết kế responsive:

- Tương thích với nhiều độ phân giải
- Layout tự động điều chỉnh
- Touch-friendly controls

## **🔄 Cập nhật và bảo trì**

### **Cập nhật dữ liệu:**

- Tự động backup XML files
- Version control cho dữ liệu
- Rollback capability

### **Bảo trì:**

- Log files cho debugging
- Performance monitoring
- Error tracking

## **📞 Hỗ trợ**

Nếu gặp vấn đề, vui lòng:

1. Kiểm tra Java version (yêu cầu JDK 17+)
2. Đảm bảo có quyền write vào thư mục data/
3. Kiểm tra log files trong console
4. Liên hệ support team

## **📄 License**

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

---

**Phiên bản**: 1.0-SNAPSHOT  
**Ngày phát hành**: 01/08/2025  
**Nhóm phát triển**: OOPSH Team  
**Giáo viên hướng dẫn**: Hà Thị Kim Dung

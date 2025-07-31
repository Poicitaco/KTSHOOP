# Hướng dẫn Compile Báo cáo LaTeX

## 📋 Yêu cầu hệ thống

### 1. Cài đặt LaTeX Distribution
- **Windows**: MiKTeX hoặc TeX Live
- **macOS**: MacTeX
- **Linux**: TeX Live

### 2. Cài đặt Editor
- **TeXstudio** (Khuyến nghị)
- **TeXmaker**
- **Overleaf** (Online)
- **VS Code** với extension LaTeX Workshop

## 🚀 Cách compile

### Phương pháp 1: Sử dụng TeXstudio

1. **Mở file `bao_cao_oop.tex`** trong TeXstudio
2. **Chọn Build & View** (F5) hoặc **Quick Build** (F1)
3. **Chọn LaTeX → PDF** trong menu Build
4. **Đợi compile hoàn thành**

### Phương pháp 2: Sử dụng Command Line

```bash
# Windows (MiKTeX)
pdflatex bao_cao_oop.tex
pdflatex bao_cao_oop.tex

# Linux/macOS (TeX Live)
pdflatex bao_cao_oop.tex
pdflatex bao_cao_oop.tex
```

### Phương pháp 3: Sử dụng Overleaf (Online)

1. **Truy cập** https://www.overleaf.com
2. **Tạo project mới**
3. **Upload file `bao_cao_oop.tex`**
4. **Upload tất cả hình ảnh vào thư mục `images/`**
5. **Compile tự động**

## ⚠️ Lưu ý quan trọng

### 1. Hình ảnh
- **Đảm bảo tất cả hình ảnh đã được tạo** trong thư mục `images/`
- **Tên file phải khớp** với reference trong file .tex
- **Định dạng**: PNG, JPG, PDF

### 2. Font chữ
- **Times New Roman** được sử dụng làm font chính
- **Cài đặt font tiếng Việt** nếu cần thiết

### 3. Packages
- **Tất cả packages đã được include** trong file .tex
- **Không cần cài đặt thêm** packages nào khác

## 🔧 Xử lý lỗi thường gặp

### Lỗi 1: Không tìm thấy hình ảnh
```
! LaTeX Error: File `images/system_architecture.png' not found.
```

**Giải pháp**: 
- Kiểm tra file hình ảnh có tồn tại không
- Kiểm tra đường dẫn trong file .tex
- Đảm bảo tên file chính xác (phân biệt hoa thường)

### Lỗi 2: Font không hỗ trợ tiếng Việt
```
! LaTeX Error: Command \vietnam unavailable.
```

**Giải pháp**:
- Cài đặt package `vietnam`
- Hoặc thay thế bằng `\usepackage[utf8]{inputenc}`

### Lỗi 3: Compile lần đầu không thành công
```
! LaTeX Error: Label(s) may have changed.
```

**Giải pháp**:
- **Compile 2 lần** để tạo mục lục và references
- Lần đầu: `pdflatex bao_cao_oop.tex`
- Lần hai: `pdflatex bao_cao_oop.tex`

## 📊 Kiểm tra kết quả

### 1. File PDF được tạo
- `bao_cao_oop.pdf` trong cùng thư mục
- Kích thước file khoảng 2-5MB

### 2. Nội dung đầy đủ
- ✅ Bìa chính và bìa lót
- ✅ Mục lục tự động
- ✅ Danh sách hình vẽ và bảng
- ✅ Tất cả hình ảnh hiển thị đúng
- ✅ Code snippets được highlight
- ✅ Đánh số trang

### 3. Format đúng yêu cầu
- ✅ Font Times New Roman, size 13
- ✅ Line spacing 1.3pt
- ✅ Margins: top-left-bottom-right: 2-3-2-2 cm
- ✅ Đánh số trang ở giữa, dưới

## 🎯 Checklist trước khi nộp

- [ ] File PDF được tạo thành công
- [ ] Tất cả hình ảnh hiển thị đúng
- [ ] Mục lục và references hoạt động
- [ ] Format đúng yêu cầu giáo viên
- [ ] Nội dung đầy đủ và chính xác
- [ ] Backup file .tex và hình ảnh

## 📞 Hỗ trợ

Nếu gặp vấn đề, hãy:
1. Kiểm tra log file (.log) để xem lỗi chi tiết
2. Đảm bảo đã cài đặt đầy đủ LaTeX distribution
3. Thử compile trên Overleaf để kiểm tra
4. Liên hệ hỗ trợ kỹ thuật nếu cần 
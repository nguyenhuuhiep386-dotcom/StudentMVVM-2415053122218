# StudentMVVM — Quản lý sinh viên

**Sinh viên** Nguyễn Hữu Hiệp  
**MSSV** 2415053122218  
**Bài thực hành** Lecture 9 — Room Database & REST API

## Mở trong Android Studio

1. Giải nén `2415053122218.zip`. Trong Android Studio, chọn **File → Open**
   và mở thư mục **StudentMVVM** chứa `settings.gradle.kts`.
2. Dùng Android Studio hỗ trợ Android Gradle Plugin 8.7.3 (Ladybug 2024.2.1 hoặc
   bản mới hơn). Trong **Settings → Build, Execution, Deployment → Build Tools →
   Gradle**, chọn **Gradle JDK** là JDK 17 hoặc JDK 21. Cài **Android SDK Platform 35**
   bằng **Tools → SDK Manager**. Gradle Wrapper sẽ tải Gradle 8.9 và Gradle Sync sẽ
   tải các thư viện ở lần mở đầu tiên.
3. Trong **Device Manager**, mở một Android Emulator, ưu tiên ảnh hệ thống API 35.
   Chọn cấu hình `app` rồi nhấn **Run ▶**. App hỗ trợ Android từ API 23.

Có thể kiểm tra build trên Windows bằng lệnh `.\gradlew.bat :app:assembleDebug` ở
thư mục project. File sinh ra nằm tại `app/build/outputs/apk/debug/app-debug.apk`.

## Thử Room

- Nhấn **Thêm sinh viên**. Có thể nhập thử tên **Nguyễn Hữu Hiệp**, MSSV
  **2415053122218**, để trống email và điền GPA ví dụ **3.5**.
- Kiểm tra bản ghi xuất hiện, sửa GPA rồi mở lại app để xem dữ liệu còn lưu.
- Nhấn **Xóa** và xác nhận để kiểm tra danh sách cập nhật lại.
- Form kiểm tra họ tên, MSSV, email khi có nhập và GPA từ 0 đến 4. GPA chấp nhận
  dấu chấm hoặc dấu phẩy thập phân.

## Thử Retrofit và dữ liệu ngoại tuyến

Mở Terminal hoặc PowerShell trong thư mục `StudentMVVM` rồi chạy lệnh sau với Python 3.

```powershell
py -3 demo-api/server.py
```

Nếu máy dùng lệnh `python` hoặc `python3`, thay `py -3` bằng lệnh tương ứng. Giữ
cửa sổ này mở. Trên **Android Emulator**, trở lại app và nhấn **Đồng bộ API**.
Ba bản ghi mẫu sẽ xuất hiện với nhãn **Từ API**. Nút này gọi
`GET /api/v1/students`, lưu dữ liệu vào Room trong một giao dịch và cập nhật
danh sách qua Flow. Địa chỉ `10.0.2.2:8765` trỏ từ Android Emulator về máy tính
đang chạy server. Cấu hình HTTP cục bộ dùng cho bản debug.

Nhấn đồng bộ lần nữa để kiểm tra các bản ghi API không bị nhân đôi. Tắt server
bằng **Ctrl+C**, đồng bộ lại và kiểm tra app báo mất kết nối nhưng danh sách vẫn
hiển thị. Các thay đổi qua giao diện được ghi vào Room. Khi đồng bộ thành công,
server cập nhật lại các dòng **Từ API** và giữ các dòng **Nhập tại máy**.

API mẫu cũng hỗ trợ `POST /api/v1/students`. Thử trực tiếp trong PowerShell nếu
muốn kiểm tra endpoint này, sau đó đồng bộ trên app. Server giữ các bản ghi
được POST trong bộ nhớ cho đến khi dừng.

```powershell
$student = @{
    full_name = "Sinh vien thu"
    student_code = "DEMO004"
    email = "demo4@example.com"
    gpa = 3.5
} | ConvertTo-Json

Invoke-RestMethod -Method Post -Uri "http://127.0.0.1:8765/api/v1/students" `
    -ContentType "application/json" -Body $student
```

## Các lớp theo bài giảng

| Thành phần | Vị trí trong package `com.example.studentmvvm` |
| --- | --- |
| Entity, DAO và Room Singleton | `model/Student.kt`, `data/StudentDao.kt`, `data/AppDatabase.kt` |
| REST API, DTO, Retrofit Singleton | `network/StudentApiService.kt` |
| Single Source of Truth | `repository/StudentRepository.kt` |
| Flow → LiveData và coroutine trên luồng IO | `viewmodel/StudentViewModel.kt` |
| Single Activity, Fragment, ViewBinding, ListAdapter và DiffUtil | `ui/` và `res/layout/` |

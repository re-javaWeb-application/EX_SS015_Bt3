# Báo cáo phân tích và thiết kế (Bài 3)

## 1. Phân tích (I/O)
- **Input (từ URL / Query Parameters):**
  - `status` (String, optional): Trạng thái đơn hàng để lọc (Ví dụ: "Đang giao", "Đã hủy", "Tất cả").
  - `page` (int, default = 1): Số trang hiện tại người dùng muốn xem.
  - `size` (int, default = 10): Số lượng đơn hàng trên mỗi trang.
  - `sortBy` (String, default = "createdAt"): Cột dữ liệu để sắp xếp.
  - `sortDir` (String, default = "desc"): Chiều sắp xếp ("asc" hoặc "desc").

- **Output (truyền xuống Model cho View):**
  - `orders` (List<Order>): Danh sách đơn hàng trong trang hiện tại.
  - `currentPage` (int): Trang hiện tại đang hiển thị.
  - `totalPages` (int): Tổng số trang để vẽ nút phân trang.
  - `totalItems` (long): Tổng số lượng đơn hàng tìm được.

## 2. Đề xuất giải pháp
- Dùng `Pageable` của Spring Data JPA để đóng gói thông tin phân trang (Page number, Page size) và sắp xếp (`Sort`).
- Dùng interface `Page<Order>` làm kiểu trả về của Repository để tự động đếm tổng số bản ghi và lấy dữ liệu theo trang.
- Repository extends `JpaRepository` và khai báo method trả về `Page<Order>` có tham số `Pageable`.

## 3. Thiết kế luồng xử lý
1. **Trình duyệt:** Người dùng chọn bộ lọc, trang, cột sắp xếp trên giao diện, trình duyệt gửi HTTP GET Request với các Query Parameters đến Controller.
2. **Controller:** 
   - Nhận các tham số `@RequestParam`.
   - Bắt bẫy lỗi 1 (Tràn viền phân trang): Đảm bảo `page >= 1` (nếu `< 1` thì gán `= 1`).
   - Gọi method của Service và truyền các tham số này xuống.
3. **Service:**
   - Xử lý bẫy lỗi 2 (Tham số sắp xếp): Kiểm tra `sortBy` có tồn tại trong các trường cho phép hay không. Nếu không, gán về trường mặc định.
   - Tạo đối tượng `PageRequest` (kế thừa `Pageable`) chứa các thông tin `page`, `size`, `Sort`.
   - Gọi hàm từ Repository. Kiểm tra nếu `page` vượt quá `totalPages` thì lấy trang cuối cùng.
4. **Repository:** Spring Data JPA sinh ra câu lệnh SQL có `LIMIT`, `OFFSET` và `ORDER BY`, truy vấn database.
5. **Controller:** Nhận kết quả từ Service, đưa vào `Model`.
6. **View:** Hiển thị danh sách và vẽ các nút phân trang.

# 🎨 Xem Trước Thiết Kế Giao Diện Hiện Đại

## 1. Màn Hình Đăng Nhập (Login Screen)

```
╔════════════════════════════════════════════════════╗
║                                                    ║
║                    ☕ QL Coffee                    ║
║                                                    ║
║                                                    ║
║         Tên đăng nhập                              ║
║    ┌────────────────────────────────┐              ║
║    │ [Text Field - Modern Style  ]   │              ║
║    └────────────────────────────────┘              ║
║                                                    ║
║         Mật khẩu                                   ║
║    ┌────────────────────────────────┐              ║
║    │ [Password Field - Modern  ]     │              ║
║    └────────────────────────────────┘              ║
║                                                    ║
║    ┌────────────────────────────────┐              ║
║    │        💙 Đăng nhập             │              ║
║    └────────────────────────────────┘              ║
║                                                    ║
║       Demo: admin/admin123                         ║
║       hoặc staff/staff123                          ║
║                                                    ║
╚════════════════════════════════════════════════════╝

Màu sắc:
- Nền: Xanh đen tối (#0F172A)
- Trường nhập: Xám đen (#334155)
- Nút: Chàm (#6366F1)
- Text: Trắng (#F8FAFC)
```

## 2. Màn Hình Quản Lý Sản Phẩm (Product Management)

```
╔════════════════════════════════════════════════════════════════╗
║                   BẢNG SẢN PHẨM                                ║
╠════════════════════════════════════════════════════════════════╣
║ ID  │ Tên          │ Danh Mục   │ Giá       │ ...              ║
╠════════════════════════════════════════════════════════════════╣
║ 1   │ Cà phê đen   │ Đồ uống    │ 25.000    │                  ║
║ 2   │ Bánh quy     │ Ăn vặt     │ 15.000    │                  ║
║ 3   │ Trà chanh    │ Đồ uống    │ 20.000    │                  ║
║ ... │ ...          │ ...        │ ...       │                  ║
╚════════════════════════════════════════════════════════════════╝

┌─ FORM NHẬP ─────────────────────────────────────────────────┐
│ ID:         │ [Text Field]          │                        │
│ Tên:        │ [Text Field]          │                        │
│ Danh Mục:   │ [Text Field]          │                        │
│ Giá:        │ [Text Field]          │                        │
│                                   [✅ Thêm] [📝 Sửa] [🗑️ Xóa] │
└──────────────────────────────────────────────────────────────┘

Kiểu nút:
- Thêm    : Màu xanh lá cây (#22C55E)
- Sửa     : Màu xanh dương (#3B82F6)
- Xóa     : Màu đỏ (#EF4444)
```

## 3. Màn Hình Quản Lý Bàn (Table Management)

```
╔════════════════════════════════════════════════════════╗
║          BẢNG QUẢN LÝ BÀN                              ║
╠════════════════════════════════════════════════════════╣
║ ID  │ Tên Bàn      │ Trạng Thái       │ ...           ║
╠════════════════════════════════════════════════════════╣
║ 1   │ Bàn 01       │ Trống            │               ║
║ 2   │ Bàn 02       │ Đang dùng        │               ║
║ 3   │ Bàn 03       │ Trống            │               ║
║ ... │ ...          │ ...              │               ║
╚════════════════════════════════════════════════════════╝

┌─ FORM QUẢN LÝ BÀN ───────────────────────────────────┐
│ ID:       │ [Text Field]      │                       │
│ Tên Bàn:  │ [Text Field]      │                       │
│ Trạng Thái: ☐ Đang dùng                               │
│                           [✅ Thêm] [📝 Sửa] [🗑️ Xóa] │
└───────────────────────────────────────────────────────┘
```

## 4. Màn Hình Quản Lý Nhân Viên (Employee Management)

```
╔════════════════════════════════════════════════════════════════╗
║           BẢNG NHÂN VIÊN                                        ║
╠════════════════════════════════════════════════════════════════╣
║ ID  │ Tên         │ Role      │ Username     │ ...              ║
╠════════════════════════════════════════════════════════════════╣
║ 1   │ Nguyễn Văn A│ ADMIN     │ admin        │                  ║
║ 2   │ Trần Thị B  │ STAFF     │ staff        │                  ║
║ 3   │ Lê Văn C    │ STAFF     │ staff2       │                  ║
║ ... │ ...         │ ...       │ ...          │                  ║
╚════════════════════════════════════════════════════════════════╝

┌─ FORM NHÂN VIÊN ─────────────────────────────────────────────┐
│ ID:        │ [Text Field]           │                         │
│ Tên:       │ [Text Field]           │                         │
│ Vị Trí:    │ [ComboBox Role]        │                         │
│ Username:  │ [Text Field]           │                         │
│ Mật Khẩu:  │ [Password Field]       │                         │
│                                 [✅ Thêm] [📝 Sửa] [🗑️ Xóa]  │
└────────────────────────────────────────────────────────────────┘
```

## 5. Màn Hình Quản Lý Hóa Đơn (Invoice Management)

```
╔════════════════════════════════════════════════════════════════╗
║           BẢNG HÓA ĐƠN                                          ║
╠════════════════════════════════════════════════════════════════╣
║ ID  │ Bàn     │ Nhân Viên   │ Tổng        │ Trạng Thái       ║
╠════════════════════════════════════════════════════════════════╣
║ 1   │ Bàn 01  │ N.V.A       │ 125.000     │ Đã Thanh Toán   ║
║ 2   │ Bàn 02  │ N.V.B       │ 95.000      │ Chưa Thanh Toán ║
║ 3   │ Bàn 03  │ N.V.C       │ 75.000      │ Đã Thanh Toán   ║
║ ... │ ...     │ ...         │ ...         │ ...             ║
╚════════════════════════════════════════════════════════════════╝

┌─ Tạo HÓA ĐƠN ──────────────────────────────────────────────┐
│ Bàn ID: [Field]  [💙 Tạo Hóa Đơn]                          │
└───────────────────────────────────────────────────────────────┘

┌─ THÊM MÓN ──────────────────────────────────────────────────┐
│ H.Đơn ID: [Field] │ SP ID: [Field] │ S/Lượng: [Field]      │
│ [🟢 Thêm Món]                                               │
└───────────────────────────────────────────────────────────────┘

┌─ THANH TOÁN ────────────────────────────────────────────────┐
│ H.Đơn ID: [Field]  [💙 Thanh Toán]                          │
└───────────────────────────────────────────────────────────────┘

┌─ IN CHI TIẾT ───────────────────────────────────────────────┐
│ H.Đơn ID: [Field]  [🔵 In Chi Tiết]                         │
└───────────────────────────────────────────────────────────────┘
```

## 6. Màn Hình Thống Kê (Report View)

```
╔════════════════════════════════════════════════════════════════╗
║                      THỐNG KÊ                                  ║
╠════════════════════════════════════════════════════════════════╣
║                                                                ║
║  THỐNG KÊ DOANH SỐ THEO NGÀY:                                  ║
║  - 2024-04-20: 2.345.000 VNĐ                                   ║
║  - 2024-04-19: 1.890.000 VNĐ                                   ║
║                                                                ║
║  THỐNG KÊ NHÂN VIÊN:                                           ║
║  - Nguyễn Văn A: 15 hóa đơn                                    ║
║  - Trần Thị B: 12 hóa đơn                                      ║
║                                                                ║
║  SẢN PHẨM BÁN CHẠY:                                            ║
║  - Cà phê: 45 cốc                                              ║
║  - Trà: 30 cốc                                                 ║
║                                                                ║
╠════════════════════════════════════════════════════════════════╣
║           [💙 Làm Mới Thống Kê]                                ║
╚════════════════════════════════════════════════════════════════╝
```

## 7. Thanh Điều Khiển Chính (Top Navigation)

```
╔════════════════════════════════════════════════════════════════╗
║ ☕ Coffee Management - Nguyễn Văn A (ADMIN)                   ║
╠════════════════════════════════════════════════════════════════╣
║  [Sản phẩm]  [Bàn]  [Hóa đơn]  [Nhân viên]  [Thống kê]       ║
║                                                                ║
║  (Tab pane content)                                            ║
║                                                                ║
╠════════════════════════════════════════════════════════════════╣
║ Người dùng: Nguyễn Văn A     Nguồn: Admin        [🔴 Đăng Xuất]║
╚════════════════════════════════════════════════════════════════╝
```

## 🎯 Bảng Màu Chi Tiết

| Tên              | Hex Code | RGB Code           | Sử Dụng              |
|------------------|----------|-------------------|----------------------|
| Nền Chính        | #0F172A  | 15, 23, 42        | Nền cửa sổ          |
| Nền Phụ          | #1E293B  | 30, 41, 59        | Panel điều khiển    |
| Nền Tertiary     | #334155  | 51, 65, 85        | Trường nhập         |
| Chữ Chính        | #F8FAFC  | 248, 250, 252     | Chữ chính           |
| Chữ Phụ          | #CBD5E1  | 203, 213, 225     | Chữ mô tả           |
| Tiêu Đề          | #6366F1  | 99, 102, 241      | Tiêu đề, nút chính  |
| Thành Công       | #22C55E  | 34, 197, 94       | Nút Thêm            |
| Cảnh Báo         | #FB923C  | 251, 146, 60      | Nút Sửa             |
| Nguy Hiểm        | #EF4444  | 239, 68, 68       | Nút Xóa, Đăng Xuất  |
| Thông Tin        | #3B82F6  | 59, 130, 246      | Nút Info            |
| Khung (Border)   | #475569  | 71, 85, 105       | Khung trường        |

## 📐 Kích Thước & Khoảng Cách

| Tên                | Giá Trị | Sử Dụng                      |
|--------------------|--------|------------------------------|
| Padding XS         | 4px    | Khoảng cách nhỏ              |
| Padding SM         | 8px    | Khoảng cách nhỏ-trung        |
| Padding MD         | 12px   | Khoảng cách trung bình       |
| Padding LG         | 16px   | Khoảng cách lớn              |
| Padding XL         | 24px   | Khoảng cách rất lớn          |
| Padding XXL        | 32px   | Khoảng cách siêu lớn         |
| Border Radius SM   | 4px    | Góc bo nhỏ                   |
| Border Radius MD   | 8px    | Góc bo trung bình            |
| Border Radius LG   | 12px   | Góc bo lớn                   |
| Chiều cao nút      | 40px   | Nút tiêu chuẩn               |
| Chiều rộng nút     | 120px  | Nút tiêu chuẩn               |
| Chiều cao trường   | 40px   | Trường nhập tiêu chuẩn       |
| Chiều rộng trường  | 250px  | Trường nhập tiêu chuẩn       |

## ✨ Hiệu Ứng & Tương Tác

### Nút (Button)
- **Normal State**: Màu cơ bản
- **Hover State**: Làm sáng 10% (brighten)
- **Click**: Con trỏ thay đổi thành Hand
- **Focus**: Outline rõ ràng

### Trường Nhập (TextField/PasswordField)
- **Normal State**: Khung xám
- **Focus State**: Khung thay đổi sang xanh chàm (#6366F1)
- **Transition**: Mượt mà
- **Cursor**: Khi focus, con trỏ hiển thị

### Bảng (Table)
- **Row Hover**: Có thể thêm hover effect
- **Selection**: Highlight với màu chàm
- **Header**: In đậm, background tối hơn

---

## 🎓 Các Tính Năng Hiện Đại

✅ **Dark Theme** - Giao diện tối, thoải mái khi nhìn lâu
✅ **Rounded Components** - Các phần tử bo tròn mềm mại
✅ **Hover Effects** - Phản hồi khi di chuột
✅ **Color Coding** - Các màu khác nhau cho các hành động
✅ **Typography** - Font phù hợp cho từng loại text
✅ **Consistent Spacing** - Khoảng cách thống nhất
✅ **Professional Look** - Giao diện chuyên nghiệp
✅ **Accessibility** - Màu sắc tương phản cao

---

**Thiết kế được hoàn thành: 20/04/2026**

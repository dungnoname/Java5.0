-------------------------------
-- 1. TẠO DATABASE (Làm sạch & Tạo mới)
-------------------------------
USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = 'Java6Store')
BEGIN
    ALTER DATABASE Java6Store SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE Java6Store;
END
GO

CREATE DATABASE Java6Store COLLATE Latin1_General_100_CI_AS_SC_UTF8; 
GO
USE Java6Store;
GO

-------------------------------
-- 2. TẠO CẤU TRÚC BẢNG (GIỮ NGUYÊN GỐC)
-------------------------------

-- BẢNG ROLE
CREATE TABLE Roles (
    RoleId INT PRIMARY KEY,
    RoleName NVARCHAR(50) NOT NULL
);

-- BẢNG USERS
CREATE TABLE Users (
    UserId INT PRIMARY KEY IDENTITY(1,1),
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE NULL,
    GioiTinh BIT, -- 1: Nam, 0: Nữ
    SoDienThoai VARCHAR(20),
    Email VARCHAR(100),
    DiaChi NVARCHAR(255),
    TenDangNhap VARCHAR(50) UNIQUE,
    MatKhau VARCHAR(100),
    RoleId INT NOT NULL DEFAULT 0 FOREIGN KEY REFERENCES Roles(RoleId),
    Reset_Password_Token VARCHAR(255) NULL
);

-- BẢNG HÃNG
CREATE TABLE Hang (
    MaHang INT PRIMARY KEY IDENTITY(1,1),
    TenHang NVARCHAR(100) NOT NULL
);

-- BẢNG LOẠI SẢN PHẨM
CREATE TABLE LoaiSanPham (
    MaLoai INT PRIMARY KEY IDENTITY(1,1),
    TenLoai NVARCHAR(100) NOT NULL
);

-- BẢNG SẢN PHẨM
CREATE TABLE SanPham (
    MaSP INT PRIMARY KEY IDENTITY(1,1),
    TenSP NVARCHAR(255) NOT NULL,
    DonGiaBan DECIMAL(18,2) NOT NULL,
    SoLuongTon INT NOT NULL,
    MoTa NVARCHAR(MAX),
    ImgUrl NVARCHAR(255),
    TrangThai BIT DEFAULT 1,
    MaLoai INT FOREIGN KEY REFERENCES LoaiSanPham(MaLoai),
    MaHang INT FOREIGN KEY REFERENCES Hang(MaHang),
    NgayTao DATETIME DEFAULT GETDATE()
);

-- BẢNG GIỎ HÀNG
CREATE TABLE GioHang (
    MaGH INT PRIMARY KEY IDENTITY(1,1),
    UserId INT NOT NULL FOREIGN KEY REFERENCES Users(UserId),
    MaSP INT NOT NULL FOREIGN KEY REFERENCES SanPham(MaSP),
    SoLuong INT NOT NULL CHECK (SoLuong > 0),
    NgayTao DATETIME DEFAULT GETDATE()
);

-- BẢNG TRẠNG THÁI ĐƠN HÀNG
CREATE TABLE TrangThaiDonHang (
    MaTT INT PRIMARY KEY,
    TenTT NVARCHAR(50) NOT NULL,
    MoTa NVARCHAR(255),
    ThuTu INT
);

-- BẢNG HÓA ĐƠN
CREATE TABLE HoaDon (
    MaHD INT PRIMARY KEY IDENTITY(1,1),
    NgayLap DATETIME DEFAULT GETDATE(),
    NguoiDungId INT FOREIGN KEY REFERENCES Users(UserId),
    NhanVienId INT FOREIGN KEY REFERENCES Users(UserId),
    MaTT INT DEFAULT 0 FOREIGN KEY REFERENCES TrangThaiDonHang(MaTT),
);

-- BẢNG CHI TIẾT HÓA ĐƠN
CREATE TABLE ChiTietHoaDon (
    MaCTHD INT PRIMARY KEY IDENTITY(1,1),
    MaHD INT FOREIGN KEY REFERENCES HoaDon(MaHD),
    MaSP INT FOREIGN KEY REFERENCES SanPham(MaSP),
    SoLuongBan INT NOT NULL CHECK (SoLuongBan > 0),
    DonGia DECIMAL(18,2) NOT NULL
);

-- BẢNG ĐÁNH GIÁ
CREATE TABLE DanhGia (
    MaDG INT PRIMARY KEY IDENTITY(1,1),
    SoSao INT CHECK (SoSao BETWEEN 1 AND 5),
    NoiDung NVARCHAR(500),
    NgayDanhGia DATETIME DEFAULT GETDATE(),
    UserId INT FOREIGN KEY REFERENCES Users(UserId),
    MaSP INT FOREIGN KEY REFERENCES SanPham(MaSP)
);

-------------------------------
-- 3. NẠP DỮ LIỆU (Đã lọc theo yêu cầu: 20 Laptop, 3 Chuột, 3 Phím)
-------------------------------

-- Roles
INSERT INTO Roles (RoleId, RoleName) VALUES (0, N'User'), (1, N'Staff'), (2, N'Admin');

-- Users
INSERT INTO Users (HoTen, NgaySinh, GioiTinh, SoDienThoai, Email, DiaChi, TenDangNhap, MatKhau, RoleId, Reset_Password_Token) VALUES
(N'Lê Hoàng Chấn Kiệt', '1995-05-10', 1, '0901234567', 'kietlh@techshop.com', N'Hà Nội', 'chankiet95', 'kiet123', 0, ''),
(N'Phạm Đăng Khoa', '1998-03-22', 1, '0912345678', 'khoapd@techshop.com', N'Hồ Chí Minh', 'dangkhoa98', 'khoa123', 0, ''),
(N'Nguyễn Hoàng Gia Hưng', '2000-07-15', 1, '0923456789', 'hungnhg@techshop.com', N'Đà Nẵng', 'giahung00', 'hung123', 0, ''),
(N'Nguyễn Tấn Dũng', '1997-12-01', 1, '0934567890', 'dungnt@techshop.com', N'Cần Thơ', 'tandung97', 'dung123', 0, ''),
(N'Nguyễn Hoàng Gia Bảo', '1996-09-09', 1, '0945678901', 'baonhg@techshop.com', N'Hải Phòng', 'giabao96', 'bao123', 0, ''),
(N'Nguyễn Văn Nhân', '1994-02-01', 1, '0951234567', 'nhannv@staff.com', N'Hà Nội', 'staff_nhan', 'staff12345', 1, ''),
(N'Trần Thị Lan', '1993-08-12', 0, '0961234567', 'lantt@staff.com', N'Hồ Chí Minh', 'staff_lan', 'staff12345', 1, ''),
(N'Admin Cửa Hàng', '1990-01-01', 1, '0999999999', 'admin@shop.com', N'Hà Nội', 'admin_sys', 'admin12345', 2, '');


-- Trạng thái đơn hàng
INSERT INTO TrangThaiDonHang (MaTT, TenTT, MoTa, ThuTu) VALUES
(0, N'Chờ xác nhận', N'Đơn hàng vừa được tạo', 1),
(1, N'Đã xác nhận', N'Nhân viên đã xác nhận đơn hàng', 2),
(2, N'Đang giao', N'Đơn hàng đang được vận chuyển', 3),
(3, N'Hoàn thành', N'Khách hàng đã nhận hàng', 4),
(4, N'Hủy', N'Đơn hàng đã bị hủy', 5);

-- Hãng (Chỉ nạp các hãng Laptop, Chuột, Phím cần thiết)
INSERT INTO Hang (TenHang) VALUES 
(N'Apple'), (N'Dell'), (N'Asus'), (N'Logitech'), -- Hãng cũ
(N'HP'), (N'Lenovo'), (N'MSI'), (N'Acer'), (N'Razer'); -- Hãng mới thêm

-- Loại sản phẩm (Chỉ 3 loại)
INSERT INTO LoaiSanPham (TenLoai) VALUES 
(N'Laptop'),        -- ID sẽ là 1
(N'Bàn phím'),      -- ID sẽ là 2
(N'Chuột máy tính'); -- ID sẽ là 3

-- Sản phẩm: 3 Bàn phím (MaLoai = 2)
INSERT INTO SanPham (TenSP, DonGiaBan, SoLuongTon, MoTa, ImgUrl, TrangThai, MaLoai, MaHang) VALUES
(N'Logitech G Pro X TKL Lightspeed', 3990000, 15, N'Bàn phím cơ không dây TKL Gaming Logitech', 'https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcT7w5wt2DmUk2w55KCwRqdeLufNMF0pQPv5bWQMAsYgHbgp-zxANp9D9y8Wi7AzALplSSTNQTVy6SjCBvKGgA4wDFXh_Ng-RyyL2oFHJzHU0kRT_ISz2YADR7EsK3HkxVImYkb6-rj-Gg&usqp=CAc', 1, 2, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Logitech')),
(N'Asus ROG Azoth', 5490000, 10, N'Bàn phím cơ 75% cao cấp, có màn hình OLED', 'https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcTwn7pk6ZJ8pLcg99rhLSkD6gutLkcJZ2WVVKQ4W14qqxtQ6LiApeoLKn0W44ZH93qfMi2wBpXElCBCy4-18dg5pTZC4FvK2AKXFGpyOK8DB7zweC6wrsMGKbsTLFg-NYtvfNxaiw&usqp=CAc', 1, 2, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Asus')),
(N'Apple Magic Keyboard (Touch ID)', 3590000, 12, N'Bàn phím không dây Apple với Touch ID', 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcR3EBaTFWa2IOiEqsbwWe8TazfJtRn0bEcR8JnfrS_m5adDtkGfYKZyRnyMZ73IeeyQcEjykgP_0bNxVLyl26IDYk-t4flUCXL-ImaCp-sU4BjAAth0NHj-G-w2BYQ9JNX4Npc4gkbWaw&usqp=CAc', 1, 2, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Apple'));

-- Sản phẩm: 3 Chuột (MaLoai = 3)
INSERT INTO SanPham (TenSP, DonGiaBan, SoLuongTon, MoTa, ImgUrl, TrangThai, MaLoai, MaHang) VALUES
(N'Logitech MX Master 3S', 2790000, 25, N'Chuột công thái học cao cấp cho dân văn phòng', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcR3tAXSQWSQOY8lazsS4yzrk42gV7-NsptXkITxlPJRKdjlMtSCRbs4tEmqkNpac3p1egVLYQir_VbRGxmJgv7QfZZOzG89bpqT8WJc_4cy7XkoBBCZyTNDAbq79TnzKLk-x0EPVlU&usqp=CAc', 1, 3, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Logitech')),
(N'Logitech G502 X Lightspeed', 3190000, 20, N'Chuột Gaming huyền thoại, phiên bản mới nhất', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcRjeZ3Q5XLX39HsjD0cc7zXfXRFReKyRX23YZxnoIJ3L1MS_iQqWxl1OQ6MLBBUZb0wNrcQogumzcSWDNs86UdwIVKRNb4NHMJQYeFRq1XSPSHDfATNOeddJd00VTgs1PQ6crfM8fI&usqp=CAc', 1, 3, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Logitech')),
(N'Apple Magic Mouse 3', 2590000, 15, N'Chuột cảm ứng đa điểm Apple', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTgDIhY8CIOO7nO4nF5gtLJbOjrziTEAZw6gA&s', 1, 3, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Apple'));

-- Sản phẩm: 20 Laptop (MaLoai = 1)
-- (5 Laptop cũ giữ lại + 15 Laptop mới thêm vào)
INSERT INTO SanPham (TenSP, DonGiaBan, SoLuongTon, MoTa, ImgUrl, TrangThai, MaLoai, MaHang) VALUES
(N'MacBook Pro M3 Max 16 inch', 65990000, 5, N'Laptop Apple mạnh mẽ nhất cho đồ họa', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcTlE78Gp9hq368C_HFAN5zeRJJayrThZhuQiirRV69YS1uPigkrZwbTZeA8twyMa9dScn1fwnOagGbNfA5qWJn2vDd_gAWqnz4OpEJPcDEozo3a2UDaQRoTgYAodMTGhvaC9jY0VV4&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Apple')),
(N'Dell XPS 15 (i9, 32GB)', 49990000, 8, N'Laptop Dell cao cấp, màn hình OLED', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcScl9gtGZNy5pP6ufywq8oC1nkb3DoOu45c7kOG75U-ZA-RyOX8QZMV648q5-KUZp32aFbK4Z7sQsHSNmEBLnou7BfS1m15-CLg_7Qd4SUGmawCxe3r1jBJU9LZEz4xl5C8UDAybT8&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Dell')),
(N'Asus ROG Zephyrus G14 (RTX 4070)', 42990000, 10, N'Laptop Gaming nhỏ gọn, hiệu suất cao', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcQ0bFDpf9YGj31wnWth6QJubuCSfOjM7wPglMhQ1YAgkcW9FVy-kQlMq9rAoxalc11_X3ZFoI7DBlcJ25IQ3kVYHPoMp6-prb9B-BhOYPBamLxAnopRygs_cZlwPn5F4AGMCTY_I5o&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Asus')),
(N'MacBook Air M2 13 inch', 23990000, 15, N'Laptop Apple mỏng nhẹ, pin cực trâu', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcQaV3IpAGUFbrqZGn0gEeYte5wYu7AHaLc-x1EEN4QYhC7UVGlM_D1I_LNkf_wIUAcYWOuppla-Em2zMemfUli1ZKF4fjV1CcLsCQmhJcc2q2lbbMNUo2PlH5Ji-yF1dVCAFQ8M5w&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Apple')),
(N'Dell Inspiron 14 Plus', 28990000, 12, N'Laptop Dell cho người sáng tạo nội dung', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcSQqBAQ1caIZEsaGcEaR1-BdGg0ygQ3LqTp7IoG9faxfmQKRjVs1Z4bNWCKstIl0PCMR3FrdyCHgrekIxehYQqelTPGPQcgjI68qAVjmVd-Io_v0nmTN7riP2kjCjUScRRLci5WtvI&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Dell')),
(N'HP Pavilion 15', 16500000, 10, N'Laptop học tập – văn phòng', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcRxrntDVaazKpxxSqea32kHp-Xh97v9XniDVWduomutRWkAOPPo9a-k-Y2BZ6QNvI4vftdpQNqnosQc8O7-V6vTb7qhQ0Ro40N0lVU_n20FHr9DUKQCEdmgdbmv3Muh72TpypN5VY8&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'HP')),
(N'HP Omen 16', 29500000, 5, N'Gaming mạnh mẽ', 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcSjkv_QcN30bpxa3iF4bfpg8fqymr8PPlpYxqbFGN2BTDdFvSlATupesSy_MUbUEqoEIlkLHDsfZ7pGfJhNvqH7fHEmTs5sv7cBFVPpVsnw6fZVdH-iaTR4LkF1HAgP1e7QmscjWg&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'HP')),
(N'HP Envy 13', 21500000, 7, N'Mỏng nhẹ cao cấp', 'https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcR3PMIQHsrIPgNNiTHl9cbjUw5NO1lzLN8dFiOB6HD49rEOZ6HpduXNunJKXC7y_DbYxTxeOSlaBoobeo5Mm-Ec5uX6gLolWwGH3_rkSDYrybG-90NJ8bOT9_HVNJtRBl6z4LNtMA&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'HP')),
(N'Lenovo IdeaPad 5', 15500000, 12, N'Văn phòng pin lâu', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcSkHwRZ96TDrdp_WSTQovhipe3r2a3YfWTvz41Nb-Wa_4QnkTTlcuVjAvoif5jAZsPK1Oclj0i1-9Hzh1ebm9B0wTpPfkq6iyk_h6LzZ3zjUYfiU2Gsqx9AYsPZk7GOgq2z11z40NU&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Lenovo')),
(N'Lenovo Legion 5', 32000000, 6, N'Gaming RTX 4060', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcQSIyvIwb7ynHquyS8oSanf4_X_EqQAn6U6Hll6Hdpxt7sam6waMNtnAL8N7WD2_iwWnKhNcKnLR3s5xtucgJdd-seBgsWbZ0pYpwp7lsRETYxP6221jugm8fi08P6TsEhvWakT1dE&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Lenovo')),
(N'Lenovo ThinkPad X1 Carbon', 42000000, 3, N'Doanh nhân siêu bền', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcQ6UOYSrxujOnPM8-hRDLmol0MO57-VOD0wJOtS2oLiVnf2GF2d7hNLMp6WJxHsbcFiHDkH_MabcJFXkMohV9-tJL_yNc0N_1ypuCWs1XausasaHFPtxBmn-IykYhjknOr933IzUA&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Lenovo')),
(N'MSI GF63', 18500000, 8, N'Gaming giá rẻ', 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcTSTidhSWLVNigXD73BssO5FPISsWlje-dSNhVB_z-mMnwGdtisYNgmtGen5VtSFR2ghfX14lvAzm_xkH4rX9KMIjLHEm6wuNP6pc4eGAMI_k3lqKYm6wuVg521muvfTCqhhGCTLjY&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'MSI')),
(N'MSI Katana 15', 27500000, 5, N'Gaming RTX 4050', 'https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcSZKZX_HquxhXS4A3_6-5ebYGOL1ansm7KpDkEEMfEq7BrAiE6AN_3jSRK_yosZf9zr-fDUeh3ktePrWQmk1zeqyMd7DmPDB10H69bgzGCwo-A9PH6qLJUFgSMmkv-py33mSCbw0g&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'MSI')),
(N'MSI Prestige 14', 23500000, 4, N'Đồ họa – doanh nhân', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcRy8UbwLK0IjM35SUKOgH0_EVqKy7UzvtCxzsc_e1DcwX4TmtsPKtaOcpjDiIgIJmundO06WdlcaeYeK6OtxmFmpKZuruHXagK_DgQPH-EANw0Gh0xNdKoCPpHY0d-9qY8JLf8zHGY&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'MSI')),
(N'Acer Aspire 7', 17000000, 9, N'Laptop mạnh', 'http://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcS21qiWy9XJqhCzeANO-z7rW4HTOIve9ibPSRyROOCJJ_ZURCX79yBMFlzjcKuCGmDV5iXjZhZWky6VPM0aOkY33mXlKcKheOaD2OoC7or7naH-ROJMzq7mhMB0cbrg9x_5KiXS0CI&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Acer')),
(N'Acer Nitro 5', 26000000, 6, N'Gaming tản nhiệt tốt', 'https://encrypted-tbn3.gstatic.com/shopping?q=tbn:ANd9GcRAw7gYnxIrxh6DScLvN0hkQTTMs9wIX-gPdfXv0c-KX4uvYSeEa6LTGYZ5in9LfqQxFHY7m1s81SWJ24o-Nco4wRoBP3df0wKF1rbGF4iMz9elbBMMhLVqYhyVgnrC56xX9pHJnIY&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Acer')),
(N'Acer Swift 3', 20000000, 10, N'Mỏng nhẹ pin trâu', 'https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcSKwaBukIy8ZOaRt1CZRaCjvTzU_6gc-PCHI4CQUFj8L0SIcX473QaYP-AaW_5PvjqDMCga-xXLf9fks6gJ-WbjUahmAIobttxbejvuy8jZO0rbWLq4vu0Md10pxeqQ1FCzEELwEQ&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Acer')),
(N'Razer Blade 14', 54000000, 2, N'RTX 4070 cao cấp', 'https://encrypted-tbn1.gstatic.com/shopping?q=tbn:ANd9GcSYmrWAbdcn8ouzRqZlwckijkOUFvaOBZnj9yGRWzzvfPEhkGhujwIsKKinLrCUFgSckHSrCYqG_u6Ru-YxJtCkZKbrtme0Tp8p4dJPcT0tIMMj_lgYa60Pbyf7tc3Rey6twgNom3U&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Razer')),
(N'Razer Blade 15', 59000000, 1, N'Đồ họa đỉnh cao', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcSYNJ8uTGpM4nATEvZL-XTqF5tCLWbaSBCoZH1AXtFwO0LEZHMcrkEIJk9aAzpGwV3MXufOknaKNWyAm0ieE4H8KpN4KGeQDoWxuTkV-pftFbhYkYuq3dvHIYgMxcnqK1R4ccIlzWI&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Razer')),
(N'Razer Book 13', 35000000, 3, N'Mỏng nhẹ cao cấp', 'https://encrypted-tbn0.gstatic.com/shopping?q=tbn:ANd9GcQkCPsM34fZfI7jtjcpJgtYLSmzG7Co42f4XMlsAO--RmHKICdxxXRZZO7778669yr9wHbQjnaPji9WhOU-nNTim9Z9RPN-tVpobAHKtQ5ssjQ_XdG8Lec1HwBit3QlNZRyxgHpGg&usqp=CAc', 1, 1, (SELECT TOP 1 MaHang FROM Hang WHERE TenHang = N'Razer'));




-- Giỏ hàng (Mẫu vài dòng khớp với sản phẩm mới)
INSERT INTO GioHang (UserId, MaSP, SoLuong) VALUES
(1, 1, 1),   -- Lê Hoàng Chấn Kiệt: Bàn phím Logitech
(2, 4, 1),   -- Phạm Đăng Khoa: Chuột Logitech
(4, 7, 1);   -- Nguyễn Tấn Dũng: MacBook Pro

-- Hóa đơn & Chi tiết (Mẫu vài dòng khớp với sản phẩm mới)
INSERT INTO HoaDon (NgayLap, NguoiDungId, NhanVienId, MaTT) VALUES
(GETDATE(), 4, NULL, 0); -- Đơn của Dũng

INSERT INTO ChiTietHoaDon (MaHD, MaSP, SoLuongBan, DonGia) VALUES
(1, 7, 1, 65990000); -- MacBook Pro

-- Kiểm tra kết quả
SELECT * FROM LoaiSanPham; -- Sẽ chỉ có 3 loại
SELECT TenSP, LoaiSanPham.TenLoai FROM SanPham JOIN LoaiSanPham ON SanPham.MaLoai = LoaiSanPham.MaLoai;


-------------------------------------------------- MẤY ANH TÀI NHỚ THÊM MẤY PHẦN NÀY DÙM EM VỚI ĐỌC HƯỚNG DẪN TẠO TÀI KHOẢN MẬT KHẨU MÃ HÓA Ở DƯỚI-----------------------------------------------------------------
--Thủ tục thêm
--thống kê doanh thu
CREATE PROCEDURE sp_ThongKeDoanhThu
AS
BEGIN
    SELECT 
        l.TenLoai AS tenLoai,
        CAST(SUM(ct.SoLuongBan * ct.DonGia) AS FLOAT) AS tongDoanhThu, -- Ép kiểu về Float để Java hứng Double
        SUM(ct.SoLuongBan) AS tongSoLuong,
        CAST(MAX(ct.DonGia) AS FLOAT) AS giaCaoNhat,
        CAST(MIN(ct.DonGia) AS FLOAT) AS giaThapNhat,
        CAST(AVG(ct.DonGia) AS FLOAT) AS giaTrungBinh
    FROM ChiTietHoaDon ct
    JOIN SanPham sp ON ct.MaSP = sp.MaSP
    JOIN LoaiSanPham l ON sp.MaLoai = l.MaLoai
    JOIN HoaDon hd ON ct.MaHD = hd.MaHD
    WHERE hd.maTT = 3 -- Chỉ tính đơn hoàn thành
    GROUP BY l.TenLoai;
END;


--khách vip
CREATE PROCEDURE sp_TopKhachHangVIP
AS
BEGIN
    SELECT TOP 10
        u.HoTen AS tenKhachHang,
        CAST(SUM(ct.SoLuongBan * ct.DonGia) AS FLOAT) AS tongTienDaMua,
        MIN(hd.NgayLap) AS ngayMuaDauTien,
        MAX(hd.NgayLap) AS ngayMuaSauCung
    FROM HoaDon hd
    JOIN ChiTietHoaDon ct ON hd.MaHD = ct.MaHD
    JOIN Users u ON hd.NguoiDungID = u.UserId
    WHERE hd.maTT = 3
    GROUP BY u.HoTen
    ORDER BY tongTienDaMua DESC;
END;


-- Không đăng nhập được bằng tài khoản ở trên dữ liệu cũ đâu vì chưa mã hóa đấy,
-- tự chạy Backend + Frontend xong đăng ký 1 tài khoản mới là vào được
-- nếu như muốn tài khoản mới tạo là role admin thì làm theo hướng dẫn ở dưới

-- đổi tên đăng nhập lại thành tài khoản vừa đăng ký để chỉnh thành role admin
UPDATE Users 
SET RoleId = 2-- không chỉnh cái này
WHERE TenDangNhap = 'khoaMaHoa';-- thay thành tên đăng nhập chứ kh phải hoTen nha mấy anh tài 

------------------------------------------------------------------------------------------------------------------------------------------------
select * from Users
-- Tạo bảng roles
CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255)
);

-- Chèn dữ liệu vào bảng roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Quản trị viên hệ thống'),
('USER', 'Người dùng thông thường')
ON CONFLICT DO NOTHING;

-- Tạo bảng users
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    phone_number VARCHAR(255),
    avatar VARCHAR(255),
    role_id INTEGER NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Chèn dữ liệu vào bảng users
INSERT INTO users (email, password, full_name, address, phone_number, avatar, role_id) VALUES
('ninh2k4@gmail.com', '$2a$10$.o9Dbz5ldf2GOX8abi/e/OQC0TPVceDq2qajtl2iQUGeMD1jFcVRK', 'Hoài Ninh', '123 Đường Admin, TP.HCM', '0123456789', '/uploads/images/avatar/1739691800734-Screenshot 2025-02-16 102449.png', 1),
('guest@gmail.com', '$2a$10$.o9Dbz5ldf2GOX8abi/e/OQC0TPVceDq2qajtl2iQUGeMD1jFcVRK', 'Người dùng', '456 Đường User, Hà Nội', '0987654321', '/uploads/images/avatar/174005473758-Screenshot 2024-05-27 154415.png', 2)
ON CONFLICT DO NOTHING;

-- Tạo bảng products
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    image VARCHAR(255),
    detail_desc TEXT NOT NULL,
    short_desc VARCHAR(255) NOT NULL,
    quantity BIGINT NOT NULL,
    sold BIGINT DEFAULT 0,
    factory VARCHAR(255),
    target VARCHAR(255)
);

-- Chèn 10 bản ghi vào bảng products
INSERT INTO products (name, price, image, detail_desc, short_desc, quantity, sold, factory, target) VALUES
('Lenovo ThinkPad 11', 45990000, '/uploads/images/product/1740193180086-lenovo_x1_carbon.jpg', 'Laptop doanh nhân, Intel Core i7, RAM 16GB, SSD 1TB, bàn phím chống nước', 'Intel Core i7-1365U', 45, 12, 'LENOVO', 'DOANH-NHAN'),
('DELL XPS 15', 51990000, '/uploads/images/product/174019496408-dell_xps_15.jpg', 'Màn hình 15.6 inch OLED, Intel Core i7, RAM 16GB, card RTX 3050', 'Intel Core i9-13900H', 25, 8, 'DELL', 'THIET-KE-DO-HOA'),
('ASUS ROG Strix G16', 42990000, '/uploads/images/product/174019508239-asus_rog_strix_g16.jpg', 'Laptop gaming, AMD Ryzen 9, RTX 4070, màn 165Hz', 'Intel Core i7-13650HX', 30, 15, 'ASUS', 'GAMING'),
('Acer Nitro 5 AN515', 29990000, '/uploads/images/product/174019574883-acer_nitro_5.jpg', 'Laptop gaming, Intel Core i5, RTX 3060, RAM 16GB', 'Intel Core i5-12500H', 40, 20, 'ACER', 'GAMING'),
('ASUS VivoBook 15', 13990000, '/uploads/images/product/1740195847675-asus_vivobook_15.jpg', 'Laptop phổ thông, AMD Ryzen 5, RAM 8GB, SSD 512GB', 'Intel Core i3-1215U', 80, 25, 'ASUS', 'SINHVIEN-VANPHONG'),
('LG Gram 17', 41990000, '/uploads/images/product/1740196603432-lg_gram_17.jpg', 'Laptop siêu nhẹ, Intel Core i5, RAM 16GB, màn 17 inch', 'Intel Core i7-1360P', 35, 10, 'LG', 'MONG-NHE'),
('LG Gram 14', 19990000, '/uploads/images/product/1740197007634-lg_gram_14.jpg', 'Laptop siêu nhẹ, Intel Core i7, RAM 8GB, màn 14 inch', 'Intel Core i5-1240P', 50, 15, 'LG', 'SINHVIEN-VANPHONG'),
('DELL Inspiron 15', 15990000, '/uploads/images/product/1740209914533-dell_inspiron_15.jpg', 'Laptop phổ thông, Intel Core i5, RAM 8GB, SSD 256GB', 'AMD Ryzen 5 5500U', 90, 30, 'DELL', 'SINHVIEN-VANPHONG'),
('MacBook Pro M2', 69990000, '/uploads/images/product/1740210184225-macbook_pro_m2.jpg', 'Chip M2 Max, RAM 32GB, SSD 1TB, Retina Display', 'Apple M2 Pro', 20, 5, 'APPLE', 'THIET-KE-DO-HOA'),
('Acer Aspire 5', 12990000, '/uploads/images/product/1740211005350-acer_aspire_5.jpg', 'Laptop phổ thông, AMD Ryzen 3, RAM 8GB, SSD 512GB', 'Intel Core i5-1235U', 100, 40, 'ACER', 'SINHVIEN-VANPHONG')
ON CONFLICT DO NOTHING;

-- Tạo bảng orders
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    total_price DOUBLE PRECISION,
    receiver_name VARCHAR(255),
    receiver_address VARCHAR(255),
    receiver_phone VARCHAR(255),
    status VARCHAR(255),
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tạo bảng order_detail
CREATE TABLE IF NOT EXISTS order_detail (
    id SERIAL PRIMARY KEY,
    quantity BIGINT,
    price DOUBLE PRECISION,
    order_id INTEGER,
    product_id INTEGER,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Tạo bảng carts
CREATE TABLE IF NOT EXISTS carts (
    id SERIAL PRIMARY KEY,
    sum INTEGER,
    user_id INTEGER,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Tạo bảng cart_detail
CREATE TABLE IF NOT EXISTS cart_detail (
    id SERIAL PRIMARY KEY,
    quantity BIGINT,
    price DOUBLE PRECISION,
    cart_id INTEGER,
    product_id INTEGER,
    FOREIGN KEY (cart_id) REFERENCES carts(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
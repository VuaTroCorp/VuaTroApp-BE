INSERT INTO room_type (type_name) VALUES ('Phòng Trọ');
INSERT INTO room_type (type_name) VALUES ('Căn Hộ');
INSERT INTO room_type (type_name) VALUES ('Kí Túc Xá');

INSERT INTO users (username, email, password, phone, provider, provider_id, status, role)
VALUES
('hh', 'hh@gmail.com', '$2a$10$h7I9JzDD12gQoANhR3CV8eJUo01c9Pa3vl2ic/.J1pGXYhOK1R02y', '0900000001', 'LOCAL', NULL, 'ACTIVE', 'USER'),
('huy', 'huy@gmail.com', '$2a$10$h7I9JzDD12gQoANhR3CV8eJUo01c9Pa3vl2ic/.J1pGXYhOK1R02y', '0900000002', 'LOCAL', NULL, 'ACTIVE', 'USER'),
('admin', 'admin@gmail.com', '$2a$10$h7I9JzDD12gQoANhR3CV8eJUo01c9Pa3vl2ic/.J1pGXYhOK1R02y', '0900000003', 'LOCAL', NULL, 'ACTIVE', 'ADMIN'),
('linh', 'linh@gmail.com', '$2a$10$h7I9JzDD12gQoANhR3CV8eJUo01c9Pa3vl2ic/.J1pGXYhOK1R02y', '0900000004', 'LOCAL', NULL, 'ACTIVE', 'USER'),
('khanh', 'khanh@gmail.com', '$2a$10$h7I9JzDD12gQoANhR3CV8eJUo01c9Pa3vl2ic/.J1pGXYhOK1R02y', '0900000005', 'LOCAL', NULL, 'ACTIVE', 'USER');


INSERT INTO post 
(title, price, status, area, room_quantity, address, description, user_id, type_id, created_at, updated_at)
VALUES

('Phòng trọ gần ĐH Nha Trang', 1500000, 'APPROVED', 20, 1,
'Nha Trang',
'Phòng trọ sạch sẽ, gần trường đại học Nha Trang',1, 1, NOW(), NOW()),

('Căn hộ mini trung tâm', 3500000, 'APPROVED', 35, 2,
'Trần Phú, Nha Trang',
'Căn hộ mini đầy đủ nội thất, gần biển', 5, 2, NOW(), NOW()),

('Phòng trọ giá rẻ sinh viên', 1000000, 'APPROVED', 18, 1,
'Vĩnh Hải, Nha Trang',
'Phòng nhỏ phù hợp sinh viên, có wifi', 4, 1, NOW(), NOW()),

('Phòng trọ có gác', 2000000, 'APPROVED', 25, 1,
'Lê Hồng Phong, Nha Trang',
'Phòng trọ có gác lửng, rộng rãi thoáng mát', 2, 1, NOW(), NOW()),

('Căn hộ cao cấp', 5000000, 'APPROVED', 50, 2,
'Phạm Văn Đồng, Nha Trang',
'Căn hộ view biển, đầy đủ nội thất cao cấp',3, 2, NOW(), NOW());
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

-- 5 bài ban đầu
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
'Căn hộ view biển, đầy đủ nội thất cao cấp',3, 2, NOW(), NOW()),

-- 10 bài thêm
('Phòng trọ gần chợ Đầm', 1800000, 'APPROVED', 22, 1,
'Chợ Đầm, Nha Trang',
'Phòng trọ gần chợ, thuận tiện sinh hoạt', 1, 1, NOW(), NOW()),

('Căn hộ mini gần biển', 4000000, 'APPROVED', 30, 1,
'Trần Phú, Nha Trang',
'Căn hộ gần biển, view đẹp, đầy đủ tiện nghi', 2, 2, NOW(), NOW()),

('Phòng trọ giá rẻ', 900000, 'APPROVED', 15, 1,
'Vĩnh Trường, Nha Trang',
'Phòng nhỏ, giá rẻ, phù hợp sinh viên', 3, 1, NOW(), NOW()),

('Phòng trọ mới xây', 2200000, 'APPROVED', 28, 1,
'Nguyễn Thiện Thuật, Nha Trang',
'Phòng mới xây, sạch sẽ, an ninh tốt', 4, 1, NOW(), NOW()),

('Căn hộ 1 phòng ngủ', 4500000, 'APPROVED', 40, 1,
'Phước Long, Nha Trang',
'Căn hộ rộng rãi, có ban công', 5, 2, NOW(), NOW()),

('Phòng trọ có máy lạnh', 2500000, 'APPROVED', 20, 1,
'Ngô Gia Tự, Nha Trang',
'Phòng có máy lạnh, wifi mạnh', 1, 1, NOW(), NOW()),

('Phòng trọ gần bến xe', 1600000, 'APPROVED', 18, 1,
'Phía Bắc Nha Trang',
'Gần bến xe, thuận tiện đi lại', 2, 1, NOW(), NOW()),

('Căn hộ full nội thất', 4800000, 'APPROVED', 45, 2,
'Vĩnh Điềm Trung, Nha Trang',
'Căn hộ đầy đủ nội thất, chỉ việc vào ở', 3, 2, NOW(), NOW()),

('Phòng trọ rộng rãi', 2100000, 'APPROVED', 26, 1,
'Lê Thành Phương, Nha Trang',
'Phòng rộng, thoáng, có chỗ để xe', 4, 1, NOW(), NOW()),

('Căn hộ view thành phố', 4200000, 'APPROVED', 38, 1,
'Trung tâm Nha Trang',
'Căn hộ view đẹp, gần trung tâm', 5, 2, NOW(), NOW());
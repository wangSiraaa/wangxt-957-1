-- 初始化数据
INSERT INTO ward (ward_code, ward_name, bed_count, is_isolation, max_accompany_per_bed, status) VALUES
('WARD001', '内科一病区', 40, 0, 1, 1),
('WARD002', '内科二病区', 35, 0, 1, 1),
('WARD003', '外科一病区', 45, 0, 1, 1),
('WARD004', 'ICU重症监护室', 20, 0, 1, 1),
('WARD005', '隔离病区', 30, 1, 1, 1),
('WARD006', '儿科病区', 25, 0, 2, 1);

INSERT INTO patient (patient_no, patient_name, id_card, gender, age, ward_id, bed_no, admission_date, expected_discharge_date, diagnosis, status) VALUES
('P20240001', '张三', '110101199001011234', 1, 34, 1, '01-01', '2024-01-15', '2024-01-25', '高血压', 1),
('P20240002', '李四', '110101198505055678', 2, 39, 1, '01-02', '2024-01-16', '2024-01-20', '糖尿病', 1),
('P20240003', '王五', '110101197808089012', 1, 46, 3, '03-05', '2024-01-10', '2024-01-30', '阑尾炎术后', 1);

INSERT INTO accompany_person (person_name, id_card, gender, phone, relation, id_card_expire_date, address) VALUES
('张小三', '110101201001011234', 1, '13800138001', '父子', '2030-01-01', '北京市朝阳区'),
('李小花', '110101200505055678', 2, '13800138002', '母女', '2025-05-05', '北京市海淀区'),
('王大锤', '110101199508089012', 1, '13800138003', '兄弟', '2028-08-08', '北京市西城区');

INSERT INTO sys_user (user_name, real_name, password, role_type, ward_id, phone, status) VALUES
('nurse001', '护士小王', '123456', 1, 1, '13900139001', 1),
('nurse002', '护士小李', '123456', 1, 3, '13900139002', 1),
('gate001', '门禁老张', '123456', 2, NULL, '13900139003', 1),
('admin', '管理员', '123456', 3, NULL, '13900139000', 1);

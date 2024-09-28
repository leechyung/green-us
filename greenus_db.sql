CREATE TABLE `admin` (
`admin_seq` INT NOT NULL PRIMARY KEY,
`admin_id` VARCHAR(200) NULL,
`admin_pwd` VARCHAR(200) NULL
);

CREATE TABLE `user` (
`user_seq` INT NOT NULL PRIMARY KEY,
`user_name`  VARCHAR(20) NULL.
`user_pwd` VARCHAR(200) NULL,
`user_email` VARCHAR(200) NULL,
`user_addr` VARCHAR(200) NULL,
`user_phone` VARCHAR(200) NULL,
`user_account` VARCHAR(200) NULL,
`user_w_count` INT,
`admin_seq` INT NOT NULL,
FOREIGN KEY (`admin_seq`) REFERENCES `admin`(`admin_seq`)
);

CREATE TABLE `greening` (
`g_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NULL,
`g_name` VARCHAR(200) NULL,
`g_start_date` DATE NULL,
`g_end_date` DATE NULL,
`g_certi_way` VARCHAR(200) NULL,
`g_info` VARCHAR(200) NULL,
`g_member_num` INT NULL,
`g_freq` VARCHAR(200) NULL,
`g_deposit` INT NULL,
`g_total_count` INT NULL,
`g_number` INT NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`)
);

CREATE TABLE `participate` (
`p_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NULL,
`g_seq` INT NULL,
`p_complete` VARCHAR(200) NULL,
`p_count` INT NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`),
FOREIGN KEY (`g_seq`) REFERENCES `greening`(`g_seq`)
);

CREATE TABLE `certify` (
`certify_seq` INT NOT NULL PRIMARY KEY,
`certify_img` VARCHAR(200) NULL,
`certify_date` DATE NULL,
`user_seq` INT NULL,
`g_seq` INT NULL,
`p_seq` INT NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`),
FOREIGN KEY (`g_seq`) REFERENCES `greening`(`g_seq`),
FOREIGN KEY (`p_seq`) REFERENCES `participate`(`p_seq`)
);

CREATE TABLE `review` (
`review_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NOT NULL,
`g_seq` INT NOT NULL,
`review_content` VARCHAR(200) NULL,
`review_date` DATE NULL,
`review_rate` FLOAT NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`),
FOREIGN KEY (`g_seq`) REFERENCES `greening`(`g_seq`)
);

CREATE TABLE `notice` (
`notice_seq` INT NOT NULL PRIMARY KEY,
`admin_seq` INT NULL,
`notice_content` VARCHAR(200) NULL,
`notice_date` DATE NULL,
FOREIGN KEY (`admin_seq`) REFERENCES `admin`(`admin_seq`)
);

CREATE TABLE `prize` (
`prize_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NULL,
`g_seq` INT NULL,
`p_seq` INT NULL,
`prize_name` VARCHAR(200) NULL,
`prize_money` INT NULL,
`prize_date` DATE NULL,
FOREIGN KEY (`user_seq`) REFERENCES `participate`(`user_seq`),
FOREIGN KEY (`g_seq`) REFERENCES `participate`(`g_seq`),
FOREIGN KEY (`p_seq`) REFERENCES `participate`(`p_seq`)
);

CREATE TABLE `Ranking` (
`rank_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`)
);

CREATE TABLE `withdraw` (
`withdraw_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NULL,
`withdraw_content` VARCHAR(200) NULL,
`withdraw_date` DATE NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`)
);

CREATE TABLE `payment` (
`payment_seq` INT NOT NULL PRIMARY KEY,
`user_seq` INT NULL,
`payment_content` VARCHAR(200) NULL,
`payment_method` VARCHAR(200) NULL,
`payment_date` DATE NULL,
`payment_money` INT NULL,
FOREIGN KEY (`user_seq`) REFERENCES `user`(`user_seq`)
);

CREATE TABLE `report` (
`report_seq` INT NOT NULL PRIMARY KEY,
`certify_seq` INT NULL,
`report_date` DATE NULL,
`report_result` VARCHAR(200) NULL,
FOREIGN KEY (`certify_seq`) REFERENCES `certify`(`certify_seq`)
);
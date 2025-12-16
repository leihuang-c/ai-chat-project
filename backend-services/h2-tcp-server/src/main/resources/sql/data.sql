-- 插入一些测试数据 (可选)
MERGE INTO `user` (`username`, `password`, `nickname`, `email`) 
KEY(`username`)
VALUES ('testuser', '$2a$10$abcdefghijklmnopqrstuv', '测试用户', 'test@example.com');


select username, password, nickname, email from "USER" where username = 'testuser';

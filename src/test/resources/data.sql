INSERT INTO users(email, name) VALUES ('updateName@user.com','updateName');
INSERT INTO users(email, name) VALUES ('user@user.com','user');
INSERT INTO users(email, name) VALUES ('other@other.com','other');
INSERT INTO users(email, name) VALUES ('practicum@yandex.ru','practicum');

INSERT INTO item_requests(description, requester_id, created)	VALUES ('Хотел бы воспользоваться щёткой для обуви',1,'2023-04-03 15:34:48');
INSERT INTO item_requests(description, requester_id, created)	VALUES ('Хотел бы воспользоваться мясорубкой',2,'2023-04-02 12:34:48');
INSERT INTO item_requests(description, requester_id, created)	VALUES ('Хотел бы воспользоваться бензопилой',4,'2023-03-23 10:34:48');

INSERT INTO items(description, name, available, owner_id, request_id)VALUES ('Аккумуляторная дрель + аккумулятор','Аккумуляторная дрель',true,1,3);
INSERT INTO items(description, name, available, owner_id, request_id)VALUES ('Аккумуляторная отвертка','Отвертка',true,4,NULL);
INSERT INTO items(description, name, available, owner_id, request_id)VALUES ('Тюбик суперклея марки Момент','Клей Момент',true,4,2);
INSERT INTO items( description, name, available, owner_id, request_id)VALUES ('Стол для празднования','Кухонный стол',true,3,NULL);
INSERT INTO items(description, name, available, owner_id, request_id)VALUES ('Стандартная щётка для обуви','Щётка для обуви',true,4,1);

INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-03 15:34:34','2023-04-03 15:34:35','APPROVED',1,2);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-04 15:34:32','2023-04-05 15:34:32','CANCELED',1,2);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-04 15:34:34','2023-04-04 16:34:34','REJECTED',4,1);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-03 16:34:34','2023-04-03 17:34:34','APPROVED',3,2);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-03 15:34:42','2023-04-04 15:34:39','REJECTED',1,3);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-03 15:34:42','2023-04-03 15:34:43','WAITING',1,2);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-13 15:34:40','2023-04-14 15:34:40','APPROVED',3,1);
INSERT INTO bookings(booking_start, booking_end, booking_status, booker_id, item_id) VALUES ('2023-04-03 15:34:42','2023-04-03 16:34:40','WAITING',1,4);

INSERT INTO comments(text, item, author, created) VALUES ('Add comment from user1',2,1,'2023-04-03 15:34:46');
INSERT INTO comments(text, item, author, created) VALUES ('Add comment from user2',3,2,'2023-03-23 15:34:46');
INSERT INTO comments(text, item, author, created) VALUES ('Add comment from user3',4,3,'2023-04-01 15:34:46');
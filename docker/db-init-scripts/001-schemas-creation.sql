CREATE DATABASE IF NOT EXISTS items_db;
CREATE USER IF NOT EXISTS 'items_user'@'%' IDENTIFIED BY 'items_password';
GRANT ALL PRIVILEGES ON items_db.* TO 'items_user'@'%';

CREATE DATABASE IF NOT EXISTS users_db;
CREATE USER IF NOT EXISTS 'users_user'@'%' IDENTIFIED BY 'users_password';
GRANT ALL PRIVILEGES ON users_db.* TO 'users_user'@'%';

CREATE DATABASE IF NOT EXISTS notifications_db;
CREATE USER IF NOT EXISTS 'notifications_user'@'%' IDENTIFIED BY 'notifications_password';
GRANT ALL PRIVILEGES ON notifications_db.* TO 'notifications_user'@'%';

FLUSH PRIVILEGES;
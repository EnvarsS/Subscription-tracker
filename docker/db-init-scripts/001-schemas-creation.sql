CREATE DATABASE IF NOT EXISTS items_db;
CREATE USER IF NOT EXISTS 'items_user'@'%' IDENTIFIED BY 'items_pass';
GRANT ALL PRIVILEGES ON items_db.* TO 'items_user'@'%';

CREATE DATABASE IF NOT EXISTS users_db;
CREATE USER IF NOT EXISTS 'users_user'@'%' IDENTIFIED BY 'users_password';
GRANT ALL PRIVILEGES ON users_db.* TO 'users_user'@'%';

FLUSH PRIVILEGES;
/* mysql -u root -p*/

DROP USER IF EXISTS 'user'@'%';
CREATE USER IF NOT EXISTS 'user'@'%' IDENTIFIED BY 'user';

DROP SCHEMA IF EXISTS spootifydb;
CREATE SCHEMA spootifydb;

GRANT ALL ON spootifydb.* TO 'user'@'%';
FLUSH PRIVILEGES;

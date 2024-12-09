/*
 * This sql file inserts a new user login entry to the database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
INSERT INTO user_login(username, password_hash, password_salt) VALUES(?, ?, ?);

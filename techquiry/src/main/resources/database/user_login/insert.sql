/*
 * This sql file selects the count of user login entries in the database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
INSERT INTO user_login(user_id, username, password_hash, password_salt) VALUES(?, ?, ?, ?);

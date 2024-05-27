/*
 * This sql file selects the count of user entries within the application
 * database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
INSERT INTO users(user_id, username, display_name, password_hash, password_salt) VALUES(?, ?, ?, ?, ?);

/*
 * This sql file selects the count of user entries within the application
 * database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
UPDATE users SET username = ?, display_name = ?, password_hash = ?, password_salt = ? WHERE user_id = ?;

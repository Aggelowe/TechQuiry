/*
 * This sql file updates the only user login entry that has the given username
 * with the given values.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
UPDATE user_login SET username = ?, password_hash = ?, password_salt = ? WHERE user_id = ?;

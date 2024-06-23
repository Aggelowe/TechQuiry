/*
 * This sql file deletes the user login entry with the given user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
DELETE FROM user_login WHERE user_id = ?;

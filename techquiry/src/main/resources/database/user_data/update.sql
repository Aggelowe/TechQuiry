/*
 * This sql file updates the only user data entry that has the given user id
 * with the given values.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
UPDATE user_data SET first_name = ?, last_name = ?, icon = ? WHERE user_id = ?;

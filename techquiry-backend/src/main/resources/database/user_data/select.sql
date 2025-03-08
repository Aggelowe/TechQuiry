/*
 * This sql file selects the only user data entry with the given user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM user_data WHERE user_id = ? LIMIT 1;

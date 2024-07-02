/*
 * This sql file selects the only user entry with the given user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM user_login WHERE user_id = ? LIMIT 1;

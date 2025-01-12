/*
 * This sql file selects the user login entries with the user id in the observer
 * entries with the given inquiry id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM user_login WHERE user_id IN (SELECT user_id FROM observer WHERE inquiry_id = ?);
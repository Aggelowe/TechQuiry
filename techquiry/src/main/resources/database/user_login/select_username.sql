/*
 * This sql file selects the only user login entry with the given username.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM user_login WHERE username = ? LIMIT 1;

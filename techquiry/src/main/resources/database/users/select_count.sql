/*
 * This sql file selects the count of user entries within the application
 * database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
SELECT COUNT(user_id) AS users_count FROM users;

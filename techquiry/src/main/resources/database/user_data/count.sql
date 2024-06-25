/*
 * This sql file selects the count of entries within the user_data table.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
SELECT COUNT(user_id) AS users_count FROM user_data LIMIT 1;

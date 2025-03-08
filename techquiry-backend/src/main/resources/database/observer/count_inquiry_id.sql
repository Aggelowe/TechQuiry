/*
 * This sql file selects the count of observer entries with the given inquiry id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT COUNT(user_id) AS observer_count FROM observer WHERE inquiry_id = ?;

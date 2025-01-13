/*
 * This sql file checks whether the observer entry with the given information
 * exists.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT COUNT(*) AS exist FROM observer WHERE inquiry_id = ? AND user_id = ?;

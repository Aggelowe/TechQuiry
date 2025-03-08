/*
 * This sql file selects the count of response entries with the given inquiry id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT COUNT(response_id) AS response_count FROM response WHERE inquiry_id = ?;
/*
 * This sql file selects the only response entry with the given response id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM response WHERE response_id = ? LIMIT 1;

/*
 * This sql file selects the only inquiry entry with the given inquiry id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM inquiry WHERE inquiry_id = ? LIMIT 1;

/*
 * This sql file selects the inquiry entries with the inquiry id in the observer
 * entries with the given user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM inquiry WHERE inquiry_id IN (SELECT inquiry_id FROM observer WHERE user_id = ?);


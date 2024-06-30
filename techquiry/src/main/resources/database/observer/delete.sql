/*
 * This sql file deletes the observer entry with the given inquiry id and user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
DELETE FROM observer WHERE inquiry_id = ? AND user_id = ?;

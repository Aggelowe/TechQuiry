/*
 * This sql file updates the only inquiry entry that has the given inquiry id
 * with the given values.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
UPDATE inquiry SET user_id = ?, title = ?, content = ?, anonymous = ? WHERE inquiry_id = ?;

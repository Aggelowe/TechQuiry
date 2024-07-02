/*
 * This sql file updates the only response entry that has the given response id
 * with the given values.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
UPDATE response SET inquiry_id = ?, user_id = ?, anonymous = ?, content = ? WHERE response_id = ?;

/*
 * This sql file deletes the upvote entry with the given response id and user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
DELETE FROM upvote WHERE response_id = ? AND user_id = ?;

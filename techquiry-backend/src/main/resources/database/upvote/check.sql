/*
 * This sql file checks whether the upvote entry with the given information
 * exists.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT COUNT(*) AS exist FROM upvote WHERE response_id = ? AND user_id = ?;

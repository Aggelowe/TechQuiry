/*
 * This sql file selects the count of upvote entries with the given response id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT COUNT(user_id) AS upvote_count FROM upvote WHERE response_id = ?;

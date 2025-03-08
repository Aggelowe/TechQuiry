/*
 * This sql file selects the response entries with the response id in the upvote
 * entries with the given user id.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM response WHERE response_id IN (SELECT response_id FROM upvote WHERE user_id = ?);

/*
 * This sql file inserts a new response entry to the database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
INSERT INTO response(inquiry_id, user_id, anonymous, content) VALUES(?, ?, ?, ?);
SELECT last_insert_rowid() AS response_id;

/*
 * This sql file inserts a new inquiry entry to the database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
INSERT INTO inquiry(inquiry_id, user_id, title, content, anonymous) VALUES(?, ?, ?, ?, ?);

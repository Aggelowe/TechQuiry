/*
 * This sql file inserts a new inquiry entry to the database.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
INSERT INTO inquiry(user_id, title, content, anonymous) VALUES(?, ?, ?, ?);
SELECT last_insert_rowid() AS inquiry_id;

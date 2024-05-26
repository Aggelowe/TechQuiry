/*
 * This sql file creates the user table inside the application database.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
CREATE TABLE IF NOT EXISTS users (
	user_id 		INTEGER 	PRIMARY KEY,
	username 		TEXT 		NOT NULL UNIQUE,
	display_name 	TEXT		,
	password_hash 	TEXT 		NOT NULL,
	password_salt	TEXT		NOT NULL
);

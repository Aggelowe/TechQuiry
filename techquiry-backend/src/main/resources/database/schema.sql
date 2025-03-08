/*
 * This sql file applies the TechQuiry database schema to the application's database.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
CREATE TABLE IF NOT EXISTS "user_login" (
	"user_id" INTEGER NOT NULL UNIQUE,
	"username" TEXT NOT NULL UNIQUE,
	"password_hash" TEXT NOT NULL,
	"password_salt" TEXT NOT NULL,
	PRIMARY KEY("user_id")	
);

CREATE TABLE IF NOT EXISTS "user_data" (
	"user_id" INTEGER NOT NULL UNIQUE,
	"first_name" TEXT NOT NULL,
	"last_name" TEXT NOT NULL,
	"icon" BLOB,
	PRIMARY KEY("user_id"),
	FOREIGN KEY ("user_id") REFERENCES "user_login"("user_id")
	ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "inquiry" (
	"inquiry_id" INTEGER NOT NULL UNIQUE,
	"user_id" INTEGER NOT NULL,
	"title" TEXT NOT NULL,
	"content" TEXT NOT NULL,
	"anonymous" INTEGER NOT NULL,
	PRIMARY KEY("inquiry_id"),
	FOREIGN KEY ("user_id") REFERENCES "user_login"("user_id")
	ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "response" (
	"response_id" INTEGER NOT NULL UNIQUE,
	"inquiry_id" INTEGER NOT NULL,
	"user_id" INTEGER NOT NULL,
	"anonymous" INTEGER NOT NULL,
	"content" TEXT NOT NULL,
	PRIMARY KEY("response_id"),
	FOREIGN KEY ("inquiry_id") REFERENCES "inquiry"("inquiry_id")
	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY ("user_id") REFERENCES "user_login"("user_id")
	ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "observer" (
	"inquiry_id" INTEGER NOT NULL,
	"user_id" INTEGER NOT NULL,
	PRIMARY KEY("inquiry_id", "user_id"),
	FOREIGN KEY ("inquiry_id") REFERENCES "inquiry"("inquiry_id")
	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY ("user_id") REFERENCES "user_login"("user_id")
	ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "upvote" (
	"response_id" INTEGER NOT NULL,
	"user_id" INTEGER NOT NULL,
	PRIMARY KEY("response_id", "user_id"),
	FOREIGN KEY ("response_id") REFERENCES "response"("response_id")
	ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY ("user_id") REFERENCES "user_login"("user_id")
	ON UPDATE CASCADE ON DELETE CASCADE
);

/*
 * This sql file selects the inquiry entries with the given user
 * id which are also not anonymous.
 * 
 * Author: Aggelowe
 * Since: 0.0.1
 */
SELECT * FROM inquiry WHERE user_id = ? AND anonymous = 0;

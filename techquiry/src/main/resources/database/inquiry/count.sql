/*
 * This sql file selects the count of entries within the inquiry table.
 * 
 * Author: Aggelowe 
 * Since: 0.0.1
 */
SELECT COUNT(inquiry_id) AS inquiry_count FROM inquiry LIMIT 1;

package com.aggelowe.techquiry.database;

import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* 
 * The {@link LocalResult} class provides a way for data of a {@link ResultSet}
 * to be copied to memory and thus not depend on the connection with the
 * database.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
public class LocalResult implements Iterable<Map<String, Object>> {

	/**
	 * The copied result data as an array list, where each contained map represents
	 * a row of the result table.
	 */
	private final List<Map<String, Object>> result;

	/**
	 * This constructor constructs a new {@link LocalResult} instance with the
	 * provided parameter as the result set data.
	 * 
	 * @param result The result data
	 */
	private LocalResult(List<Map<String, Object>> result) {
		this.result = result;
	}

	/**
	 * This method returns a copy of the list containing the result data.
	 * 
	 * @return A copy of the result data
	 */
	public List<Map<String, Object>> list() {
		return new ArrayList<>(result);
	}

	/**
	 * This method returns an iterator that iterates through the rows/maps of the
	 * result. Any changes made by the iterator will NOT affect the result.
	 * 
	 * @return An iterator of the data
	 */
	@Override
	public Iterator<Map<String, Object>> iterator() {
		return list().iterator();
	}

	/**
	 * This method constructs a new {@link LocalResult} object that contains the
	 * copied data from the given result set.
	 * 
	 * @param resultSet The result set to copy the data from
	 * @return The constructed local result
	 * @throws SQLException If an error occurs while copying the result set
	 */
	public static LocalResult of(ResultSet resultSet) throws SQLException {
		if (resultSet == null) {
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<>();
		ResultSetMetaData meta = resultSet.getMetaData();
		int columns = meta.getColumnCount();
		while (resultSet.next()) {
			Map<String, Object> row = new HashMap<>();
			for (int i = 1; i <= columns; i++) {
				String label = meta.getColumnLabel(i);
				int type = meta.getColumnType(i);
				Object value = resultSet.getObject(i);
				value = switch (type) {
					case Types.BLOB: {
						if (value != null) {
							Blob blob = (Blob) value;
							yield blob.getBytes(1, (int) blob.length());
						}
						yield null;
					}
					case Types.DATE: {
						yield value != null ? ((Date) value).toLocalDate() : null;
					}
					case Types.TIME: {
						yield value != null ? ((Time) value).toLocalTime() : null;
					}
					case Types.TIMESTAMP: {
						yield value != null ? ((Timestamp) value).toLocalDateTime() : null;
					}
					default: {
						yield value;
					}
				};
				row.put(label, value);
			}
			result.add(row);
		}
		return new LocalResult(result);
	}

}

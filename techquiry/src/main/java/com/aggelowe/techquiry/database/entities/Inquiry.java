package com.aggelowe.techquiry.database.entities;

import io.micrometer.common.lang.NonNull;
import lombok.*;

/**
 * The {@link Inquiry} class represents an inquiry entry of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Data
@AllArgsConstructor
public class Inquiry {
	
	/**
	 * The unique id of the inquiry
	 */
    @NonNull
	private final int id;
	
	/**
	 * The user id of the author
	 */
    @NonNull
    @ToString.Exclude
	private int userId;
	
	/**
	 * The title of the inquiry
	 */
    @NonNull
	private String title;
	
	/**
	 * The content of the inquiry
	 */
    @NonNull
	private String content;
	
	/**
	 * Whether the author is anonymous
	 */
    @NonNull
	private boolean anonymous;

}

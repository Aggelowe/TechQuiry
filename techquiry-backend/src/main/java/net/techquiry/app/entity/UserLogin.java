package net.techquiry.app.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * The {@link UserLogin} class represents a user login of the TechQuiry
 * application along with the respective information and data.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Getter
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
@ToString
public class UserLogin {

	/**
	 * The unique id of the user
	 */
	@NonNull
	private Integer userId;

	/**
	 * The unique username chosen by the user
	 */
	@NonNull
	private String username;

	/**
	 * The hashed password of the user
	 */
	@NonNull
	@ToString.Exclude
	private byte[] passwordHash;

	/**
	 * The random salt used in hashing the password
	 */
	@NonNull
	@ToString.Exclude
	private byte[] passwordSalt;

}

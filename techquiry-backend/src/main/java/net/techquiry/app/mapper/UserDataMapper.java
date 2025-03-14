package net.techquiry.app.mapper;

import org.springframework.stereotype.Component;

import net.techquiry.app.dto.UserDataDto;
import net.techquiry.app.entity.UserData;
import net.techquiry.app.entity.UserData.UserDataBuilder;
import net.techquiry.app.mapper.exception.MapperException;
import net.techquiry.app.mapper.exception.MissingValueException;

/**
 * The {@link UserDataMapper} class is responsible for mapping between
 * {@link UserData} and {@link UserDataDto} objects.
 * 
 * @author Aggelowe
 * @since 0.0.1
 */
@Component
public class UserDataMapper {

	/**
	 * This method maps the given {@link UserData} to a {@link UserDataDto} object.
	 * 
	 * @param userData The user data entity to map
	 * @return The user data DTO
	 */
	public UserDataDto toDto(UserData userData) {
		Integer userId = userData.getUserId();
		String firstName = userData.getFirstName();
		String lastName = userData.getLastName();
		return new UserDataDto(userId, firstName, lastName);
	}

	/**
	 * This method creates a new {@link UserData} object based on the data of the
	 * given {@link UserDataDto}.
	 * 
	 * @param userDataDto The data transfer object to map
	 * @return The new user data entity
	 * @throws MissingValueException If the first or last name in the DTO are
	 *                               missing
	 */
	public UserData toEntity(UserDataDto userDataDto) throws MapperException {
		String firstName = userDataDto.getFirstName();
		String lastName = userDataDto.getLastName();
		if (firstName == null || lastName == null) {
			throw new MissingValueException("The first and/or last name is missing!");
		}
		return new UserData(0, firstName, lastName);
	}

	/**
	 * This method creates a new {@link UserData} object whose data are a copy of
	 * the original entity and whose data are changed according to the given
	 * {@link UserDataDto}.
	 * 
	 * @param userDataDto The data transfer object to map
	 * @param original    The entity to draw the original data from
	 * @return The new user data entity
	 */
	public UserData updateEntity(UserDataDto userDataDto, UserData original) {
		String firstName = userDataDto.getFirstName();
		String lastName = userDataDto.getLastName();
		UserDataBuilder builder = original.toBuilder();
		if (firstName != null) {
			builder.firstName(firstName);
		}
		if (lastName != null) {
			builder.lastName(lastName);
		}
		return builder.build();
	}

}

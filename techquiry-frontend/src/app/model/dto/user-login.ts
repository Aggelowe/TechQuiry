/**
 * Represents and contains a user's login information.
 */
export interface UserLogin {

	/**
	 * The unique id of the user.
	 */
	userId?: number;

	/**
	 * The unique username of the user.
	 */
	username: string;

	/**
	 * The plaintext password of the user.
	 */
	password?: string;

}

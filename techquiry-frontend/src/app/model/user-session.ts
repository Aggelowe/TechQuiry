import { UserLogin } from "@app/model/dto/user-login";
import { UserData } from "@app/model/dto/user-data";

/**
 * Contains the current session's user data
 */
export interface UserSession {

	/**
	 * The current user's login information
	 */
	userLogin: UserLogin,

	/**
	 * The current user's data
	 */
	userData?: UserData,

	/**
	 * The current user's icon
	 */
	userIcon?: Blob

}

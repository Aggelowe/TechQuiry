import { ErrorType } from "@app/model/error-type";

/**
* Standardizes the structure of error responses.
*/
export interface ErrorResponse {

	/**
	 * The type of error.
	 */
	type: ErrorType,

	/**
	 * The error status code.
	 */
	status?: number,

	/**
	 * The error message.
	 */
	message: string;

}

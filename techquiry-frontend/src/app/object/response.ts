/**
 * Represents and contains a response's information.
 */
export interface Response {

	/**
	 * The unique id of the response.
	 */
	responseId?: number;

	/**
	 * The id of the parent inquiry.
	 */
	inquiryId?: number;

	/**
	 * The user id of the author.
	 */
	userId?: number;

	/**
	 * Whether the author is anonymous.
	 */
	anonymous: boolean;

	/**
	 * The content of the response.
	 */
	content: string;

}

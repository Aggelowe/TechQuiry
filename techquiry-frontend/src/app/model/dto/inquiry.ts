/**
 * Represents and contains an inquiry's information.
 */
export interface Inquiry {

	/**
	 * The unique id of the inquiry.
	 */
	inquiryId?: number;

	/**
	 * The user id of the author.
	 */
	userId?: number;

	/**
	 * The title of the inquiry.
	 */
	title: string;

	/**
	 * The content of the inquiry.
	 */
	content: string;

	/**
	 * Whether the author is anonymous.
	 */
	anonymous: boolean;

}

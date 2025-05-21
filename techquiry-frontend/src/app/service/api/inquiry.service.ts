import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { UserLogin } from '../../object/user-login';
import { Inquiry } from '../../object/inquiry';
import { Response } from '../../object/response';

const baseUrl: string = `${environment.apiUrl}/inquiry`

@Injectable({
	providedIn: 'root'
})
export class InquiryService {

	constructor(private httpClient: HttpClient) { }

	getInquiryCount(): Observable<number> {
		const callUrl = `${baseUrl}/count`;
		return this.httpClient.get<number>(callUrl);
	}

	getInquiryRange(count: number, page: number): Observable<Inquiry[]> {
		const callUrl = `${baseUrl}/range/${count}/${page - 1}`;
		return this.httpClient.get<Inquiry[]>(callUrl);
	}

	createInquiry(inquiry: Inquiry): Observable<number> {
		const callUrl = `${baseUrl}/create`;
		return this.httpClient.post<number>(callUrl, inquiry, { withCredentials: true });
	}

	getInquiry(inquiryId: number): Observable<Inquiry> {
		const callUrl = `${baseUrl}/id/${inquiryId}`;
		return this.httpClient.get<Inquiry>(callUrl);
	}

	deleteInquiry(inquiryId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${inquiryId}/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	updateInquiry(inquiryId: number, inquiry: Inquiry): Observable<void> {
		const callUrl = `${baseUrl}/id/${inquiryId}/update`;
		return this.httpClient.post<void>(callUrl, inquiry, { withCredentials: true });
	}

	getResponses(inquiryId: number): Observable<Response[]> {
		const callUrl = `${baseUrl}/id/${inquiryId}/response`;
		return this.httpClient.get<Response[]>(callUrl);
	}

	getResponseCount(inquiryId: number): Observable<number> {
		const callUrl = `${baseUrl}/id/${inquiryId}/response/count`;
		return this.httpClient.get<number>(callUrl);
	}

	createResponse(inquiryId: number, response: Response): Observable<number> {
		const callUrl = `${baseUrl}/id/${inquiryId}/response/create`;
		return this.httpClient.post<number>(callUrl, response, { withCredentials: true });
	}

	getObservers(inquiryId: number): Observable<UserLogin[]> {
		const callUrl = `${baseUrl}/id/${inquiryId}/observer`;
		return this.httpClient.get<UserLogin[]>(callUrl);
	}

	getObserverCount(inquiryId: number): Observable<number> {
		const callUrl = `${baseUrl}/id/${inquiryId}/observer/count`;
		return this.httpClient.get<number>(callUrl);
	}

	checkObserver(inquiryId: number): Observable<boolean> {
		const callUrl = `${baseUrl}/id/${inquiryId}/observer/check`;
		return this.httpClient.get<boolean>(callUrl, { withCredentials: true });
	}

	createObserver(inquiryId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${inquiryId}/observer/create`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	deleteObserver(inquiryId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${inquiryId}/observer/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

}

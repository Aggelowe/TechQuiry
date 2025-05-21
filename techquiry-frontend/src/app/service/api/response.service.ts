import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserLogin } from '@app/model/dto/user-login';
import { Response } from '@app/model/dto/response';
import { environment } from '@environment';

const baseUrl: string = `${environment.apiUrl}/response`

@Injectable({
	providedIn: 'root'
})
export class ResponseService {

	constructor(private httpClient: HttpClient) { }

	getResponse(responseId: number): Observable<Response> {
		const callUrl = `${baseUrl}/id/${responseId}`;
		return this.httpClient.get<Response>(callUrl);
	}

	deleteResponse(responseId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${responseId}/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	updateResponse(responseId: number, response: Response): Observable<void> {
		const callUrl = `${baseUrl}/id/${responseId}/update`;
		return this.httpClient.post<void>(callUrl, response, { withCredentials: true });
	}

	getUpvotes(responseId: number): Observable<UserLogin[]> {
		const callUrl = `${baseUrl}/id/${responseId}/upvote`;
		return this.httpClient.get<UserLogin[]>(callUrl);
	}

	getUpvoteCount(responseId: number): Observable<number> {
		const callUrl = `${baseUrl}/id/${responseId}/upvote/count`;
		return this.httpClient.get<number>(callUrl);
	}

	checkUpvote(responseId: number): Observable<boolean> {
		const callUrl = `${baseUrl}/id/${responseId}/upvote/check`;
		return this.httpClient.get<boolean>(callUrl, { withCredentials: true });
	}

	createUpvote(responseId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${responseId}/upvote/create`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	deleteUpvote(responseId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${responseId}/upvote/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

}

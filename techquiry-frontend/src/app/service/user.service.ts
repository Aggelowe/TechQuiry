import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { UserLogin } from '../object/user-login';
import { UserData } from '../object/user-data';
import { Inquiry } from '../object/inquiry';

const baseUrl: string = `${environment.apiUrl}/user`

@Injectable({
	providedIn: 'root'
})
export class UserService {

	constructor(private httpClient: HttpClient) { }

	getUserLoginCount(): Observable<number> {
		const callUrl = `${baseUrl}/count`;
		return this.httpClient.get<number>(callUrl);
	}

	getUserLoginRange(count: number, page: number): Observable<UserLogin[]> {
		const callUrl = `${baseUrl}/range/${count}/${page - 1}`;
		return this.httpClient.get<UserLogin[]>(callUrl);
	}

	createUserLogin(userLogin: UserLogin): Observable<number> {
		const callUrl = `${baseUrl}/create`;
		return this.httpClient.post<number>(callUrl, userLogin, { withCredentials: true });
	}

	getCurrentUserLogin(): Observable<UserLogin> {
		const callUrl = `${baseUrl}/current`;
		return this.httpClient.get<UserLogin>(callUrl, { withCredentials: true });
	}

	login(userLogin: UserLogin): Observable<UserLogin> {
		const callUrl = `${baseUrl}/login`;
		return this.httpClient.post<UserLogin>(callUrl, userLogin, { withCredentials: true });
	}

	logout(): Observable<void> {
		const callUrl = `${baseUrl}/logout`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	createUserData(userData: UserData): Observable<void> {
		const callUrl = `${baseUrl}/data/create`;
		return this.httpClient.post<void>(callUrl, userData, { withCredentials: true });
	}

	getUserLoginByUsername(username: String): Observable<UserLogin> {
		const callUrl = `${baseUrl}/u/${username}`;
		return this.httpClient.get<UserLogin>(callUrl);
	}

	getUserLoginByUserId(userId: number): Observable<UserLogin> {
		const callUrl = `${baseUrl}/id/${userId}`;
		return this.httpClient.get<UserLogin>(callUrl);
	}

	deleteUserLogin(userId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${userId}/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	updateUserLogin(userId: number, userLogin: UserLogin): Observable<void> {
		const callUrl = `${baseUrl}/id/${userId}/update`;
		return this.httpClient.post<void>(callUrl, userLogin, { withCredentials: true });
	}

	getInquiries(userId: number): Observable<Inquiry[]> {
		const callUrl = `${baseUrl}/id/${userId}/inquiries`;
		return this.httpClient.get<Inquiry[]>(callUrl, { withCredentials: true });
	}

	getObservedInquiries(userId: number): Observable<Inquiry[]> {
		const callUrl = `${baseUrl}/id/${userId}/observed`;
		return this.httpClient.get<Inquiry[]>(callUrl);
	}

	getUpvotedResponses(userId: number): Observable<Response[]> {
		const callUrl = `${baseUrl}/id/${userId}/upvotes`;
		return this.httpClient.get<Response[]>(callUrl);
	}

	getUserData(userId: number): Observable<UserData> {
		const callUrl = `${baseUrl}/id/${userId}/data`;
		return this.httpClient.get<UserData>(callUrl);
	}

	updateUserData(userId: number, userData: UserData): Observable<void> {
		const callUrl = `${baseUrl}/id/${userId}/data/update`;
		return this.httpClient.post<void>(callUrl, userData, { withCredentials: true });
	}

	deleteUserData(userId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${userId}/data/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

	getUserIcon(userId: number): Observable<Blob> {
		const callUrl = `${baseUrl}/id/${userId}/data/icon`;
		return this.httpClient.get(callUrl, { responseType: 'blob' });
	}

	updateUserIcon(userId: number, file: File): Observable<void> {
		const callUrl = `${baseUrl}/id/${userId}/data/icon/update`;
		const reader = new FileReader();
		reader.readAsArrayBuffer(file);
		return new Observable<void>(observer => {
			reader.onload = () => {
				const blob = new Blob([reader.result as ArrayBuffer], { type: file.type });
				this.httpClient.post<void>(callUrl, blob, { withCredentials: true }).subscribe(() => observer.next());
			};
		});
	}

	deleteUserIcon(userId: number): Observable<void> {
		const callUrl = `${baseUrl}/id/${userId}/data/icon/delete`;
		return this.httpClient.post<void>(callUrl, '', { withCredentials: true });
	}

}

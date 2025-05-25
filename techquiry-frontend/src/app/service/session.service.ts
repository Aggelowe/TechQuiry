import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subscriber } from 'rxjs';
import { UserService } from '@app/service/api/user.service';
import { UserLogin } from '@app/model/dto/user-login';
import { UserData } from '@app/model/dto/user-data';
import { UserSession } from '@app/model/user-session';
import { ErrorResponse } from '@app/model/error-response';
import { ErrorType } from '@app/model/error-type';

@Injectable({
	providedIn: 'root'
})
export class SessionService {

	private sessionSubject: BehaviorSubject<UserSession | undefined> = new BehaviorSubject<UserSession | undefined>(undefined);

	constructor(private userService: UserService) { }

	updateUserSession(): Observable<UserSession | undefined> {
		return new Observable<UserSession | undefined>((subscriber: Subscriber<UserSession | undefined>) => {
			let emit: ((session: UserSession | undefined) => void) = (session: UserSession | undefined) => {
				subscriber.next(session);
				subscriber.complete();
				this.sessionSubject.next(session);
			};
			this.userService.getCurrentUserLogin().subscribe({
				next: (userLogin: UserLogin) => {
					this.userService.getUserData(userLogin.userId!).subscribe({
						next: (userData: UserData) => {
							this.userService.getUserIcon(userLogin.userId!).subscribe({
								next: (userIcon: Blob) => {
									emit({ userLogin, userData, userIcon });
								},
								error: (error: ErrorResponse) => {
									emit({ userLogin, userData });
									if (error.type != ErrorType.Server || error.status != 404) {
										throw error;
									}
								},
							});
						},
						error: (error: ErrorResponse) => {
							emit({ userLogin });
							if (error.type != ErrorType.Server || error.status != 404) {
								throw error;
							}
						}
					});
				},
				error: (error: ErrorResponse) => {
					if (error.type == ErrorType.Server && error.status == 401) {
						emit(undefined);
					} else {
						throw error;
					}
				}
			});
		});
	}

	getSessionObservable(): Observable<UserSession | undefined> {
		return this.sessionSubject.asObservable()
	}

	getCurrentSession(): UserSession | undefined {
		return this.sessionSubject.value;
	}

}

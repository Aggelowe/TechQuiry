import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
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

	readonly sessionObservable: Observable<UserSession | undefined> = this.sessionSubject.asObservable();

	constructor(private userService: UserService) { }

	refreshUserSession(): void {
		this.userService.getCurrentUserLogin().subscribe({
			next: (userLogin: UserLogin) => {
				this.userService.getUserData(userLogin.userId!).subscribe({
					next: (userData: UserData) => {
						this.userService.getUserIcon(userLogin.userId!).subscribe({
							next: (userIcon: Blob) => {
								this.sessionSubject.next({ userLogin, userData, userIcon });
							},
							error: (error: ErrorResponse) => {
								this.sessionSubject.next({ userLogin, userData });
								if (error.type == ErrorType.Server && error.status == 404) {
									return of(undefined);
								} else {
									return throwError(() => error);
								}
							},
						});
					},
					error: (error: ErrorResponse) => {
						this.sessionSubject.next({ userLogin });
						if (error.type == ErrorType.Server && error.status == 404) {
							return of(undefined);
						} else {
							return throwError(() => error);
						}
					}
				});
			},
			error: (error: ErrorResponse) => {
				if (error.type == ErrorType.Server && error.status == 401) {
					this.sessionSubject.next(undefined);
					return of(undefined);
				} else {
					return throwError(() => error);
				}
			}
		});
	}

}

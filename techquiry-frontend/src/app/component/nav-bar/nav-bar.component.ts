import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { finalize } from 'rxjs';
import { UserLogin } from '@app/model/dto/user-login';
import { UserSession } from '@app/model/user-session';
import { UserService } from '@app/service/api/user.service';
import { SessionService } from '@app/service/session.service';

@Component({
	selector: 'nav-bar',
	imports: [
		RouterModule
	],
	templateUrl: './nav-bar.component.html',
	styleUrl: './nav-bar.component.scss',
})
export class NavBarComponent {

	userLogin?: UserLogin;

	userIconUrl?: string;

	constructor(
		private sessionService: SessionService,
		private userService: UserService
	) {
		this.sessionService.getSessionObservable().subscribe((userSession: UserSession | undefined) => {
			if (userSession) {
				this.userLogin = userSession.userLogin;
				this.userIconUrl = userSession.userIcon ? URL.createObjectURL(userSession.userIcon) : undefined;
			} else {
				this.userLogin = undefined;
				this.userIconUrl = undefined;
			}
		});
	}

	logout() {
		this.userService.logout().pipe(
			finalize(() => {
				this.sessionService.updateUserSession().subscribe();
			})
		).subscribe();
	}

}

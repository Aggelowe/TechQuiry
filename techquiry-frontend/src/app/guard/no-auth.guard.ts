import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { UserSession } from '@app/model/user-session';
import { SessionService } from '@app/service/session.service';

export const noAuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const sessionService: SessionService = inject(SessionService);
	const router: Router = inject(Router);
	let currentSession: UserSession | undefined = sessionService.getCurrentSession();
	if (currentSession) {
		router.navigate(['/']);
		return false;
	}
	return true;
};

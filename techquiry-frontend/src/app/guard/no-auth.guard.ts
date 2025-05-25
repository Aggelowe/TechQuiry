import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot } from '@angular/router';
import { UserSession } from '@app/model/user-session';
import { SessionService } from '@app/service/session.service';
import { Observable, Subscriber } from 'rxjs';

export const noAuthGuard: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
	const sessionService: SessionService = inject(SessionService);
	const router: Router = inject(Router);
	return new Observable<boolean>((subscriber: Subscriber<boolean>) => {
		sessionService.getCurrentSession().subscribe((userSession: UserSession | undefined) => {
			if (userSession) {
				router.navigate(['/']);
				subscriber.next(false);
			} else {
				subscriber.next(true);
			}
			subscriber.complete();
		});
	});
};

import { Routes } from '@angular/router';
import { LoginComponent } from '@app/component/login/login.component';
import { noAuthGuard } from '@app/guard/no-auth.guard';

export const routes: Routes = [
	{
		path: 'login',
		component: LoginComponent,
		canActivate: [
			noAuthGuard
		]
	}
];

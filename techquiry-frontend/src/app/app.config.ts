import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { errorInterceptor } from './error/error.interceptor';
import { UserService } from './service/user.service';

export const appConfig: ApplicationConfig = {
	providers: [
		provideZoneChangeDetection({
			eventCoalescing: true
		}),
		provideRouter(routes),
		provideHttpClient(withInterceptors([
			errorInterceptor
		])),
		UserService
	]
};

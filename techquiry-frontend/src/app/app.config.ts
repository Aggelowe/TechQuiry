import { ApplicationConfig, ErrorHandler, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { errorInterceptor } from './error/error.interceptor';
import { ErrorService } from './service/error.service';

export const appConfig: ApplicationConfig = {
	providers: [
		provideZoneChangeDetection({
			eventCoalescing: true
		}),
		provideRouter(routes),
		provideHttpClient(withInterceptors([
			errorInterceptor
		])),
		{ provide: ErrorHandler, useExisting: ErrorService }
	]
};

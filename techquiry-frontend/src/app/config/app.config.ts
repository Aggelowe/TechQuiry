import { ApplicationConfig, ErrorHandler, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { errorInterceptor } from '@app/interceptor/error.interceptor';
import { ErrorService } from '@app/service/error.service';
import { routes } from '@app/config/app.routes';

export const appConfig: ApplicationConfig = {
	providers: [
		provideZoneChangeDetection({
			eventCoalescing: true
		}),
		provideRouter(routes),
		provideHttpClient(withInterceptors([
			errorInterceptor
		])),
		{
			provide: ErrorHandler,
			useExisting: ErrorService
		}
	]
};

import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ErrorResponse } from './error-response';
import { ErrorType } from './error-type';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
	return next(request).pipe(
		catchError((err: HttpErrorResponse) => {
			if (err.error instanceof ErrorEvent) {
				return throwError(() => ({
					type: ErrorType.Connection,
					message: (err.error as ErrorEvent).message ?? 'Connection error occured!'
				} as ErrorResponse));
			}
			return throwError(() => ({
				type: ErrorType.Server,
				status: err.status,
				message: err.error?.message || 'Server error occured!'
			} as ErrorResponse));
		})
	);
};

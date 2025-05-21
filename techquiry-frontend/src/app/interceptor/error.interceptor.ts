import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { ErrorResponse } from '@app/model/error-response';
import { ErrorType } from '@app/model/error-type';

export const errorInterceptor: HttpInterceptorFn = (request, next) => {
	return next(request).pipe(
		catchError((err: HttpErrorResponse) => {
			if (err.error instanceof ErrorEvent || err.status === 0) {
				return throwError(() => ({
					type: ErrorType.Connection,
					message: (err.error as ErrorEvent).message ?? 'A connection error occured!'
				} as ErrorResponse));
			}
			return throwError(() => ({
				type: ErrorType.Server,
				status: err.status,
				message: err.error?.message || 'A server error occured!'
			} as ErrorResponse));
		})
	);
};

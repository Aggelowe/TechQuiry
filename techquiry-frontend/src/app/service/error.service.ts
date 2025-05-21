import { ErrorHandler, Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { ErrorResponse } from '@app/error/error-response';
import { ErrorType } from '@app/error/error-type';

@Injectable({
	providedIn: 'root'
})
export class ErrorService extends ErrorHandler {

	readonly errorSubject: Subject<ErrorResponse> = new Subject();

	override handleError(error: any): void {
		super.handleError(error);
		if ('type' in error && 'message' in error && Object.values(ErrorType).includes(error.type)) {
			this.errorSubject.next(error as ErrorResponse);
		}
	}

}

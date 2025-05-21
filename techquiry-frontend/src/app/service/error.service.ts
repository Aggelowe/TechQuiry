import { ErrorHandler, Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { ErrorResponse } from '@app/model/error-response';
import { ErrorType } from '@app/model/error-type';

@Injectable({
	providedIn: 'root'
})
export class ErrorService extends ErrorHandler {

	private errorSubject: Subject<ErrorResponse> = new Subject();

	readonly errorObservable: Observable<ErrorResponse> = this.errorSubject.asObservable();

	override handleError(error: any): void {
		super.handleError(error);
		if ('type' in error && 'message' in error && Object.values(ErrorType).includes(error.type)) {
			this.errorSubject.next(error as ErrorResponse);
		}
	}

}

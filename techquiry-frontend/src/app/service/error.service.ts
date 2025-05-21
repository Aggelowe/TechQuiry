import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { ErrorResponse } from '@app/error/error-response';

@Injectable({
	providedIn: 'root'
})
export class ErrorService {

	readonly errorSubject: Subject<ErrorResponse> = new Subject();

}

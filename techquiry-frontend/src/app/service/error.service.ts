import { Injectable } from '@angular/core';
import { ErrorResponse } from '../error/error-response';
import { Subject } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class ErrorService {

	readonly errorSubject: Subject<ErrorResponse> = new Subject();

}

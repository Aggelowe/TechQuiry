import { Component, OnInit, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NgbAlert, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { debounceTime, tap } from 'rxjs';
import { ErrorService } from '../../service/error.service';
import { ErrorType } from '../../error/error-type';
import { environment } from '../../../environments/environment';
import { ErrorResponse } from '../../error/error-response';

@Component({
	selector: 'error-alert',
	imports: [
		NgbAlertModule
	],
	templateUrl: './error-alert.component.html'
})
export class ErrorAlertComponent {

	@ViewChild('alert', { static: false }) private alert!: NgbAlert;

	title?: string;
	message?: string;

	constructor(private errorService: ErrorService) {
		this.errorService.errorSubject.pipe(
			takeUntilDestroyed(),
			tap((error: ErrorResponse) => {
				this.title = error.type === ErrorType.Server ? `Error ${error.status}! ` : undefined;
				this.message = error.message;
			}),
			debounceTime(environment.alertTime)
		).subscribe((error: ErrorResponse) => {
			this.alert.close()
		});
	}

}

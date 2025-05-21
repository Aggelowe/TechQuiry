import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { debounceTime, tap } from 'rxjs';
import { NgbAlert, NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { ErrorService } from '@app/service/error.service';
import { ErrorType } from '@app/error/error-type';
import { ErrorResponse } from '@app/error/error-response';
import { environment } from '@environment';

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

	constructor(private errorService: ErrorService,
		private changeDetectorRef: ChangeDetectorRef
	) {
		this.errorService.errorSubject.pipe(
			takeUntilDestroyed(),
			tap((error: ErrorResponse) => {
				this.title = error.type === ErrorType.Server ? `Error ${error.status}! ` : undefined;
				this.message = error.message;
				this.changeDetectorRef.detectChanges();
			}),
			debounceTime(environment.alertTime)
		).subscribe((error: ErrorResponse) => {
			this.alert.close()
		});
	}

}

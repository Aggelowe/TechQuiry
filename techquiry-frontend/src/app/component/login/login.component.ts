import { Component } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UserLogin } from '@app/model/dto/user-login';
import { ErrorResponse } from '@app/model/error-response';
import { UserService } from '@app/service/api/user.service';
import { SessionService } from '@app/service/session.service';

@Component({
	selector: 'app-login',
	imports: [
		ReactiveFormsModule
	],
	templateUrl: './login.component.html',
	styleUrl: './login.component.scss'
})
export class LoginComponent {

	disabled: boolean = false;

	loginForm: FormGroup = new FormGroup({
		username: new FormControl('', Validators.required),
		password: new FormControl('', Validators.required)
	});

	constructor(
		private userService: UserService,
		private sessionService: SessionService,
		private router: Router
	) {

	}

	login(): void {
		if (!this.loginForm.valid || this.disabled) {
			return;
		}
		this.disabled = true;
		this.userService.login(this.loginForm.value).subscribe({
			next: (userLogin: UserLogin) => {
				this.sessionService.updateUserSession().subscribe(() => {
					this.router.navigate(['/']);
					this.disabled = false;
				});
			},
			error: (error: ErrorResponse) => {
				this.disabled = false;
				throw error;
			}
		});
	}

	isInvalid(form: string): boolean {
		let control: AbstractControl<any, any> | undefined = this.loginForm.controls[form];
		if (!control) {
			return true;
		}
		return control.invalid && (control.dirty || control.touched);
	}

}
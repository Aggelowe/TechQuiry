import { Component, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBarComponent } from "@app/component/nav-bar/nav-bar.component";
import { ErrorAlertComponent } from "@app/component/error-alert/error-alert.component";
import { SessionService } from '@app/service/session.service';

@Component({
	selector: 'content',
	imports: [
		RouterOutlet,
		NavBarComponent,
		ErrorAlertComponent
	],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {

	constructor(private sessionService: SessionService) { }

	ngOnInit() {
		this.sessionService.updateUserSession().subscribe();
	}

}

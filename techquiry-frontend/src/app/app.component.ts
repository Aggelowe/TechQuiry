import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBarComponent } from "./component/nav-bar/nav-bar.component";
import { ErrorAlertComponent } from "./component/error-alert/error-alert.component";

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
export class AppComponent {



}

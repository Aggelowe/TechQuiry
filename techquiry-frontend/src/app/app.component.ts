import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavBarComponent } from "./component/nav-bar/nav-bar.component";

@Component({
	selector: 'content',
	imports: [
		RouterOutlet,
		NavBarComponent
	],
	templateUrl: './app.component.html',
	styleUrl: './app.component.scss',
})
export class AppComponent {



}

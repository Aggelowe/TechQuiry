import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../../service/user.service';

@Component({
	selector: 'nav-bar',
	imports: [
		RouterModule
	],
	templateUrl: './nav-bar.component.html',
	styleUrl: './nav-bar.component.scss',
})
export class NavBarComponent {

	constructor() {	}

}

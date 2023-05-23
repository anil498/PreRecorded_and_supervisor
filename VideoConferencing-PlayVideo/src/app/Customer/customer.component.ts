import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
	selector: 'customer-dashboard',
	templateUrl: './customer.component.html',
	styleUrls: ['./customer.component.scss']
})

export class CustomerComponent implements OnInit {
	title = 'openvidu-angular';

	constructor(private router: Router) {}

	ngOnInit(): void {}

	goTo(path: string) {
		this.router.navigate([`/${path}`]);
	}
}

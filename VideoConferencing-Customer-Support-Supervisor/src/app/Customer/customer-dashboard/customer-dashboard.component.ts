import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
@Component({
	selector: 'customer-dashboard',
	templateUrl: './customer-dashboard.component.html',
	styleUrls: ['./customer-dashboard.component.css']
})
export class CustomerDashboardComponent implements OnInit {
	constructor(private router: Router) {}

	ngOnInit(): void {}

	goTo(path: string) {
		this.router.navigate([`/${path}`]);
	}
}

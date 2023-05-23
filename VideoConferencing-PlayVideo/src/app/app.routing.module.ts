import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CallComponent } from './openvidu-call/call.component';
import { TestingComponent } from './testing-app/testing.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { CustomerComponent} from './Customer/customer.component';
import { SupportComponent} from './Support/support.component';


const routes: Routes = [
	{ path: '', component: DashboardComponent },
	{ path: 'call', component: CallComponent },
	{ path: 'testing', component: TestingComponent },
	{ path: 'admin', component: AdminDashboardComponent },
	{ path: 'customer', component: CustomerComponent },
	{ path: 'support', component: SupportComponent },
	{ path: 'call/:id', component: CallComponent },
	
];
@NgModule({
	imports: [RouterModule.forRoot(routes, { useHash: true })],
	exports: [RouterModule]
})
export class AppRoutingModule {}

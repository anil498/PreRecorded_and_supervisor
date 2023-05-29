import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CallComponent } from './call-confirmation/call.component';
import { TestingComponent } from './testing-app/testing.component';
import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { CallCustomerComponent } from './customer/call-customer/call-customer.component';
import { CallSupportComponent } from './Support/call-support/call-support.component';
import { CustomerDashboardComponent } from './customer/customer-dashboard/customer-dashboard.component';
import { SupportDashboardComponent } from './Support/support-dashboard/support-dashboard.component';
import { SuperDashboardComponent } from './Supervisor/super-dashboard/super-dashboard.component'
import { CallSuperComponent } from './Supervisor/call-super/call-super.component'
import { CallSuperConfirmationComponent } from './callsuper-confirmation/callsuper-confirmation.component';

const routes: Routes = [
	{ path: '', component: DashboardComponent },
	{ path: 'customer', component: CustomerDashboardComponent },
	{ path: 'call-customer', component: CallCustomerComponent },
	{ path: 'testing', component: TestingComponent },
	{ path: 'call-support/:id', component: CallSupportComponent },
	{ path: 'support', component: SupportDashboardComponent },
	{ path: 'admin', component: AdminDashboardComponent },
	{ path: 'super', component: SuperDashboardComponent},
	{ path: 'call-super/:id', component: CallSuperComponent},
	{ path: 'call', component: CallComponent},
	{ path: 'confirm', component: CallSuperConfirmationComponent }
];
@NgModule({
	imports: [RouterModule.forRoot(routes, { useHash: true })],
	exports: [RouterModule]
})
export class AppRoutingModule {}

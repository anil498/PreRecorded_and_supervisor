import { NgModule } from '@angular/core';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { BrowserModule } from '@angular/platform-browser';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app.routing.module';

import { environment } from 'src/environments/environment';

import { AdminDashboardComponent } from './admin/admin-dashboard/admin-dashboard.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { CallComponent } from './call-confirmation/call.component';
import { TestingComponent } from './testing-app/testing.component';
// openvidu-angular
import { OpenViduAngularModule } from 'openvidu-angular';
import { CallCustomerComponent, PopupComponent } from './customer/call-customer/call-customer.component';
import { CallSupportComponent} from './Support/call-support/call-support.component';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import { WebSocketService } from './services/websocket.service';
import { ConfirmationDialogService } from './confirmation-dialog/confirmation-dialog.service';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { FormsModule,ReactiveFormsModule } from '@angular/forms';
import { CustomerDashboardComponent } from './customer/customer-dashboard/customer-dashboard.component';
import { SupportDashboardComponent } from './Support/support-dashboard/support-dashboard.component';
import { SuperDashboardComponent } from './Supervisor/super-dashboard/super-dashboard.component';
import { CallSuperComponent } from './Supervisor/call-super/call-super.component';
import { CallSuperConfirmationComponent } from './callsuper-confirmation/callsuper-confirmation.component';

import { SharedService} from './services/shared.service'



@NgModule({
	declarations: [
		AppComponent,
		DashboardComponent,
		AdminDashboardComponent,
		CallComponent,
		TestingComponent,
		CallCustomerComponent,
		CallSupportComponent,
		SupportDashboardComponent,
		CustomerDashboardComponent,
		ConfirmationDialogComponent,
		SuperDashboardComponent,
		CallSuperComponent,
		PopupComponent,
		CallSuperConfirmationComponent
	],
	imports: [
		MatSortModule,
		MatTableModule,
		MatFormFieldModule,
		MatInputModule,
		MatPaginatorModule,
		BrowserModule,
		MatCheckboxModule,
		MatButtonModule,
		MatIconModule,
		MatMenuModule,
		BrowserAnimationsModule,
		NgbModule,
		FormsModule,
		ReactiveFormsModule,
		OpenViduAngularModule,
		OpenViduAngularModule.forRoot(environment),
		AppRoutingModule // Order is important, AppRoutingModule must be the last import for useHash working
	],
	providers: [WebSocketService, ConfirmationDialogService , SharedService],
	entryComponents: [ConfirmationDialogComponent , PopupComponent],
	bootstrap: [AppComponent]
})
export class AppModule {}

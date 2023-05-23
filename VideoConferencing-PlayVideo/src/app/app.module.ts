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
import { CallComponent , DialogComponent,  VideoFileDialogComponent , FileDialogComponent } from './openvidu-call/call.component';
import { TestingComponent } from './testing-app/testing.component';
// openvidu-angular
import {OpenViduAngularModule } from 'openvidu-angular';
import { SupportComponent } from './Support/support.component'
import { WebSocketService } from './services/websocket.service';
import { ConfirmationDialogService } from './confirmation-dialog/confirmation-dialog.service';
import { ConfirmationDialogComponent } from './confirmation-dialog/confirmation-dialog.component';
import {  MatDialogContent, MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { MatListModule } from '@angular/material/list';
import { MatSliderModule } from '@angular/material/slider';

@NgModule({
	declarations: [AppComponent, DashboardComponent, AdminDashboardComponent, CallComponent, TestingComponent , SupportComponent,  VideoFileDialogComponent , DialogComponent , FileDialogComponent ],
	imports: [
		BrowserModule,
		FormsModule,
		MatCheckboxModule,
		MatButtonModule,
		MatIconModule,
		MatMenuModule,
		MatDialogModule,
		MatListModule,
		MatSliderModule,
		BrowserAnimationsModule,
		OpenViduAngularModule.forRoot(environment),
		AppRoutingModule, // Order is important, AppRoutingModule must be the last import for useHash working
	],
	providers: [WebSocketService,ConfirmationDialogService],
	entryComponents: [ConfirmationDialogComponent , CallComponent],
	bootstrap: [AppComponent],
	exports:[CallComponent,VideoFileDialogComponent ,  DialogComponent , FileDialogComponent],
	
})
export class AppModule {}


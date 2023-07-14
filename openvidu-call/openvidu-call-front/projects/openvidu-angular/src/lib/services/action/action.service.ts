import { Injectable } from '@angular/core';
import { MatDialog, MatDialogConfig, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Subscription } from 'rxjs';
import { DeleteDialogComponent } from '../../components/dialogs/delete-recording.component';
import { DialogTemplateComponent } from '../../components/dialogs/dialog.component';
import {ScreenDialogTemplateComponent} from '../../components/dialogs/screen-dialog.component';
import { ProFeatureDialogTemplateComponent } from '../../components/dialogs/pro-feature-dialog.component';
import { RecordingDialogComponent } from '../../components/dialogs/recording-dialog.component';
import { INotificationOptions } from '../../models/notification-options.model';
import { OpenViduAngularConfigService } from '../config/openvidu-angular.config.service';

/**
 * @internal
 */
@Injectable({
	providedIn: 'root'
})
export class ActionService {
	private dialogRef:
		| MatDialogRef<DialogTemplateComponent | RecordingDialogComponent | DeleteDialogComponent | ProFeatureDialogTemplateComponent |ScreenDialogTemplateComponent>
		|undefined;
	private dialogSubscription: Subscription;
	constructor(private snackBar: MatSnackBar, public dialog: MatDialog,private libService:OpenViduAngularConfigService) {}

	launchNotification(options: INotificationOptions, callback): void {
		if (!options.config) {
			options.config = {
				duration: 3000,
				verticalPosition: 'top',
				horizontalPosition: 'end'
			};
		}

		const notification = this.snackBar.open(options.message, options.buttonActionText, options.config);
		if (callback) {
			notification.onAction().subscribe(() => {
				callback();
			});
		}
	}

	openDialog(titleMessage: string, descriptionMessage: string, allowClose = true) {
		try {
			this.closeDialog();
		} catch (error) {
		} finally {
			const config: MatDialogConfig = {
				minWidth: '250px',
				data: { title: titleMessage, description: descriptionMessage, showActionButtons: allowClose },
				disableClose: !allowClose
			};
			this.dialogRef = this.dialog.open(DialogTemplateComponent, config);
			
			this.dialogSubscription = this.dialogRef.afterClosed().subscribe((result) => {
				this.dialogRef = undefined;
			});
		}
	}
	openUrlDialog(titleMessage: string, descriptionMessage: string, allowClose = true,redirectUrl:string) {
		try {
			this.closeDialog();
		} catch (error) {
		} finally {
			const config: MatDialogConfig = {
				minWidth: '250px',
				data: { title: titleMessage, description: descriptionMessage, showActionButtons: allowClose },
				disableClose: !allowClose
			};
			console.log("Result1",config)
			this.dialogRef = this.dialog.open(DialogTemplateComponent, config);
			console.log("Result",this.dialogRef)
			
			this.dialogSubscription = this.dialogRef.afterClosed().subscribe((result) => {
				// this.dialogRef = undefined;
				if(result=='close'){
					window.location.replace(redirectUrl);
				}
			});
		}
	}
	async openScreenDialog(titleMessage: string, descriptionMessage: string, allowClose = true): Promise<boolean> {
		try {
		  await this.closeDialog();
		} catch (error) {
		  // Handle error if necessary
		} finally {
		  const config: MatDialogConfig = {
			minWidth: '250px',
			data: { title: titleMessage, description: descriptionMessage, showActionButtons: allowClose },
			disableClose: !allowClose
		  };
		  this.dialogRef = this.dialog.open(ScreenDialogTemplateComponent, config);
	  
		  return new Promise<boolean>((resolve) => {
			this.dialogSubscription = this.dialogRef.afterClosed().subscribe((result) => {
			  if (result !== null) {
				console.log('Selected audio option:', result);
				if (result) {
					resolve(true);
				}else{
					resolve(false);
				}
			  }
			});
		  });
		}
	  }
	  

	openProFeatureDialog(titleMessage: string, descriptionMessage: string, allowClose = true) {
		try {
			this.closeDialog();
		} catch (error) {
		} finally {
			const config: MatDialogConfig = {
				minWidth: '250px',
				data: { title: titleMessage, description: descriptionMessage, showActionButtons: allowClose },
				disableClose: !allowClose
			};
			this.dialogRef = this.dialog.open(ProFeatureDialogTemplateComponent, config);
			this.dialogSubscription = this.dialogRef.afterClosed().subscribe((result) => {
				this.dialogRef = undefined;
			});
		}
	}

	openDeleteRecordingDialog(succsessCallback) {
		try {
			this.closeDialog();
		} catch (error) {
		} finally {
			this.dialogRef = this.dialog.open(DeleteDialogComponent);

			this.dialogSubscription = this.dialogRef.afterClosed().subscribe((result) => {
				if (result) {
					succsessCallback();
				}
			});
		}
	}

	openRecordingPlayerDialog(src: string, allowClose = true) {
		try {
			this.closeDialog();
		} catch (error) {
		} finally {
			const config: MatDialogConfig = {
				minWidth: '250px',
				data: { src, showActionButtons: allowClose },
				disableClose: !allowClose
			};
			this.dialogRef = this.dialog.open(RecordingDialogComponent, config);
		}
	}

	closeDialog() {
		this.dialogRef?.close();
		if (this.dialogSubscription) this.dialogSubscription.unsubscribe();
	}
}

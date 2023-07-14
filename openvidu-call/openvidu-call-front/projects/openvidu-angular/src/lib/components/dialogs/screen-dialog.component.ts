import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogData } from '../../models/dialog.model';

/**
 * @internal
 */

@Component({
    selector: 'ov-dialog-template',
    template: `
      <h1 mat-dialog-title class="dialog-title">{{ data.title }}</h1>
      <div class="dialog-content-wrapper">
        <div mat-dialog-content class="dialog-content">
          <mat-radio-group [(ngModel)]="audioOption" class="radio-group">
            <mat-radio-button [value]="true" [checked]="true" class="radio-button">Share Without Audio</mat-radio-button>
            <mat-radio-button [value]="false" class="radio-button">Share With Audio</mat-radio-button>
          </mat-radio-group>
        </div>
      </div>
      <div mat-dialog-actions *ngIf="data.showActionButtons" class="dialog-actions">
        <button mat-button class="submit-button" (click)="submit()">{{ 'PANEL.SUBMIT' | translate }}</button>
        <button mat-button class="close-button" (click)="close()">{{ 'PANEL.CLOSE' | translate }}</button>
      </div>
    `,
    styles: [`
      .dialog-title {
        font-size: 24px;
        color: #333;
        margin-bottom: 16px;
      }
  
      .dialog-content-wrapper {
        overflow: hidden;
      }
  
      .dialog-content {
        font-size: 16px;
        color: #666;
        margin-bottom: 24px;
        overflow-y: scroll;
        scrollbar-width: thin;
        scrollbar-color: transparent transparent;
      }
  
      .dialog-content::-webkit-scrollbar {
        width: 6px;
      }
  
      .dialog-content::-webkit-scrollbar-track {
        background-color: transparent;
      }
  
      .dialog-content::-webkit-scrollbar-thumb {
        background-color: transparent;
      }
  
      .radio-group {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
      }
  
      .radio-button {
        margin-bottom: 8px;
      }
  
      .dialog-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 5px;
      }
  
      .submit-button {
        background-color: green;
        color: white;
        padding: 8px 16px;
        border-radius: 4px;
        margin-left: 8px;
        font-size: 14px;
      }
  
      .close-button {
        background-color: red;
        color: white;
        padding: 8px 16px;
        border-radius: 4px;
        font-size: 14px;
      }
    `]
  })  
export class ScreenDialogTemplateComponent {
  audioOption: boolean;

  constructor(
    public dialogRef: MatDialogRef<ScreenDialogTemplateComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    this.audioOption=true;
  }

  submit() {
    console.log('Submit button clicked');
    this.dialogRef.close(this.audioOption);
  }

  close() {
    console.log('Close button clicked');
    this.dialogRef.close(null);
  }
}

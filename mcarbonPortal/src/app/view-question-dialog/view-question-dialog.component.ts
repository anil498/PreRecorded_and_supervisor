import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { RestService } from 'app/services/rest.service';

@Component({
  selector: 'app-view-question-dialog',
  templateUrl: './view-question-dialog.component.html',
  styleUrls: ['./view-question-dialog.component.scss']
})
export class ViewQuestionDialogComponent implements OnInit {

  accessData: any;
  topLevelAccess: any[] = [];
  topLevelAccess1: any[] = [];
  topLevelAccess2: any[] = [];
  accessName: any[] = [];

  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<ViewQuestionDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public icdc: any
  ) {
  }

  ngOnInit(): void {
    console.log(this.icdc)
    // if (this.accessData) {
    //   for (let i = 0; i < this.accessData.length; i++) {
    //     var flag = true;
    //     for (let j = 0; j < this.accessId.length; j++) {
    //       if (this.accessId[j] === this.accessData[i].accessId) {
    //         flag = false;
    //         break;
    //       }
    //     }
    //     if (flag === true) this.accessData[i].status = 0;
    //   }
    // }
    // this.accessData = this.accessData.filter((item) => item.status === 1);
    // console.log(this.accessData);
    // this.topLevelAccess = this.accessData.filter((item) => item.pId === 0);
    // const accessHalf = Math.ceil(this.topLevelAccess.length / 2);
    // this.topLevelAccess1 = this.topLevelAccess.slice(0, accessHalf);
    // this.topLevelAccess2 = this.topLevelAccess.slice(accessHalf);
  }

  ngOnDestroy() {

  }


}

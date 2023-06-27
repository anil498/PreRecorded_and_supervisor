import { HttpClient } from "@angular/common/http";
import {
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnInit,
} from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { MatSnackBar } from "@angular/material/snack-bar";
import { DomSanitizer } from "@angular/platform-browser";
import { Router } from "@angular/router";
import { RestService } from "app/services/rest.service";
@Component({
  selector: 'app-update-access-dialog',
  templateUrl: './update-access-dialog.component.html',
  styleUrls: ['./update-access-dialog.component.scss']
})
export class UpdateAccessDialogComponent implements OnInit {

  accessForm: FormGroup;
  parentAccess: any;
  accesses: any[];
  access: any;
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<UpdateAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public accessData: any
  ) {
    this.accesses = accessData.accesses;
    this.access = accessData.access;
    this.parentAccess = this.accesses.filter((item) => item.pId === 0);
    this.parentAccess.sort((a, b) => a.accessId - b.accessId);
  }

  async ngOnInit(): Promise<void> {
    this.accessForm = this.fb.group({
      parentId: [this.access.pId, [Validators.required]],
      name: [this.access.name, [Validators.required]],
      accessId: [this.access.accessId, [Validators.required]],
      seq: [this.access.seq, [Validators.required]],
      systemName: [this.access.systemName, [Validators.required]],
    });
    console.log(this.access);
  }

  submit() {
    if (this.accessForm.invalid) {
      return;
    }

    try {
      const response = this.restService.updateAccess(
        "Access/Update",
        this.accessForm.value.parentId,
        this.accessForm.value.accessId,
        this.accessForm.value.seq,
        this.accessForm.value.name,
        this.accessForm.value.systemName
      );
      console.log(response);
    } catch {
      console.log("access not created");
    }
  }

}

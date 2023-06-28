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
  selector: "app-create-access-dialog",
  templateUrl: "./create-access-dialog.component.html",
  styleUrls: ["./create-access-dialog.component.scss"],
})
export class CreateAccessDialogComponent implements OnInit {
  accessForm: FormGroup;
  parentAccess: any;
  constructor(
    private router: Router,
    private dialogRef: MatDialogRef<CreateAccessDialogComponent>,
    private restService: RestService,
    private fb: FormBuilder,
    private sanitizer: DomSanitizer,
    private snackBar: MatSnackBar,
    private elementRef: ElementRef,
    private http: HttpClient,
    private changeDetectorRef: ChangeDetectorRef,
    @Inject(MAT_DIALOG_DATA) public accesses: any
  ) {
    this.parentAccess = this.accesses.filter((item) => item.pId === 0);
    this.parentAccess.sort((a, b) => a.accessId - b.accessId);
  }

  async ngOnInit(): Promise<void> {
    this.accessForm = this.fb.group({
      parentId: [null, [Validators.required]],
      name: ["", [Validators.required]],
      accessId: [null, [Validators.required]],
      seq: [null, [Validators.required]],
      systemName: ["", [Validators.required]],
    });
    console.log(this.accesses);
  }

  submit() {
    if (this.accessForm.invalid) {
      return;
    }

    try {
      const response = this.restService.createAccess(
        "Access/Create",
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

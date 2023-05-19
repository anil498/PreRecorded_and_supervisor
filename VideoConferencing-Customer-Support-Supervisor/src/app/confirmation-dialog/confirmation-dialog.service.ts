import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ConfirmationDialogComponent } from './confirmation-dialog.component';


@Injectable()
export class ConfirmationDialogService {
  open:boolean;
  modalInstance:any;

  constructor(private modalService: NgbModal) { }

  public confirm(
    title: string='Customer Call Join Request',
    message: string='Do You Want to Join?',
    btnOkText: string = 'Join',
    btnCancelText: string = 'Cancel',
    dialogSize: 'sm'|'lg' = 'sm'): Promise<boolean> {
    var modalRef = this.modalService.open(ConfirmationDialogComponent, { size: dialogSize });
    modalRef.componentInstance.title = title;
    modalRef.componentInstance.message = message;
    modalRef.componentInstance.btnOkText = btnOkText;
    modalRef.componentInstance.btnCancelText = btnCancelText;
    this.open = true;

    //Set modalInstance
    this.modalInstance = modalRef;

    return modalRef.result;
  }
  public isOpen() {
    return this.open;
  };
  public close(result) {
    this.modalInstance.close(result);
  };
  
  public dismiss(reason) {
    this.modalInstance.dismiss(reason);
  };

}

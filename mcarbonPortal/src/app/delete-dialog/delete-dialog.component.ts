import { Component, EventEmitter, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-delete-dialog',
  templateUrl: './delete-dialog.component.html',
  styleUrls: ['./delete-dialog.component.scss']
})
export class DeleteDialogComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  @Output() confirmEvent = new EventEmitter();
  @Output() cancelEvent = new EventEmitter();

  confirm() {
    this.confirmEvent.emit();
  }

  cancel() {
    this.cancelEvent.emit();
  }
}

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAccountDialogComponent } from './update-account-dialog.component';

describe('UpdateAccountDialogComponent', () => {
  let component: UpdateAccountDialogComponent;
  let fixture: ComponentFixture<UpdateAccountDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateAccountDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateAccountDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

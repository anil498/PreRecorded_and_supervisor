import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAccessDialogComponent } from './update-access-dialog.component';

describe('UpdateAccessDialogComponent', () => {
  let component: UpdateAccessDialogComponent;
  let fixture: ComponentFixture<UpdateAccessDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdateAccessDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateAccessDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateAccessDialogComponent } from './create-access-dialog.component';

describe('CreateAccessDialogComponent', () => {
  let component: CreateAccessDialogComponent;
  let fixture: ComponentFixture<CreateAccessDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CreateAccessDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateAccessDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

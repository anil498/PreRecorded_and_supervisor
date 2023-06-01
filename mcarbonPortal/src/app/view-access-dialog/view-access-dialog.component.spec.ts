import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewAccessDialogComponent } from './view-access-dialog.component';

describe('ViewAccessDialogComponent', () => {
  let component: ViewAccessDialogComponent;
  let fixture: ComponentFixture<ViewAccessDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewAccessDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewAccessDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

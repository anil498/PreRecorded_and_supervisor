import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewQuestionDialogComponent } from './view-question-dialog.component';

describe('ViewQuestionDialogComponent', () => {
  let component: ViewQuestionDialogComponent;
  let fixture: ComponentFixture<ViewQuestionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewQuestionDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewQuestionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

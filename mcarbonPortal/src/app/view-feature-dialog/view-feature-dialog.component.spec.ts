import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewFeatureDialogComponent } from './view-feature-dialog.component';

describe('ViewFeatureDialogComponent', () => {
  let component: ViewFeatureDialogComponent;
  let fixture: ComponentFixture<ViewFeatureDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewFeatureDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewFeatureDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

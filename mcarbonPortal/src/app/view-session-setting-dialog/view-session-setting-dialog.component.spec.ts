import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewSessionSettingDialogComponent } from './view-session-setting-dialog.component';

describe('ViewSessionSettingDialogComponent', () => {
  let component: ViewSessionSettingDialogComponent;
  let fixture: ComponentFixture<ViewSessionSettingDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ViewSessionSettingDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewSessionSettingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

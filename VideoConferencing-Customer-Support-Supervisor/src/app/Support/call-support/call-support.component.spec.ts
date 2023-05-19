import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CallSupportComponent } from './call-support.component';

describe('CallSupportComponent', () => {
  let component: CallSupportComponent;
  let fixture: ComponentFixture<CallSupportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CallSupportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CallSupportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

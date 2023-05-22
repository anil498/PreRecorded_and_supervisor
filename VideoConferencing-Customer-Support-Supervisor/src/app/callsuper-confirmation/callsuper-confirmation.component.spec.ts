import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CallSuperConfirmationComponent } from './callsuper-confirmation.component';

describe('CallComponent', () => {
  let component: CallSuperConfirmationComponent;
  let fixture: ComponentFixture<CallSuperConfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CallSuperConfirmationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CallSuperConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

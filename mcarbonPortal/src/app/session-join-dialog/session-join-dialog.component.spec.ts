import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionJoinDialogComponent } from './session-join-dialog.component';

describe('SessionJoinDialogComponent', () => {
  let component: SessionJoinDialogComponent;
  let fixture: ComponentFixture<SessionJoinDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SessionJoinDialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SessionJoinDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

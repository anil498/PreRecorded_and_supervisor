import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConnectpageComponent } from './connectpage.component';

describe('ConnectpageComponent', () => {
  let component: ConnectpageComponent;
  let fixture: ComponentFixture<ConnectpageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConnectpageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ConnectpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

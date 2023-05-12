import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DynamicSupportComponent } from './dynamic-support.component';

describe('DynamicSupportComponent', () => {
  let component: DynamicSupportComponent;
  let fixture: ComponentFixture<DynamicSupportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DynamicSupportComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DynamicSupportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

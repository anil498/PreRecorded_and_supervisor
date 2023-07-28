import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IcdcManagementComponent } from './icdc-management.component';

describe('IcdcManagementComponent', () => {
  let component: IcdcManagementComponent;
  let fixture: ComponentFixture<IcdcManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IcdcManagementComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IcdcManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

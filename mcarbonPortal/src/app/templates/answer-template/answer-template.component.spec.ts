import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnswerTemplateComponent } from './answer-template.component';

describe('AnswerTemplateComponent', () => {
  let component: AnswerTemplateComponent;
  let fixture: ComponentFixture<AnswerTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AnswerTemplateComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AnswerTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

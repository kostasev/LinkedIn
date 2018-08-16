import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangeInfoComponent } from './change-info.component';

describe('ChangeInfoComponent', () => {
  let component: ChangeInfoComponent;
  let fixture: ComponentFixture<ChangeInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangeInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangeInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

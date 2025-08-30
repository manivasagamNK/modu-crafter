import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewJoinerDashboard } from './new-joiner-dashboard';

describe('NewJoinerDashboard', () => {
  let component: NewJoinerDashboard;
  let fixture: ComponentFixture<NewJoinerDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewJoinerDashboard]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewJoinerDashboard);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

import { Component, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-supervisor-dashboard',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './supervisor-dashboard.html',
  styleUrl: './supervisor-dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,

})
export class SupervisorDashboard {
view = signal<string>('dashboard');
  headerTitle = computed(() => {
    switch(this.view()) {
      case 'dashboard':
        return 'Dashboard';
      case 'amcDetails':
        return 'AMC Details';
      case 'udemyTrack':
        return 'Udemy Track';
      default:
        return 'Dashboard';
    }
  });

  amcDetails = [
    { id: '001', name: 'John Doe', techstack: 'Angular', doj: '2023-01-15' },
    { id: '002', name: 'Jane Smith', techstack: 'React', doj: '2023-02-20' },
    { id: '003', name: 'Peter Jones', techstack: 'Python', doj: '2023-03-10' },
  ];

  amcInterviews = [
    { name: 'John Doe', interviewDate: '2023-01-05', feedback: 'Excellent' },
    { name: 'Jane Smith', interviewDate: '2023-02-10', feedback: 'Good' },
    { name: 'Peter Jones', interviewDate: '2023-03-01', feedback: 'Average' },
  ];

  amcDetailsWithInterview = [
    { id: '001', name: 'John Doe', techstack: 'Angular', doj: '2023-01-15', interviewDate: '2023-01-05', feedback: 'Excellent' },
    { id: '002', name: 'Jane Smith', techstack: 'React', doj: '2023-02-20', interviewDate: '2023-02-10', feedback: 'Good' },
    { id: '003', name: 'Peter Jones', techstack: 'Python', doj: '2023-03-10', interviewDate: '2023-03-01', feedback: 'Average' },
  ];

  udemyTracks = [
    { id: '001', amcName: 'John Doe', assignedCourse: 'Angular Fundamentals', completion: '85%' },
    { id: '002', amcName: 'Jane Smith', assignedCourse: 'React and Redux', completion: '60%' },
    { id: '003', amcName: 'Peter Jones', assignedCourse: 'Python for Data Science', completion: '95%' },
  ];

  onViewChange(newView: string): void {
    this.view.set(newView);
  }

}

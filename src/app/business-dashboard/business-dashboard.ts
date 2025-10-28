import { Component, signal, ChangeDetectionStrategy, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';





@Component({
  selector: 'app-business-dashboard',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './business-dashboard.html',
  styleUrls: ['./business-dashboard.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,

})
export class BusinessDashboard {
  // Signal to manage the current view state
    // Signal to manage the current view state
  currentView = signal<string>('dashboard');

  // Signals for the date range
  startDate = signal<string>('2025-03-01');
  endDate = signal<string>('2025-03-14');

  // Computed signal for the page title
  pageTitle = computed(() => {
    switch (this.currentView()) {
      case 'dashboard':
        return 'Dashboard';
      case 'amc':
        return 'AMC';
      case 'ams':
        return 'AMS';
      case 'interviews':
        return 'Interview Details';
      default:
        return 'Dashboard';
    }
  });
  
  

  // Function to change the view
  changeView(view: string) {
    this.currentView.set(view);
  }
  
  // Function to handle row clicks
  onRowClick(item: any) {
    console.log('Row clicked:', item);
    // You can implement your navigation logic here
  }

  // New Joiner data as a signal for reactivity
  newJoiners = signal([
    { name: 'John ', location: 'Chennai', doj: 'Mar 15, 2025' },
    { name: 'Harish', location: 'Chennai CTW ', doj: 'Mar 14, 2025' },
    { name: 'Priya', location: 'Pune', doj: 'Mar 12, 2025' }
  ]);

  // Full list of interview results
  interviewResults = signal([
    { name: 'John Doe', date: 'Mar 14', category: 'Java', feedback: 'Positive' },
    { name: 'Alex', date: 'Mar 12', category: 'Full Stack', feedback: 'Awaiting' },
    { name: 'Michael', date: 'Mar 11', category: 'DevOps', feedback: 'Positive' },
    { name: 'Emily', date: 'Mar 10', category: 'QA', feedback: 'Negative' },
    { name: 'Chris', date: 'Mar 9', category: 'Data Science', feedback: 'Positive' },
    { name: 'Jessica', date: 'Mar 8', category: 'Cloud', feedback: 'Awaiting' },
    { name: 'David', date: 'Mar 7', category: 'Java', feedback: 'Positive' },
    { name: 'Sophia', date: 'Mar 6', category: 'Full Stack', feedback: 'Awaiting' },
    { name: 'Olivia', date: 'Mar 5', category: 'DevOps', feedback: 'Positive' },
    { name: 'William', date: 'Mar 4', category: 'QA', feedback: 'Negative' },
    { name: 'Benjamin', date: 'Mar 3', category: 'UI/UX', feedback: 'Positive' }
  ]);
  
  // Computed signal for filtering interview results based on date range
  filteredInterviewResults = computed(() => {
    const start = new Date(this.startDate());
    const end = new Date(this.endDate());
    
    return this.interviewResults().filter(result => {
      const resultDate = new Date(`2025-${result.date}`);
      return resultDate >= start && resultDate <= end;
    });
  });

  // Function to trigger the date filter
  filterByDate() {
    // The computed signal will automatically update, but we can log for debugging
    console.log('Filtering results from', this.startDate(), 'to', this.endDate());
  }

  // AMC Grid Data as a signal
  amcData = signal([
    {
      empId: 1,
      empName: 'aaa',
      resume: 'aaa_resume.docx',
      feedback: 'Awaiting',
      ams: 'bbb',
      udamayTrack: '20% - AI'
    },
    {
      empId: 2,
      empName: 'abc',
      resume: 'abc_resume.docx',
      feedback: 'Awaiting',
      ams: 'not assigend',
      udamayTrack: 'not started'
    },
    {
      empId: 3,
      empName: 'xyz',
      resume: 'xyz_resume.docx',
      feedback: 'Positive',
      ams: 'ccc',
      udamayTrack: '50% - Web Dev'
    },
    {
      empId: 4,
      empName: 'def',
      resume: 'def_resume.docx',
      feedback: 'Negative',
      ams: 'bbb',
      udamayTrack: '90% - AI'
    },
    {
      empId: 5,
      empName: 'pqr',
      resume: 'pqr_resume.docx',
      feedback: 'Awaiting',
      ams: 'ddd',
      udamayTrack: '25% - DevOps'
    },
     {
      empId: 6,
      empName: 'xyz',
      resume: 'xyz_resume.docx',
      feedback: 'Positive',
      ams: 'ccc',
      udamayTrack: '50% - Web Dev'
    },
    {
      empId: 7,
      empName: 'def',
      resume: 'def_resume.docx',
      feedback: 'Negative',
      ams: 'bbb',
      udamayTrack: '90% - AI'
    },
    {
      empId: 8,
      empName: 'pqr',
      resume: 'pqr_resume.docx',
      feedback: 'Awaiting',
      ams: 'ddd',
      udamayTrack: '25% - DevOps'
    }
  ]);

  // AMS Data
  amsData = signal([
    { amsEid: 101, amsName: 'Alice', assignedAmcCount: 5, techStack: 'Java, Spring', managerName: 'Bob' },
    { amsEid: 102, amsName: 'Charlie', assignedAmcCount: 3, techStack: 'Python, Django', managerName: 'Bob' },
    { amsEid: 103, amsName: 'Dave', assignedAmcCount: 7, techStack: 'JavaScript, Angular', managerName: 'Eve' },
    { amsEid: 104, amsName: 'Frank', assignedAmcCount: 2, techStack: 'C#, .NET', managerName: 'Eve' },
    { amsEid: 105, amsName: 'Grace', assignedAmcCount: 6, techStack: 'React, Node.js', managerName: 'Heidi' },
    { amsEid: 106, amsName: 'Ivan', assignedAmcCount: 4, techStack: 'PHP, Laravel', managerName: 'Heidi' },
    { amsEid: 107, amsName: 'Judy', assignedAmcCount: 8, techStack: 'Ruby, Rails', managerName: 'Bob' },
    { amsEid: 108, amsName: 'Ken', assignedAmcCount: 1, techStack: 'Go, Gin', managerName: 'Eve' },
    { amsEid: 109, amsName: 'Liam', assignedAmcCount: 9, techStack: 'Python, Flask', managerName: 'Heidi' },
    { amsEid: 110, amsName: 'Mona', assignedAmcCount: 5, techStack: 'Java, Microservices', managerName: 'Bob' }
  ]);

  // Chart data and computed signal to create the conic gradient for the pie chart
  pieChartData = {
    labels: ['Billed AMC', 'New Joiner-Bench', 'Recent Bench'],
    datasets: [{
      data: [300, 500, 100],
      backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
    }]
  };

  pieChartGradient = computed(() => {
    const total = this.pieChartData.datasets[0].data.reduce((sum, value) => sum + value, 0);
    let gradientString = 'conic-gradient(';
    let startAngle = 0;
    
    this.pieChartData.datasets[0].data.forEach((value, index) => {
      const percentage = (value / total) * 100;
      const endAngle = startAngle + percentage;
      const color = this.pieChartData.datasets[0].backgroundColor[index];
      
      gradientString += `${color} ${startAngle}% ${endAngle}%,`;
      startAngle = endAngle;
    });

    // Remove the trailing comma and close the parenthesis
    return gradientString.slice(0, -1) + ')';
  });
}
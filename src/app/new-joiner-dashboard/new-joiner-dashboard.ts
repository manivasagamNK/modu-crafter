import { Component, signal, ChangeDetectionStrategy, computed, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// A type definition for the form object for better type safety
interface NewJoinerForm {
  name: string;
  empId: string;
  doj: string;
  location: string;
  techSkills: string[];
  resume: File | null;
}
@Component({
  selector: 'app-new-joiner-dashboard',
  imports: [CommonModule, FormsModule],
  standalone: true,
  templateUrl: './new-joiner-dashboard.html',
  styleUrl: './new-joiner-dashboard.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NewJoinerDashboard {

 // Signals for current view
  currentView = signal<string>('dashboard');

  // Signals for form data
  newJoinerForm = signal<{
    name: string;
    empId: string;
    doj: string;
    location: string;
    techSkills: string[];
    resume: File | null;
  }>({
    name: '',
    empId: '',
    doj: '',
    location: '',
    techSkills: [],
    resume: null
  });

  availableSkills = signal<string[]>(['Java, Spring', 'Angular', 'Python', 'AI', 'QA Tester']);
  otherSkill = signal<string>('');

  // Data for the Udemy chart and table
  udemyCourses = signal<{ name: string; percentage: number; color: string }[]>([
    { name: 'AI', percentage: 50, color: '#42a5f5' },
    { name: 'Angular', percentage: 20, color: '#ff9800' }
  ]);

  // Data for interview details
  interviewDetails = signal([
    { client: 'qww', date: 'Mar 25', feedback: 'Awaiting' },
    { client: 'asf', date: 'Mar 15', feedback: 'Awaiting' },
    { client: 'qww', date: 'Apr 09', feedback: 'Awaiting' },
    { client: 'fgh', date: 'Apr 25', feedback: 'Positive' }
  ]);

  // Computed signal for the page title
  pageTitle = computed(() => {
    switch (this.currentView()) {
      case 'dashboard':
        return 'Dashboard';
      case 'ams':
        return 'AMS Details';
      case 'udemy':
        return 'Udemy Details';
      case 'interviews':
        return 'Interview Details';
      default:
        return 'New Joiner Dashboard';
    }
  });

  // Computed signal for the Udemy chart gradient
  udemyChartGradient = computed(() => {
    const courses = this.udemyCourses();
    const total = courses.reduce((sum, value) => sum + value.percentage, 0);
    let gradientString = 'conic-gradient(';
    let startAngle = 0;

    courses.forEach((course) => {
      const percentage = (course.percentage / total) * 100;
      const endAngle = startAngle + percentage;
      gradientString += `${course.color} ${startAngle}% ${endAngle}%,`;
      startAngle = endAngle;
    });

    return gradientString.slice(0, -1) + ')';
  });

  changeView(view: string) {
    this.currentView.set(view);
  }

  // Simplified update method to fix the compilation error
  updateForm(field: keyof NewJoinerForm, value: string | File) {
    this.newJoinerForm.update(form => ({ ...form, [field]: value }));
  }

  updateSkills(skill: string, event: Event) {
    const isChecked = (event.target as HTMLInputElement).checked;
    this.newJoinerForm.update(form => {
      const skills = isChecked
        ? [...form.techSkills, skill]
        : form.techSkills.filter(s => s !== skill);
      return { ...form, techSkills: skills };
    });
  }

  addOtherSkill() {
    const skill = this.otherSkill().trim();
    if (skill && !this.newJoinerForm().techSkills.includes(skill)) {
      this.newJoinerForm.update(form => ({
        ...form,
        techSkills: [...form.techSkills, skill]
      }));
      this.otherSkill.set(''); // Clear the input field
    }
  }

  onFileSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.updateForm('resume', file);
    }
  }

  saveNewJoiner() {
    console.log('New Joiner Data Saved:', this.newJoinerForm());
    // You can add logic here to send the data to a backend or a service
  }
}

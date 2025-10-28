import { Component, signal, ChangeDetectionStrategy, computed, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
// FIX: Kept FormsModule, as you are using [ngModel]
import { FormsModule } from '@angular/forms';
import { HttpClient,HttpHeaders } from '@angular/common/http';

// A type definition for the form object for better type safety
interface NewJoinerForm {
    name: string;
    empId: string;
    doj: string;
    location: string;
    techStack: string;
    resume: File | null;
}
interface UdemyCourse {
    title: string;
    skill_gap: string;
    link: string;
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
    private apiUrl = 'http://localhost:8080/api/employee';
    currentView = signal<string>('dashboard');
    isExtractingSkills = signal(false);
    // This signal name is CORRECT: 'udemyCourses' (used in HTML as udemyCourses())
    udemyCourses: WritableSignal<UdemyCourse[]> = signal([
        { title: 'Mastering Data Structures & Algorithms using Java', skill_gap: 'Datastructure', link: 'https://udemy.link.datastructures' },
        { title: 'Angular 18 & Signals', skill_gap: 'Frontend', link: 'https://udemy.link.angular' },
        { title: 'Spring Boot Microservices', skill_gap: 'Backend', link: 'https://udemy.link.springboot' },
        // Add more mock data if needed
    ]);

    // This signal name is CORRECT: 'newJoinerForm' (the name suggested by the error)
    newJoinerForm = signal<NewJoinerForm>({
        name: '',
        empId: '',
        doj: '',
        location: '',
        techStack: '', 
        resume: null
    });


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


    constructor(private http: HttpClient) {}

    changeView(view: string) {
        this.currentView.set(view);
    }

    updateForm(field: keyof NewJoinerForm, value: string | File) {
        this.newJoinerForm.update(form => ({ ...form, [field]: value }));
    }

    onFileSelected(event: Event) {
        const file = (event.target as HTMLInputElement).files?.[0];
        if (file) {
            this.updateForm('resume', file);
            this.isExtractingSkills.set(true); 
            this.extractSkillsFromResume(file);
        }
    }

    extractSkillsFromResume(file: File): void {
        const formData = new FormData();
        formData.append('file', file);

        this.http.post<any>(`${this.apiUrl}/extractSkills`, formData).subscribe({
            next: (response) => {
                const extractedSkills: string = response.techStack || '';

                this.newJoinerForm.update(form => ({ ...form, techStack: extractedSkills }));

                this.isExtractingSkills.set(false);
                console.log('Skills extracted and updated:', extractedSkills);
                alert('Skills extracted successfully! Please save the form.');
            },
            error: (err) => {
                console.error('Skill extraction failed:', err);
                alert('Failed to extract skills automatically. Please enter them manually and save.');
                this.isExtractingSkills.set(false);
                this.newJoinerForm.update(form => ({ ...form, techStack: '' }));
            }
        });
    }

    saveNewJoiner() {
        const formDataState = this.newJoinerForm();

        if (!formDataState.name || !formDataState.empId) {
            alert('Please fill in Name and Employee ID.');
            return;
        }

        // 🔥 New: Mandatory check for file (since backend expects it)
        if (!formDataState.resume) {
            alert('Please upload a resume file.');
            return;
        }

        // 1. Prepare the JSON data for the 'emp' part (excluding the file)
        const employeeMetadata = {
            name: formDataState.name,
            // Assuming empId should be a number for the backend DTO, if it's a string, remove parseInt
            empId: parseInt(formDataState.empId, 10), 
            doj: formDataState.doj,
            location: formDataState.location,
            techStack: formDataState.techStack 
        };

        const finalFormData = new FormData();

        // 2. Append the JSON data as a Blob (key: 'emp')
        // The key 'emp' must match the @RequestPart("emp") in your Spring Boot controller.
        const employeeBlob = new Blob([JSON.stringify(employeeMetadata)], { type: 'application/json' });
        finalFormData.append('emp', employeeBlob);

        // 3. Append the File data (key: 'file')
        // The key 'file' must match the @RequestPart("file") in your Spring Boot controller.
        finalFormData.append('file', formDataState.resume, formDataState.resume.name);


        // 4. Send the request as multipart/form-data
        this.http.post<any>(`${this.apiUrl}/addProfile`, finalFormData).subscribe({
            next: (response) => {
                console.log('Profile saved successfully:', response);
                alert('New Joiner Profile Saved Successfully!');
            },
            error: (err) => {
                console.error('Profile save failed:', err);
                alert('Failed to save profile. Please check the console for errors.');
            }
        });
    }
}
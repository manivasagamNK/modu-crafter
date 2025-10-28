import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

export interface InterviewResult {
  interviewId: number; // New: Unique ID for the interview record
  empId: number;       // New: Employee ID
  name: string;
  client: string;
  date: Date;
  result: string;
  feedback: string;
}

export interface DialogData {
  empId: number;
  empName: string;
  ams: string;
  billable: boolean;
  risktype: string;
  resumeFile?: File | null;
}

@Injectable({
  providedIn: 'root'
})
export class BusinessDashboardService {
 // private base = '/assets/mock';
private apiBase = 'http://localhost:8080/api';
  constructor(private http: HttpClient) {}

  // Load a single mock JSON containing all dashboard arrays
getInterviewDetailsByEmp(empId: number): Observable<InterviewResult[]> {
    return this.http.get<InterviewResult[]>(`${this.apiBase}/interviews/employee/${empId}`);
  }

 updateInterviewDetails(interviewId: number, payload: any): Observable<any> {
    console.log(`Updating interview ID: ${interviewId} with payload:`, payload);
    // Corrected template literal syntax to properly include interviewId
    return this.http.put(`${this.apiBase}/interviews/${interviewId}`, payload);
  }
  getAmcEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/employee/role/amc`);
  }
   getAmsEmployees(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/employee/role/ams`);
  }

  getInterviewData(): Observable<InterviewResult[]> {
    return this.http.get<InterviewResult[]>(`${this.apiBase}/interviews/summary`);
  }
  // Optional: remove this if you no longer use the mock file
  // getAmcData() {
  //   return this.getDashboard().pipe(map(d => d.amcData || []));
  // }

 uploadResume(empId: number, file: File | null) {
    const formData = new FormData();
    if (file) formData.append('file', file);

    return this.http.post('${this.apiBase}/employee/${empId}/upload', formData);
  }
  
  downlaodResume(empId: number): Observable<Blob> {
    return this.http.get<Blob>(`${this.apiBase}/employee/resume/${empId}`, {
      responseType: 'blob' as 'json'
    });
  }
  



  
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { NewJoiner, AMSDetails, UdemyCourse, Interview } from '../models/interfaces';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // New Joiner APIs
  getNewJoiner(empId: string): Observable<NewJoiner> {
    return this.http.get<NewJoiner>(`${this.apiUrl}/new-joiners/${empId}`);
  }

  saveNewJoiner(data: NewJoiner): Observable<NewJoiner> {
    return this.http.post<NewJoiner>(`${this.apiUrl}/new-joiners`, data);
  }

  uploadResume(empId: string, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('resume', file);
    return this.http.post(`${this.apiUrl}/new-joiners/${empId}/resume`, formData);
  }

  // AMS Details APIs
  getAMSDetails(empId: string): Observable<AMSDetails> {
    return this.http.get<AMSDetails>(`${this.apiUrl}/ams-details/${empId}`);
  }

  // Udemy Course APIs
  getUdemyCourses(empId: string): Observable<UdemyCourse[]> {
    return this.http.get<UdemyCourse[]>(`${this.apiUrl}/udemy-courses/${empId}`);
  }

  // Interview APIs
  getInterviews(empId: string): Observable<Interview[]> {
    return this.http.get<Interview[]>(`${this.apiUrl}/interviews/${empId}`);
  }
}
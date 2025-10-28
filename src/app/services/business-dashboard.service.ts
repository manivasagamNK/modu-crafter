import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, delay, map } from 'rxjs/operators';

export interface InterviewResult {
  empId: number;
  name: string;
  date: string;
  category: string;
  feedback: string;
  client: string;
  result: string;
}

export interface DialogData {
  empId: number;
  empName: string;
  resume: string;
  feedback: string;
  ams: string;
  udamayTrack: string;
}

@Injectable({
  providedIn: 'root'
})
export class BusinessDashboardService {
  private base = '/assets/mock';

  constructor(private http: HttpClient) {}

  // Load a single mock JSON containing all dashboard arrays
  getDashboard(): Observable<any> {
    return this.http.get(`${this.base}/business-dashboard.json`).pipe(
      catchError(err => {
        console.error('Failed to load mock dashboard data', err);
        return of({ newJoiners: [], interviewResults: [], amcData: [], amsData: [] });
      })
    );
  }

  getNewJoiners() {
    return this.getDashboard().pipe(map(d => d.newJoiners || []));
  }

  getInterviewResults() {
    return this.getDashboard().pipe(map(d => d.interviewResults || []));
  }

  getAmcData() {
    return this.getDashboard().pipe(map(d => d.amcData || []));
  }

  getAmsData() {
    return this.getDashboard().pipe(map(d => d.amsData || []));
  }

  // Simulate resume upload and return the file metadata
  uploadResume(empId: number, file: File | null) {
    const result = { empId, name: file?.name ?? null };
    // simulate network latency
    return of(result).pipe(delay(500));
  }
}

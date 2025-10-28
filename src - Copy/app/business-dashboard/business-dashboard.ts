import { Component, signal, ChangeDetectionStrategy, computed, Inject, ViewChild, ElementRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {MatDialogModule} from '@angular/material/dialog';
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition} from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { HttpClientModule } from '@angular/common/http';
import { BusinessDashboardService } from '../services/business-dashboard.service';

// Top-level interface for interview results
export interface InterviewResult {
    name: string;
client: any;
  date: any; // e.g. 'Mar 14'
  result: any;
  feedback: string;
}
export interface UpdateResultRequest {
  result: string;
  feedback: string;
}


@Component({
  selector: 'app-business-dashboard',
  standalone: true,
  imports: [CommonModule,FormsModule, MatDialogModule, MatFormFieldModule, HttpClientModule],
  templateUrl: './business-dashboard.html',
  styleUrls: ['./business-dashboard.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,

})
export class BusinessDashboard implements OnInit {
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
  
  constructor(public dialog: MatDialog, private _snackBar: MatSnackBar, private service: BusinessDashboardService){}  

  horizontalPosition: MatSnackBarHorizontalPosition = 'end';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  openNotification() {
    this._snackBar.open("Notification Alert", "Ok", {
      horizontalPosition: this.horizontalPosition,
      verticalPosition: this.verticalPosition,
    });
  }

  // Function to change the view
  changeView(view: string) {
    this.currentView.set(view);
  }
  
  // Function to handle row clicks
  onRowClick(item: any) {
    console.log('Row clicked:', item);
    //this.onUploadResume(item);
    // You can implement your navigation logic here
  }

 
  // New Joiner data as a signal for reactivity (will be loaded from service)
  newJoiners = signal<any[]>([]);
  // Search and sort for New Joiners
  newJoinerSearch = signal<string>('');
  newJoinerSort = signal<{ key: string | null; dir: 'asc' | 'desc' | null }>({ key: null, dir: null });

  filteredNewJoiners = computed(() => {
    const search = this.newJoinerSearch().toLowerCase().trim();
    let list = this.newJoiners();

    if (search) {
      list = list.filter((r: any) =>
        (r.name || '').toLowerCase().includes(search) ||
        (r.location || '').toLowerCase().includes(search) ||
        (r.doj || '').toLowerCase().includes(search)
      );
    }

    const sortState = this.newJoinerSort();
    if (sortState.key) {
      list = [...list].sort((a: any, b: any) => {
        const ak = a[sortState.key!];
        const bk = b[sortState.key!];
        if (ak == null) return 1;
        if (bk == null) return -1;
        const cmp = String(ak).localeCompare(String(bk), undefined, { numeric: true, sensitivity: 'base' });
        return sortState.dir === 'asc' ? cmp : -cmp;
      });
    }

    return list;
  });

  toggleNewJoinerSort(key: string) {
    const s = this.newJoinerSort();
    if (s.key === key) {
      const next = s.dir === 'asc' ? 'desc' : s.dir === 'desc' ? null : 'asc';
      this.newJoinerSort.set({ key: next ? key : null, dir: next });
    } else {
      this.newJoinerSort.set({ key, dir: 'asc' });
    }
  }

  // Pagination for new joiners
  newJoinerPage = signal<number>(0);
  newJoinerPageSize = signal<number>(5);

  newJoinerTotalPages = computed(() => {
    const total = this.filteredNewJoiners().length;
    return Math.max(1, Math.ceil(total / this.newJoinerPageSize()));
  });

  paginatedNewJoiners = computed(() => {
    const page = this.newJoinerPage();
    const size = this.newJoinerPageSize();
    const list = this.filteredNewJoiners();
    const start = page * size;
    return list.slice(start, start + size);
  });

  goToNewJoinerPage(index: number) {
    const max = this.newJoinerTotalPages();
    const clamped = Math.max(0, Math.min(index, max - 1));
    this.newJoinerPage.set(clamped);
  }

  nextNewJoinerPage() {
    this.goToNewJoinerPage(this.newJoinerPage() + 1);
  }

  prevNewJoinerPage() {
    this.goToNewJoinerPage(this.newJoinerPage() - 1);
  }

  setNewJoinerPageSize(size: number) {
    this.newJoinerPageSize.set(size);
    // reset to first page when page size changes
    this.newJoinerPage.set(0);
  }

  // Full list of interview results (loaded from service)
  interviewResults = signal<InterviewResult[]>([]);
  // Search and sort state for interviews
  interviewSearch = signal<string>('');
  interviewSort = signal<{ key: keyof InterviewResult | null; dir: 'asc' | 'desc' | null }>({ key: null, dir: null });
  
  // Computed signal for filtering interview results based on date range
  // (old simple computed removed in favor of the richer computed defined below)
  

  // Function to trigger the date filter
  filterByDate() {
    // The computed signal will automatically update, but we can log for debugging
    console.log('Filtering results from', this.startDate(), 'to', this.endDate());
  }
  // Search and sort state for AMC
  amcSearch = signal<string>('');
  amcSort = signal<{ key: string | null; dir: 'asc' | 'desc' | null }>({ key: null, dir: null });

  // Computed filtered & searched interview results (date filter + search + sort)
  filteredInterviewResults = computed(() => {
    const search = this.interviewSearch().toLowerCase().trim();
    const startDateStr = this.startDate();
    const endDateStr = this.endDate();
    let list = this.interviewResults();

    // Apply date range filter if both dates are set
    // if (startDateStr && endDateStr) {
    //   const startDate = new Date(startDateStr);
    //   const endDate = new Date(endDateStr);
    //   // Adjust end date to include the entire day
    //   endDate.setHours(23, 59, 59, 999);

    //   list = list.filter(r => {
    //     // Convert date string (e.g., 'Mar 14') to a Date object in current year
    //     const [month, day] = r.date.split(' ');
    //     const dateStr = `${month} ${day}, ${new Date().getFullYear()}`;
    //     const itemDate = new Date(dateStr);
    //     return itemDate >= startDate && itemDate <= endDate;
    //   });
    // }

    // Apply search filter
    if (search) {
      list = list.filter(r =>
        (r.name || '').toLowerCase().includes(search) ||
        (r.result || '').toLowerCase().includes(search) ||
        (r.feedback || '').toLowerCase().includes(search)
      );
    }

    // Apply sorting
    const sortState = this.interviewSort();
    if (sortState.key) {
      list = [...list].sort((a, b) => {
        const ak = (a as any)[sortState.key!];
        const bk = (b as any)[sortState.key!];
        if (ak == null) return 1;
        if (bk == null) return -1;
        const cmp = String(ak).localeCompare(String(bk), undefined, { numeric: true, sensitivity: 'base' });
        return sortState.dir === 'asc' ? cmp : -cmp;
      });
    }

    return list;
  });

  // Pagination for interviews (dashboard small widget)
  interviewPage = signal<number>(0);
  interviewPageSize = signal<number>(5);

  interviewTotalPages = computed(() => {
    const total = this.filteredInterviewResults().length;
    return Math.max(1, Math.ceil(total / this.interviewPageSize()));
  });

  paginatedInterviewResults = computed(() => {
    const page = this.interviewPage();
    const size = this.interviewPageSize();
    const list = this.filteredInterviewResults();
    const start = page * size;
    return list.slice(start, start + size);
  });

  goToInterviewPage(index: number) {
    const max = this.interviewTotalPages();
    const clamped = Math.max(0, Math.min(index, max - 1));
    this.interviewPage.set(clamped);
  }

  nextInterviewPage() {
    this.goToInterviewPage(this.interviewPage() + 1);
  }

  prevInterviewPage() {
    this.goToInterviewPage(this.interviewPage() - 1);
  }

  setInterviewPageSize(size: number) {
    this.interviewPageSize.set(size);
    this.interviewPage.set(0);
  }

  // Computed filtered & searched AMC data
  filteredAmcData = computed(() => {
    const search = this.amcSearch().toLowerCase().trim();
    let list = this.amcData();

    if (search) {
      list = list.filter((r: any) =>
        String(r.empId).toLowerCase().includes(search) ||
        (r.empName || '').toLowerCase().includes(search) ||
        (r.ams || '').toLowerCase().includes(search) ||
        (r.billable || '').toLowerCase().includes(search) ||
        (r.riskCategory || '').toLowerCase().includes(search) ||
        (r.resume || '').toLowerCase().includes(search)
            );
    }

    const sortState = this.amcSort();
    if (sortState.key) {
      list = [...list].sort((a: any, b: any) => {
        const ak = a[sortState.key!];
        const bk = b[sortState.key!];
        if (ak == null) return 1;
        if (bk == null) return -1;
        const cmp = String(ak).localeCompare(String(bk), undefined, { numeric: true, sensitivity: 'base' });
        return sortState.dir === 'asc' ? cmp : -cmp;
      });
    }

    return list;
  });

  // Pagination for AMC view
  amcPage = signal<number>(0);
  amcPageSize = signal<number>(5);

  amcTotalPages = computed(() => {
    const total = this.filteredAmcData().length;
    return Math.max(1, Math.ceil(total / this.amcPageSize()));
  });

  paginatedAmcData = computed(() => {
    const page = this.amcPage();
    const size = this.amcPageSize();
    const list = this.filteredAmcData();
    const start = page * size;
    return list.slice(start, start + size);
  });

  goToAmcPage(index: number) {
    const max = this.amcTotalPages();
    const clamped = Math.max(0, Math.min(index, max - 1));
    this.amcPage.set(clamped);
  }

  nextAmcPage() { this.goToAmcPage(this.amcPage() + 1); }
  prevAmcPage() { this.goToAmcPage(this.amcPage() - 1); }
  setAmcPageSize(size: number) { this.amcPageSize.set(size); this.amcPage.set(0); }

  // Toggle sort helpers
  toggleInterviewSort(key: keyof InterviewResult) {
    const s = this.interviewSort();
    if (s.key === key) {
      const next = s.dir === 'asc' ? 'desc' : s.dir === 'desc' ? null : 'asc';
      this.interviewSort.set({ key: next ? key : null, dir: next });
    } else {
      this.interviewSort.set({ key, dir: 'asc' });
    }
  }

  toggleAmcSort(key: string) {
    const s = this.amcSort();
    if (s.key === key) {
      const next = s.dir === 'asc' ? 'desc' : s.dir === 'desc' ? null : 'asc';
      this.amcSort.set({ key: next ? key : null, dir: next });
    } else {
      this.amcSort.set({ key, dir: 'asc' });
    }
  }

  // Search and sort for AMS
  amsSearch = signal<string>('');
  amsSort = signal<{ key: string | null; dir: 'asc' | 'desc' | null }>({ key: null, dir: null });

  filteredAmsData = computed(() => {
    const search = this.amsSearch().toLowerCase().trim();
    let list = this.amsData();

    if (search) {
      list = list.filter((r: any) =>
        String(r.amsEid).toLowerCase().includes(search) ||
        (r.amsName || '').toLowerCase().includes(search) ||
        String(r.assignedAmcCount).toLowerCase().includes(search) ||
        (r.techStack || '').toLowerCase().includes(search) ||
        (r.managerName || '').toLowerCase().includes(search)
      );
    }

    const sortState = this.amsSort();
    if (sortState.key) {
      list = [...list].sort((a: any, b: any) => {
        const ak = a[sortState.key!];
        const bk = b[sortState.key!];
        if (ak == null) return 1;
        if (bk == null) return -1;
        const cmp = String(ak).localeCompare(String(bk), undefined, { numeric: true, sensitivity: 'base' });
        return sortState.dir === 'asc' ? cmp : -cmp;
      });
    }

    return list;
  });

  // Pagination for AMS view
  amsPage = signal<number>(0);
  amsPageSize = signal<number>(5);

  amsTotalPages = computed(() => {
    const total = this.filteredAmsData().length;
    return Math.max(1, Math.ceil(total / this.amsPageSize()));
  });

  paginatedAmsData = computed(() => {
    const page = this.amsPage();
    const size = this.amsPageSize();
    const list = this.filteredAmsData();
    const start = page * size;
    return list.slice(start, start + size);
  });

  goToAmsPage(index: number) { const max = this.amsTotalPages(); this.amsPage.set(Math.max(0, Math.min(index, max-1))); }
  nextAmsPage() { this.goToAmsPage(this.amsPage()+1); }
  prevAmsPage() { this.goToAmsPage(this.amsPage()-1); }
  setAmsPageSize(size: number) { this.amsPageSize.set(size); this.amsPage.set(0); }

  // Pagination for Interview Details (full view)
  interviewDetailsPage = signal<number>(0);
  interviewDetailsPageSize = signal<number>(10);

  interviewDetailsTotalPages = computed(() => {
    const total = this.filteredInterviewResults().length;
    return Math.max(1, Math.ceil(total / this.interviewDetailsPageSize()));
  });

  paginatedInterviewDetails = computed(() => {
    const page = this.interviewDetailsPage();
    const size = this.interviewDetailsPageSize();
    const list = this.filteredInterviewResults();
    const start = page * size;
    return list.slice(start, start + size);
  });

  goToInterviewDetailsPage(index: number) { const max = this.interviewDetailsTotalPages(); this.interviewDetailsPage.set(Math.max(0, Math.min(index, max-1))); }
  nextInterviewDetailsPage() { this.goToInterviewDetailsPage(this.interviewDetailsPage()+1); }
  prevInterviewDetailsPage() { this.goToInterviewDetailsPage(this.interviewDetailsPage()-1); }
  setInterviewDetailsPageSize(size: number) { this.interviewDetailsPageSize.set(size); this.interviewDetailsPage.set(0); }

  toggleAmsSort(key: string) {
    const s = this.amsSort();
    if (s.key === key) {
      const next = s.dir === 'asc' ? 'desc' : s.dir === 'desc' ? null : 'asc';
      this.amsSort.set({ key: next ? key : null, dir: next });
    } else {
      this.amsSort.set({ key, dir: 'asc' });
    }
  }

  // AMC Grid Data as a signal (loaded from service)
  amcData = signal<any[]>([]);

  // AMS Data (loaded from service)
  amsData = signal<any[]>([]);

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

  onUploadResume(row:DialogData) {    
    let file: File | null = null;
  const dialogRef = this.dialog.open(BusinessDashboardDialog, {
        width: '400px',
        data: row
      });

      dialogRef.afterClosed().subscribe(result => {
        // result is the selected File or null
        if(result instanceof File) {
          this.service.uploadResume(row.empId, result).subscribe(res => {
            const name = (res as any).name;
            this.amcData.update(e =>
              e.map(item => item.empId === row?.empId ? { ...item, resume: name } : item)
            );
            this._snackBar.open('Resume uploaded: ' + name, 'Close', { duration: 2000 });
          });
        } else {
          console.log('No file selected');
        }
      });
  }

  ngOnInit(): void {
    this.loadAmcEmployees();
    this.loadInterviewSummary();
    this.loadAmsEmployees();
  }
loadInterviewSummary() {
  this.service.getInterviewData().subscribe({
    next: (data) => {
      // Backend returns EmployeeInterviewSummaryDTO list
      const mapped = data.map((i: any) => ({
        name: i.name || i.empName || 'Unknown',
        client: i.client || i.clientName || 'N/A',
        date: i.interviewDate || i.interviewDate || '',
        result: i.result || i.status || 'N/A',
        feedback: i.feedback || i.remarks || '-',
      }));

      this.interviewResults.set(mapped);
      console.log('Interview summary loaded:', mapped);
    },
    error: (err) => {
      console.error(' Error loading interview summary', err);
    }
  });
}
  loadAmcEmployees() {
  this.service.getAmcEmployees().subscribe({
    next: (data) => {
      // ✅ Transform backend data to only what UI needs
      const mapped = data.map((emp: any) => ({
        empId: emp.empId,
        empName: emp.name,
        ams: emp.amsName,
        billable: emp.isBillable ? 'Yes' : 'No',
        riskCategory: this.calculateRiskCategory(emp), // optional logic
        resume: emp.resumeUrl // if backend supports resume upload/download
      }));

      this.amcData.set(mapped);
      console.log('AMC Employees (mapped):', mapped);
    },
    error: (err) => {
      console.error('Error loading AMC employees', err);
    }
  });
}

loadAmsEmployees() {
  this.service.getAmsEmployees().subscribe({
    next: (data) => {
      // ✅ Transform backend data to only what UI needs
      const mapped = data.map((emp: any) => ({
        amsEid: emp.empId,
        amsName: emp.name,
        assignedAmcCount:2,
        managerName: emp.managerName,
      }));

      this.amsData.set(mapped);
      console.log('AMS Employees (mapped):', mapped);
    },
    error: (err) => {
      console.error('Error loading AMS Data', err);
    }
  });
}
downloadResume(emp: any) {
  this.service.downlaodResume(emp.empId).subscribe({
    next: (blob) => {
      const fileURL = URL.createObjectURL(blob);
      window.open(fileURL, '_blank');
    },
    error: (err) => {
      console.error('Error downloading resume', err);
    }
  });
}
private calculateRiskCategory(emp: any): string {
  if (!emp.isBillable) return 'At Risk';
  if (emp.techStack?.includes('Kafka')) return 'High Demand';
  return 'Stable';
}

   // Open the InterviewResultDialog to view/edit a specific interview result
  viewDetails(result: InterviewResult) {
    const dialogRef = this.dialog.open(InterviewResultDialog, {
      width: '90vw',      // responsive width (90% of viewport)
      maxWidth: '1400px', // cap the maximum width
      height: '80vh',     // taller dialog for more space
      data: result
    });

    dialogRef.afterClosed().subscribe((res: any) => {
      if (!res) return;

      // If dialog returned saved items (original updated + newly added)
      if (res.action === 'save' && Array.isArray(res.items)) {
        const items: InterviewResult[] = res.items;

        // First item corresponds to the original (updated) row
        let originalUpdated = items[0];

        // Remaining items, if any, are newly added rows
        let newItems = items.slice(1).filter(i => i && (i.name || i.client || i.date || i.result || i.feedback));
        let newItem = newItems[newItems.length - 1];

        if(newItems.length > 0) {
          this.interviewResults.update(list => {
            return list.map(r => r.name === result.name ? { ...r, client: newItem?.client, date: newItem?.date, 
              result: newItem?.result, feedback: newItem?.feedback } : r);
          });
        } else {
          this.interviewResults.update(list => {
           return list.map(r => r.name === result.name ? { ...r, ...originalUpdated } : r);
        });}

        this.interviewResults.set([...this.interviewResults()]);

          console.log(this.interviewResults());        
        
      }
    });
  }


}

export interface DialogData {
  empId: number;
  empName: string;
  resume: string;
  feedback: string;
  ams: string;
  udamayTrack: string;
}

@Component({
  selector: 'business-dashboard-dialog',
  templateUrl: 'business-dashboard-dialog.html',
  styleUrl: './business-dashboard-dialog.css',
  standalone: true,
  imports: [CommonModule,FormsModule, MatDialogModule, MatFormFieldModule,
    MatInputModule,MatIconModule
  ],
})
export class BusinessDashboardDialog {
  @ViewChild('fileInput') fileInput!: ElementRef;
  selectedFile: File | null = null;

  constructor(
    public dialogRef: MatDialogRef<BusinessDashboardDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];      
    } else {
      this.selectedFile = null;     
    }
  }

  onUpload(): void {
    this.dialogRef.close(this.selectedFile); // Close dialog and pass the selected file
  }

  onCancel(): void {
    this.dialogRef.close(null); // Close dialog without a file
  }

}

@Component({
  selector: 'interview-result-dialog',
  templateUrl: './business-dashboard-interview-dialog.html',
  styleUrl: './business-dashboard-interview-dialog.css',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule],
})
export class InterviewResultDialog {
  // internal editable rows; original indicates the incoming item
  rows: { item: InterviewResult; original?: boolean }[] = [];
  empName: string = '';

  constructor(
    public dialogRef: MatDialogRef<InterviewResultDialog>,
    @Inject(MAT_DIALOG_DATA) public data: InterviewResult
  ) {
    // start with the provided item as the first row
    this.rows = [{ item: { ...data }, original: true }];
    this.empName = data.name;    
  }

  // Save: return all rows back to the caller
  onSave(): void {
    const items = this.rows.map(r => r.item);
    for (let i = 0; i < items.length; i++) {
      if(items[i].name === '' || items[i].client === '' || items[i].date === '' ||
        items[i].result === '' || items[i].feedback === '') {
          alert('Please fill all fields for row ' + (i + 1));
          return;
        }
      }
        this.dialogRef.close({ action: 'save', items });
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }

  // Add a new empty row inside the dialog for the user to fill
  onAdd(): void {
    const originalName = this.rows[0]?.item?.name ?? '';
    const now = new Date();
    const dateStr = now.toLocaleString('en-US', { month: 'short' }) + ' ' + now.getDate();
    this.rows.push({ item: {
      name: originalName, date: dateStr, feedback: '',
      result: '',
      client: ''
    }, original: false });
  }
}
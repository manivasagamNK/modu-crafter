// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { NewJoinerDashboard } from './new-joiner-dashboard/new-joiner-dashboard';
import { BusinessDashboard } from './business-dashboard/business-dashboard';
import { SupervisorDashboard } from './supervisor-dashboard/supervisor-dashboard';
import { AuthGuard } from './guards/auth.guard';
// No need to import FormsModule here

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'new-joiner', component: NewJoinerDashboard },
  { path: 'business-dashboard', component: BusinessDashboard, canActivate: [AuthGuard] },
  { path: 'supervisor-dashboard', component: SupervisorDashboard, canActivate: [AuthGuard] },
  { path: '**', redirectTo: 'login' }
];
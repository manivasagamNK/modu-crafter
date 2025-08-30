import { Component, ChangeDetectionStrategy, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { FormsModule } from '@angular/forms'; 

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
    standalone: true,
  imports: [FormsModule], // Include FormsModule in imports
  styleUrls: ['./login.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,

})
export class LoginComponent {
   username: string = '';
  password: string = '';

  private users = [
        { username: 'newjoin', password: 'join123', role: 'new-joiner', route: '/new-joiner' },
    { username: 'ba_user', password: 'ba123', role: 'business', route: '/business-dashboard' },
    { username: 'admin', password: 'admin123', role: 'supervisor', route: '/supervisor-dashboard' }
  ];

   constructor(private router: Router, private authService: AuthService) {}

  onLogin() {
    const matchedUser = this.users.find(u =>
      u.username === this.username &&
      u.password === this.password
    );

    if (matchedUser) {
      // FIX: Call the login method from AuthService to set the authentication state.
      this.authService.login(matchedUser.username, matchedUser.role);
      this.router.navigate([matchedUser.route]);
    } else {
      console.log('Invalid username or password. Please try again.');
    }
  }
}
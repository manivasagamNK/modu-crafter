import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isLoggedIn = false;
  private userRole: string | null = null;

  login(username: string, role: string) {
    this.isLoggedIn = true;
    this.userRole = role;
  }

  logout() {
    this.isLoggedIn = false;
    this.userRole = null;
  }

  isAuthenticated(): boolean {
    return this.isLoggedIn;
  }

  getRole(): string | null {
    return this.userRole;
  }
}
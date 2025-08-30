import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {FormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, MatToolbarModule, MatIconModule, MatInputModule,
    CommonModule, FormsModule, MatButtonModule, MatFormFieldModule, MatMenuModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('modu-crafter');
  value = 'Search';
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  navigateToNew() {
    this.router.navigateByUrl('/newjoiner');
  }

  navigateToHome() {
    this.router.navigate(['..'], { relativeTo: this.route });   
  }

}

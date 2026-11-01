import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet],
  template: `
    <div class="app-shell">
      <header class="topbar">
        <a class="brand" routerLink="/" aria-label="Stockroom home">
          <span class="brand-mark" aria-hidden="true">S</span>
          <span class="brand-copy">
            <span class="brand-name">stockroom</span>
            <span class="brand-caption">PRODUCT MANAGEMENT</span>
          </span>
        </a>
        <nav class="topbar-actions" aria-label="Account">
          <a class="nav-link" routerLink="/" routerLinkActive="nav-link-active" [routerLinkActiveOptions]="{ exact: true }">Discover</a>
          <a *ngIf="auth.isAdmin" class="nav-link admin-link" routerLink="/admin">Admin</a>
          <ng-container *ngIf="auth.user$ | async as user; else signedOut">
            <span class="account-email">{{ user.email }}</span>
            <button class="nav-action" type="button" (click)="auth.logout()">Log out</button>
          </ng-container>
          <ng-template #signedOut>
            <a class="nav-link" routerLink="/login">Log in</a>
            <a class="nav-cta" routerLink="/register">Sign up</a>
          </ng-template>
          <span class="status-dot" aria-hidden="true"></span>
        </nav>
      </header>

      <main class="page-content">
        <router-outlet></router-outlet>
      </main>

      <footer class="site-footer">
        <span>Stockroom</span>
        <span>Simple, thoughtful inventory management.</span>
      </footer>
    </div>
  `
})
export class AppComponent {
  readonly title = 'product-frontend1';

  constructor(public auth: AuthService) {}
}

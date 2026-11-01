import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-auth',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <section class="auth-layout">
      <div class="auth-aside">
        <p class="eyebrow">A BETTER WAY TO DISCOVER</p>
        <h1>Find your next favorite.</h1>
        <p>Explore a curated catalog and get smart recommendations tailored to what you are searching for.</p>
        <div class="auth-aside-note"><span class="status-dot"></span> Your personal product edit</div>
      </div>
      <div class="surface auth-card">
        <p class="eyebrow">{{ isRegister ? 'JOIN STOCKROOM' : 'WELCOME BACK' }}</p>
        <h2>{{ isRegister ? 'Create your account' : 'Sign in' }}</h2>
        <p class="auth-description">
          {{ isRegister ? 'Save your place and discover products picked for you.' : 'Sign in to continue to your product discovery space.' }}
        </p>
        <form class="auth-form" (ngSubmit)="submit()" #form="ngForm">
          <div class="form-field">
            <label for="account-email">Email address</label>
            <input id="account-email" type="email" name="email" [(ngModel)]="email" autocomplete="email" placeholder="you@example.com" required email />
          </div>
          <div class="form-field">
            <label for="account-password">Password</label>
            <input id="account-password" type="password" name="password" [(ngModel)]="password" [attr.autocomplete]="isRegister ? 'new-password' : 'current-password'" placeholder="At least 8 characters" required minlength="8" />
          </div>
          <p *ngIf="errorMessage" class="form-error" role="alert">{{ errorMessage }}</p>
          <button class="button button-primary auth-submit" type="submit" [disabled]="form.invalid || busy">
            {{ busy ? 'Please wait…' : (isRegister ? 'Create account' : 'Sign in') }}
          </button>
        </form>
        <p class="auth-switch">
          {{ isRegister ? 'Already have an account?' : 'New to Stockroom?' }}
          <a [routerLink]="isRegister ? '/login' : '/register'">{{ isRegister ? 'Sign in' : 'Create an account' }}</a>
        </p>
        <p class="auth-admin-note">Admin accounts are configured by the workspace administrator.</p>
      </div>
    </section>
  `
})
export class AuthComponent implements OnInit {
  email = '';
  password = '';
  errorMessage = '';
  busy = false;
  isRegister = false;

  constructor(
    private auth: AuthService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.isRegister = data['mode'] === 'register';
      this.errorMessage = '';
    });
  }

  submit(): void {
    if (this.busy) return;
    this.busy = true;
    this.errorMessage = '';
    const request = this.isRegister
      ? this.auth.register(this.email, this.password)
      : this.auth.login(this.email, this.password);
    request.subscribe({
      next: () => {
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl');
        const safeReturnUrl = returnUrl?.startsWith('/') && !returnUrl.startsWith('//')
          ? returnUrl
          : '/';
        this.busy = false;
        void this.router.navigateByUrl(safeReturnUrl);
      },
      error: (error: HttpErrorResponse) => {
        this.busy = false;
        this.errorMessage = error.error?.detail
          ?? error.error?.message
          ?? (error.status === 0
            ? 'Could not reach the server. Please try again shortly.'
            : (this.isRegister
              ? 'We could not create your account. Check your details and try again.'
              : 'We could not sign you in. Check your details and try again.'));
      }
    });
  }
}

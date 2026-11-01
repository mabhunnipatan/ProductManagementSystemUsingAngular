import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Inject, Injectable, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { AuthResponse, AccountRole } from '../models/auth';

declare global {
  var __PRODUCT_API_URL__: string | undefined;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'stockroom.auth';
  private readonly authUrl =
    (globalThis.__PRODUCT_API_URL__ ?? 'http://localhost:8080/api/products')
      .replace(/\/api\/products\/?$/, '/api/auth');
  private readonly browser: boolean;
  private readonly userSubject = new BehaviorSubject<AuthResponse | null>(null);

  readonly user$ = this.userSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) platformId: object
  ) {
    this.browser = isPlatformBrowser(platformId);
    if (this.browser) {
      const saved = localStorage.getItem(this.storageKey);
      if (saved) {
        try {
          const user = JSON.parse(saved) as AuthResponse;
          if (typeof user.token === 'string' && typeof user.email === 'string'
            && (user.role === 'USER' || user.role === 'ADMIN')) {
            this.userSubject.next(user);
          } else {
            localStorage.removeItem(this.storageKey);
          }
        } catch {
          localStorage.removeItem(this.storageKey);
        }
      }
    }
  }

  get token(): string | null {
    return this.userSubject.value?.token ?? null;
  }

  get role(): AccountRole | null {
    return this.userSubject.value?.role ?? null;
  }

  get isAuthenticated(): boolean {
    return this.token !== null;
  }

  get isAdmin(): boolean {
    return this.role === 'ADMIN';
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authUrl}/login`, { email, password })
      .pipe(tap(response => this.setSession(response)));
  }

  register(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.authUrl}/register`, { email, password })
      .pipe(tap(response => this.setSession(response)));
  }

  logout(): void {
    if (this.browser) {
      localStorage.removeItem(this.storageKey);
    }
    this.userSubject.next(null);
    void this.router.navigate(['/']);
  }

  private setSession(response: AuthResponse): void {
    if (this.browser) {
      localStorage.setItem(this.storageKey, JSON.stringify(response));
    }
    this.userSubject.next(response);
  }
}

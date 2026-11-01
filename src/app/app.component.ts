import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
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
        <div class="topbar-note">
          <span class="status-dot" aria-hidden="true"></span>
          Your workspace
        </div>
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
}

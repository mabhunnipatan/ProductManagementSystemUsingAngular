import { Routes } from '@angular/router';
import { ProductListComponent } from './components/productlist/productlist.component';
import { ProductFormComponent } from './components/productform/productform.component';
import { AuthComponent } from './components/auth/auth.component';
import { adminGuard } from './services/auth.guard';


export const routes: Routes = [
  { path: '', component: ProductListComponent },
  { path: 'login', component: AuthComponent, data: { mode: 'login' } },
  { path: 'register', component: AuthComponent, data: { mode: 'register' } },
  { path: 'admin', component: ProductListComponent, canActivate: [adminGuard] },
  { path: 'add', component: ProductFormComponent, canActivate: [adminGuard] },
  { path: 'edit/:id', component: ProductFormComponent, canActivate: [adminGuard] },
  { path: '**', redirectTo: '' }
];

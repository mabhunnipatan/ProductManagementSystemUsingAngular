import { Routes } from '@angular/router';
import { ProductListComponent } from './components/productlist/productlist.component';
import { ProductFormComponent } from './components/productform/productform.component';


export const routes: Routes = [
  { path: '', component: ProductListComponent },
  { path: 'add', component: ProductFormComponent },
  { path: 'edit/:id', component: ProductFormComponent },
];

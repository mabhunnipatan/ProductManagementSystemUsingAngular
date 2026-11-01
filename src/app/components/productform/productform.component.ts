import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { Product } from '../../models/product';
import { ProductService } from '../../services/productservice.service';
import { HttpErrorResponse } from '@angular/common/http';
@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './productform.component.html'   // ✅ matches file name
})
export class ProductFormComponent implements OnInit {
  product: Product = { name: '', description: '', price: 0, category: 'Clothing', imageUrl: '' };
  isEdit = false;
  errorMessage = '';
  saving = false;

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    public  router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.productService.getById(+id).subscribe({
        next: product => this.product = { ...product, category: product.category || 'Other', imageUrl: product.imageUrl || '' },
        error: () => this.errorMessage = 'This product could not be loaded. Please return to the catalog and try again.'
      });
    }
  }

  onSubmit(): void {
    if (this.saving) return;
    this.saving = true;
    this.errorMessage = '';
    const save = (this.isEdit && this.product.id !== undefined)
      ? this.productService.update(this.product.id, this.product)
      : this.productService.create(this.product);
    save.subscribe({
      next: () => {
        this.saving = false;
        void this.router.navigate(['/']);
      },
      error: (error: HttpErrorResponse) => {
        this.saving = false;
        this.errorMessage = error.status === 401 || error.status === 403
          ? 'Admin access is required to manage products. Please sign in with an admin account.'
          : 'The product could not be saved. Please review the details and try again.';
      }
    });
  }
}

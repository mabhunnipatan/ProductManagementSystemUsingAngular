import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';

import { Product } from '../../models/product';
import { ProductService } from '../../services/productservice.service';


@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './productlist.component.html'   // ✅ matches file name
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];

  constructor(private productService: ProductService, private router: Router) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts() {
    this.productService.getAll().subscribe(data => this.products = data);
  }

  deleteProduct(id: number) {
    if (confirm("Are you sure to delete this product?")) {
      this.productService.delete(id).subscribe(() => this.loadProducts());
    }
  }

  editProduct(id: number) {
    this.router.navigate(['/edit', id]);
  }
}

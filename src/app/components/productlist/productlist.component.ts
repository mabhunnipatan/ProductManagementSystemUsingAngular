import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';

import { Product } from '../../models/product';
import { ProductService } from '../../services/productservice.service';
import { AuthService } from '../../services/auth.service';


@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './productlist.component.html'   // ✅ matches file name
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  visibleProducts: Product[] = [];
  query = '';
  selectedCategory = 'All';
  assistantMessage = '';
  errorMessage = '';
  loading = false;

  constructor(
    private productService: ProductService,
    private router: Router,
    public auth: AuthService
  ) {}

  get categories(): string[] {
    return [...new Set(this.products.map(product => product.category || 'Other'))].sort();
  }

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.errorMessage = '';
    this.productService.getAll().subscribe({
      next: data => {
        this.products = data;
        this.applyCategory();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Products could not be loaded. Please try again.';
        this.loading = false;
      }
    });
  }

  search(): void {
    const searchTerm = this.query.trim();
    if (!searchTerm) {
      this.clearSearch();
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.selectedCategory = 'All';
    this.productService.getRecommendations(searchTerm).subscribe({
      next: products => {
        this.products = products;
        this.visibleProducts = products;
        this.assistantMessage = products.length
          ? `I found ${products.length} ${products.length === 1 ? 'close match' : 'close matches'} for “${searchTerm}”.`
          : `I couldn't find a close match for “${searchTerm}”. Try a broader search.`;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'The smart finder is temporarily unavailable. Please try again.';
        this.loading = false;
      }
    });
  }

  searchFor(term: string): void {
    this.query = term;
    this.search();
  }

  selectCategory(category: string): void {
    this.selectedCategory = category;
    this.applyCategory();
  }

  clearSearch(): void {
    this.query = '';
    this.assistantMessage = '';
    this.selectedCategory = 'All';
    this.loadProducts();
  }

  private applyCategory(): void {
    this.visibleProducts = this.selectedCategory === 'All'
      ? this.products
      : this.products.filter(product => (product.category || 'Other') === this.selectedCategory);
  }

  deleteProduct(id: number) {
    if (confirm("Are you sure to delete this product?")) {
      this.productService.delete(id).subscribe({
        next: () => this.loadProducts(),
        error: () => this.errorMessage = 'This product could not be removed. Please try again.'
      });
    }
  }

  editProduct(id: number) {
    this.router.navigate(['/edit', id]);
  }
}

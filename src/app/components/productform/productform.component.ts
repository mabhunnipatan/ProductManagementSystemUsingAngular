import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { Product } from '../../models/product';
import { ProductService } from '../../services/productservice.service';
 // ✅ fixed service import

@Component({
  selector: 'app-product-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './productform.component.html'   // ✅ matches file name
})
export class ProductFormComponent implements OnInit {
  product: Product = { name: '', description: '', price: 0 };
  isEdit = false;

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute,
    public  router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEdit = true;
      this.productService.getById(+id).subscribe(p => this.product = p);
    }
  }

  onSubmit() {
    if (this.isEdit) {
      this.productService.update(this.product.id!, this.product)
        .subscribe(() => this.router.navigate(['/']));
    } else {
      this.productService.create(this.product)
        .subscribe(() => this.router.navigate(['/']));
    }
  }
}

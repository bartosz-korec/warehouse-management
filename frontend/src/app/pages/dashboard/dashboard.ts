import { Component, inject } from '@angular/core';
import { OrderResponse } from '../../models/order-response';
import { AuthService } from '../../services/auth.service';
import { OrderService } from '../../services/order.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss',
})
export class Dashboard {
  private readonly orderService = inject(OrderService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  orderResponse?: OrderResponse;
  errorMessage?: string;
  isLoading = false;

  collectOrder(): void {
    this.isLoading = true;
    this.errorMessage = undefined;
    this.orderResponse = undefined;

    this.orderService.generateOrder().subscribe({
      next: (response) => {
        this.orderResponse = response;
        console.log('Order response:', response);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to collect order. Please try again.';
        this.isLoading = false;
      },
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

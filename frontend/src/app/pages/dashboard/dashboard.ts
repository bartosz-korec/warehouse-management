import { Component, inject } from '@angular/core';
import { GridDto, OrderLocationDto, OrderResponse } from '../../models/order-response';
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
  private readonly trackTileValue = 1;

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

  confirmItemCollection(): void {
    if (!this.orderResponse) {
      return;
    }

    const remainingLocations = [...this.orderResponse.locations];

    while (remainingLocations.length && this.isPoint(remainingLocations[0].label)) {
      remainingLocations.shift();
    }

    if (!remainingLocations.length) {
      this.orderResponse = undefined;
      return;
    }

    remainingLocations.shift();

    while (remainingLocations.length && this.isPoint(remainingLocations[0].label)) {
      remainingLocations.shift();
    }

    if (!remainingLocations.length || this.getMajorLocationsFrom(remainingLocations).length < 2) {
      this.orderResponse = undefined;
      return;
    }

    this.orderResponse = {
      ...this.orderResponse,
      locations: remainingLocations,
    };
  }

  get startLocation(): OrderLocationDto | undefined {
    return this.getMajorLocations()[0];
  }

  get destinationLocation(): OrderLocationDto | undefined {
    return this.getMajorLocations()[1];
  }

  get hasRoute(): boolean {
    return this.canConfirmSegment;
  }

  get canConfirmSegment(): boolean {
    return this.getMajorLocations().length >= 2;
  }

  get currentGrid(): GridDto | undefined {
    if (!this.orderResponse?.warehouses.length) {
      return undefined;
    }

    const activeGridId = this.startLocation?.point.gridIndex ?? this.orderResponse.warehouses[0].id;

    return (
      this.orderResponse.warehouses.find((grid) => grid.id === activeGridId) ??
      this.orderResponse.warehouses[0]
    );
  }

  activeSegmentLocationsByGrid(gridId: number): OrderLocationDto[] {
    return this.getActiveSegmentLocations().filter(
      (location) => location.point.gridIndex === gridId
    );
  }

  isTrack(tile: number): boolean {
    return tile === this.trackTileValue;
  }

  getDotClass(location: OrderLocationDto): Record<string, boolean> {
    return {
      'dashboard__dot--start': this.isSameLocation(location, this.startLocation),
      'dashboard__dot--end': this.isSameLocation(location, this.destinationLocation),
      'dashboard__dot--connector': this.isConnector(location.label),
      'dashboard__dot--point': this.isPoint(location.label),
    };
  }

  isLoadingDock(label: string): boolean {
    return label?.startsWith('L');
  }

  isConnector(label: string): boolean {
    return label?.startsWith('C');
  }

  isPoint(label: string): boolean {
    return !this.isLoadingDock(label) && !this.isConnector(label);
  }

  private getActiveSegmentLocations(): OrderLocationDto[] {
    if (!this.orderResponse) {
      return [];
    }

    const segment: OrderLocationDto[] = [];
    let majorCount = 0;

    for (const location of this.orderResponse.locations) {
      segment.push(location);

      if (this.isMajorLocation(location.label)) {
        majorCount++;
        if (majorCount === 2) {
          break;
        }
      }
    }

    return segment;
  }

  private getMajorLocations(): OrderLocationDto[] {
    if (!this.orderResponse) {
      return [];
    }
    return this.getMajorLocationsFrom(this.orderResponse.locations);
  }

  private getMajorLocationsFrom(locations: OrderLocationDto[]): OrderLocationDto[] {
    return locations.filter((location) => this.isMajorLocation(location.label));
  }

  private isMajorLocation(label: string): boolean {
    return this.isLoadingDock(label) || this.isConnector(label);
  }

  private isSameLocation(a?: OrderLocationDto, b?: OrderLocationDto): boolean {
    if (!a || !b) {
      return false;
    }
    return (
      a.label === b.label &&
      a.point.gridIndex === b.point.gridIndex &&
      a.point.x === b.point.x &&
      a.point.y === b.point.y
    );
  }
}

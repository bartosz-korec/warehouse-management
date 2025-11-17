import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderResponse } from '../models/order-response';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private URL = 'http://localhost:8080/api/v1/order';
  private http = inject(HttpClient);

  generateOrder(): Observable<OrderResponse> {
    return this.http.post<OrderResponse>(this.URL + '/generate', {});
  }
}

import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { LoginResponse } from '../models/login-response';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  http = inject(HttpClient);
  router = inject(Router);

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      'http://localhost:8080/api/v1/auth/login',
      { email, password },
      { withCredentials: true }
    );
  }

  logout() {
    localStorage.removeItem('token');
  }
}

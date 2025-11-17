import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatError, MatFormField, MatLabel, MatSuffix } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { Router } from '@angular/router';
import { MatIcon } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [
    MatSlideToggleModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatButton,
    ReactiveFormsModule,
    MatError,
    MatIcon,
    MatSuffix,
  ],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  loginForm: FormGroup = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', Validators.required),
  });

  hidePassword = true;
  badCredential: boolean = false;
  authService = inject(AuthService);
  router = inject(Router);

  login() {
    const { email, password } = this.loginForm.value;
    if (this.loginForm.valid) {
      this.authService.login(email, password).subscribe({
        next: (response) => {
          this.badCredential = false;
          const token = response.token;

          localStorage.setItem('token', token);

          this.router.navigateByUrl('/');
        },
        error: () => {
          this.badCredential = true;
        },
      });
    } else {
      this.loginForm.markAllAsTouched();
    }
  }
}

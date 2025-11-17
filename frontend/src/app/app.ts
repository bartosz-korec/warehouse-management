import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { authInterceptor } from './interceptors/auth-interceptor';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss',
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useFactory: authInterceptor,
    },
  ],
})
export class App {
  protected readonly title = signal('frontend');
}

import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, finalize, throwError } from 'rxjs';
import { ApplicationRef, inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const appRef = inject(ApplicationRef);
  const token = authService.getToken();

  const request = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(request).pipe(
    catchError((error) => {
      if (error.status === 401 && !req.url.endsWith('/auth/login')) {
        authService.logout();
      }
      return throwError(() => error);
    }),
    // Angular 21 se ejecuta sin ZoneJS. Este tick central garantiza que la
    // interfaz refleje inmediatamente las respuestas HTTP y no espere un clic.
    finalize(() => setTimeout(() => appRef.tick(), 0))
  );
};

import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Simple JWT interceptor:
 * - Reads token from localStorage key 'auth_token' (adjust if you store elsewhere)
 * - Adds Authorization: Bearer <token> to outgoing requests (skips if no token)
 * - Does not modify requests for login/refresh endpoints (adjust as needed)
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    try {
      const token = localStorage.getItem('auth_token'); // adjust key to your app
      if (token) {
        const cloned = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`
          }
        });
        return next.handle(cloned);
      }
    } catch (e) {
      // ignore localStorage errors in some browsers/private mode
      console.warn('JwtInterceptor: token read failed', e);
    }
    return next.handle(req);
  }
}
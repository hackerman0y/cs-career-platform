import { HttpClient } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { tap } from 'rxjs';

const apiUrl = 'http://localhost:8080/api';
const storageKey = 'careerpath.session';

type AuthResponse = { token: string; email: string };

@Injectable({ providedIn: 'root' })
export class AuthStore {
  private readonly http = inject(HttpClient);
  private readonly sessionState = signal<AuthResponse | null>(this.readSession());

  readonly session = this.sessionState.asReadonly();

  register(email: string, password: string) {
    return this.http.post<AuthResponse>(`${apiUrl}/auth/register`, { email, password }).pipe(tap((session) => this.saveSession(session)));
  }

  login(email: string, password: string) {
    return this.http.post<AuthResponse>(`${apiUrl}/auth/login`, { email, password }).pipe(tap((session) => this.saveSession(session)));
  }

  token(): string | null { return this.sessionState()?.token ?? null; }

  isAuthenticated(): boolean { return this.token() !== null; }

  logout(): void {
    localStorage.removeItem(storageKey);
    this.sessionState.set(null);
  }

  private saveSession(session: AuthResponse): void {
    localStorage.setItem(storageKey, JSON.stringify(session));
    this.sessionState.set(session);
  }

  private readSession(): AuthResponse | null {
    const savedSession = localStorage.getItem(storageKey);
    if (!savedSession) return null;
    try {
      const session = JSON.parse(savedSession) as Partial<AuthResponse>;
      return typeof session.token === 'string' && typeof session.email === 'string' ? session as AuthResponse : null;
    } catch { return null; }
  }
}

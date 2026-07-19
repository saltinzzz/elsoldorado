import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { Usuario } from './usuario';

export type UserRole = 'ADMIN' | 'EMPLEADO' | 'CLIENTE';
export interface LoginRequest { username: string; password: string; }
export interface LoginResponse { token: string; id: number; username: string; nombreCompleto: string; role: UserRole; telefono?: string; direccion?: string; }
export interface UserSession extends LoginResponse {}
export interface RegistroRequest { nombres:string; apellidos:string; email:string; telefono:string; direccion?:string; password:string; }
export interface PerfilActualizar { nombres:string; apellidos:string; telefono?:string; direccion?:string; }

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly baseUrl = `${environment.apiUrl}/auth`;
  private readonly sessionKey = 'soldorado_session';
  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, credentials).pipe(tap(s => this.saveSession(s)));
  }
  registrar(payload: RegistroRequest): Observable<Usuario> { return this.http.post<Usuario>(`${this.baseUrl}/registro`, payload); }
  perfil(): Observable<Usuario> { return this.http.get<Usuario>(`${this.baseUrl}/me`); }
  actualizarPerfil(payload: PerfilActualizar): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.baseUrl}/me`, payload).pipe(tap(p => this.updateSession(p)));
  }
  cambiarPassword(passwordActual:string,passwordNueva:string):Observable<void>{
    return this.http.patch<void>(`${this.baseUrl}/me/password`,{passwordActual,passwordNueva});
  }
  logout(): void { sessionStorage.removeItem(this.sessionKey); this.router.navigate(['/login']); }
  getSession(): UserSession | null {
    const raw = sessionStorage.getItem(this.sessionKey); if (!raw) return null;
    try { return JSON.parse(raw) as UserSession; } catch { return null; }
  }
  getToken(): string | null { return this.getSession()?.token ?? null; }
  getUserId(): number | null { return this.getSession()?.id ?? null; }
  getUsername(): string { return this.getSession()?.username ?? ''; }
  getDisplayName(): string { return this.getSession()?.nombreCompleto ?? this.getUsername(); }
  getPhone(): string { return this.getSession()?.telefono ?? ''; }
  getAddress(): string { return this.getSession()?.direccion ?? ''; }
  getRole(): UserRole | null { return this.getSession()?.role ?? null; }
  hasRole(...roles: UserRole[]): boolean { const role=this.getRole(); return !!role && roles.includes(role); }
  isAuthenticated(): boolean {
    const token=this.getToken(); if(!token||this.isTokenExpired(token)){if(token)sessionStorage.removeItem(this.sessionKey);return false;} return true;
  }
  updateSession(profile: Usuario): void {
    const session=this.getSession(); if(!session)return;
    this.saveSession({...session,id:profile.id,username:profile.email,nombreCompleto:profile.nombreCompleto,role:profile.rol,telefono:profile.telefono,direccion:profile.direccion});
  }
  private saveSession(session:UserSession):void{sessionStorage.setItem(this.sessionKey,JSON.stringify(session));}
  private isTokenExpired(token:string):boolean{try{const payload=JSON.parse(atob(token.split('.')[1]));return !payload.exp||payload.exp*1000<=Date.now();}catch{return true;}}
}

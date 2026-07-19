import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { UserRole } from './auth.service';

export interface Usuario {
  id: number;
  nombres: string;
  apellidos: string;
  nombreCompleto: string;
  email: string;
  rol: UserRole;
  activo: boolean;
  fechaCreacion: string;
  ultimoAcceso?: string;
  telefono?: string;
  direccion?: string;
  cargo?: string;
  fechaContratacion?: string;
}

export interface UsuarioCrear {
  nombres: string; apellidos: string; email: string; password: string; rol: UserRole;
  telefono?: string; direccion?: string; cargo?: string; fechaContratacion?: string;
}
export interface UsuarioActualizar extends Omit<UsuarioCrear, 'password'> {}

@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly baseUrl = `${environment.apiUrl}/usuarios`;
  constructor(private http: HttpClient) {}
  listar(): Observable<Usuario[]> { return this.http.get<Usuario[]>(this.baseUrl); }
  listarClientes(): Observable<Usuario[]> { return this.http.get<Usuario[]>(`${this.baseUrl}/clientes`); }
  crear(payload: UsuarioCrear): Observable<Usuario> { return this.http.post<Usuario>(this.baseUrl, payload); }
  actualizar(id: number, payload: UsuarioActualizar): Observable<Usuario> { return this.http.put<Usuario>(`${this.baseUrl}/${id}`, payload); }
  cambiarEstado(id: number, activo: boolean): Observable<Usuario> { return this.http.patch<Usuario>(`${this.baseUrl}/${id}/estado`, { activo }); }
  restablecerPassword(id: number, passwordNueva: string): Observable<void> { return this.http.patch<void>(`${this.baseUrl}/${id}/password`, { passwordNueva }); }
  
}


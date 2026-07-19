import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Categoria } from './menu';

@Injectable({ providedIn: 'root' })
export class CategoriaService {
  private readonly baseUrl = `${environment.apiUrl}/categorias`;
  constructor(private http: HttpClient) {}
  listar(): Observable<Categoria[]> { return this.http.get<Categoria[]>(this.baseUrl); }
  crear(nombre: string): Observable<Categoria> { return this.http.post<Categoria>(this.baseUrl, { nombre }); }
  actualizar(id: number, nombre: string): Observable<Categoria> { return this.http.put<Categoria>(`${this.baseUrl}/${id}`, { nombre }); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
}

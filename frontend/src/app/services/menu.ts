import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Categoria { id: number; nombre: string; }
export interface Plato {
  id?: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  disponible: boolean;
  destacado: boolean;
  visibleEnInicio: boolean;
  categoria?: Categoria;
}
export interface PlatoRequest {
  nombre: string;
  descripcion?: string;
  precio: number;
  categoriaId: number;
  disponible: boolean;
  destacado: boolean;
  visibleEnInicio: boolean;
}

@Injectable({ providedIn: 'root' })
export class MenuService {
  private readonly baseUrl = `${environment.apiUrl}/menu`;
  constructor(private http: HttpClient) {}

  obtenerMenuCompleto(): Observable<Plato[]> { return this.http.get<Plato[]>(this.baseUrl); }
  obtenerTodosParaGestion(): Observable<Plato[]> { return this.http.get<Plato[]>(`${this.baseUrl}/gestion`); }
  obtenerPorId(id: number): Observable<Plato> { return this.http.get<Plato>(`${this.baseUrl}/${id}`); }
  obtenerVistaPredefinida(): Observable<Plato[]> { return this.http.get<Plato[]>(`${this.baseUrl}/inicio`); }
  obtenerDestacados(): Observable<Plato[]> { return this.http.get<Plato[]>(`${this.baseUrl}/destacados`); }
  obtenerPorCategoria(nombre: string): Observable<Plato[]> { return this.http.get<Plato[]>(`${this.baseUrl}/categoria/${encodeURIComponent(nombre)}`); }
  buscarPorTexto(texto: string): Observable<Plato[]> { return this.http.get<Plato[]>(`${this.baseUrl}/buscar`, { params: new HttpParams().set('texto', texto) }); }
  buscarPorRangoPrecio(min: number, max: number): Observable<Plato[]> { return this.http.get<Plato[]>(`${this.baseUrl}/precio`, { params: new HttpParams().set('min', min).set('max', max) }); }
  agregarPlato(plato: PlatoRequest): Observable<Plato> { return this.http.post<Plato>(this.baseUrl, plato); }
  actualizarPlato(id: number, plato: PlatoRequest): Observable<Plato> { return this.http.put<Plato>(`${this.baseUrl}/${id}`, plato); }
  eliminarPlato(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
  cambiarDisponibilidad(id: number, disponible: boolean): Observable<Plato> { return this.http.patch<Plato>(`${this.baseUrl}/${id}/disponibilidad`, { disponible }); }
}

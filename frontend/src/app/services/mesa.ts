import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Mesa { id?: number; numero: number; capacidad: number; disponible: boolean; }

@Injectable({ providedIn: 'root' })
export class MesaService {
  private readonly baseUrl = `${environment.apiUrl}/mesas`;
  constructor(private http: HttpClient) {}
  listar(): Observable<Mesa[]> { return this.http.get<Mesa[]>(this.baseUrl); }
  listarDisponibles(): Observable<Mesa[]> { return this.http.get<Mesa[]>(`${this.baseUrl}/disponibles`); }
  crear(mesa: Mesa): Observable<Mesa> { return this.http.post<Mesa>(this.baseUrl, mesa); }
  actualizar(id: number, mesa: Mesa): Observable<Mesa> { return this.http.put<Mesa>(`${this.baseUrl}/${id}`, mesa); }
  cambiarDisponibilidad(id: number, disponible: boolean): Observable<Mesa> { return this.http.patch<Mesa>(`${this.baseUrl}/${id}/disponibilidad`, { disponible }); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
}

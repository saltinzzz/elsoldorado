import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Mesa } from './mesa';

export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'ATENDIDA';
export interface Reserva {
  id?: number; nombreCliente: string; telefono: string; fecha: string; hora: string;
  cantidadPersonas: number; observacion?: string; estado?: EstadoReserva; mesa?: Mesa;
}
export interface ReservaRequest {
  clienteId?: number; nombreCliente?: string; telefono: string; fecha: string; hora: string;
  cantidadPersonas: number; observacion?: string; mesaId?: number | null;
}

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly baseUrl = `${environment.apiUrl}/reservas`;
  constructor(private http: HttpClient) {}
  listar(): Observable<Reserva[]> { return this.http.get<Reserva[]>(this.baseUrl); }
  obtenerPorId(id: number): Observable<Reserva> { return this.http.get<Reserva>(`${this.baseUrl}/${id}`); }
  registrar(reserva: ReservaRequest): Observable<Reserva> { return this.http.post<Reserva>(this.baseUrl, reserva); }
  cambiarEstado(id: number, estado: EstadoReserva): Observable<Reserva> { return this.http.patch<Reserva>(`${this.baseUrl}/${id}/estado`, { estado }); }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
}

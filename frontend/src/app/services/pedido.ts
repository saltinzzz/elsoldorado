import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export type EstadoPedido = 'PENDIENTE' | 'EN_PREPARACION' | 'EN_CAMINO' | 'ENTREGADO' | 'CANCELADO';
export type TipoEntrega = 'DELIVERY' | 'RECOJO_LOCAL';
export type MetodoPago = 'EFECTIVO' | 'TARJETA' | 'TRANSFERENCIA' | 'YAPE' | 'PLIN';
export type EstadoPago = 'PENDIENTE' | 'PAGADO' | 'ANULADO';

export interface DetallePedido {
  id?: number;
  idPlato: number;
  nombrePlato?: string;
  cantidad: number;
  precioUnitario?: number;
  subtotal?: number;
}

export interface Pedido {
  id?: number;
  nombreCliente: string;
  telefono: string;
  tipoEntrega: TipoEntrega;
  direccion?: string;
  distrito?: string;
  referencia?: string;
  latitud?: number;
  longitud?: number;
  horaRecojo?: string;
  observacion?: string;
  metodoPago: MetodoPago;
  estadoPago: EstadoPago;
  fechaPago?: string;
  codigoOperacion?: string;
  detalles: DetallePedido[];
  total?: number;
  fechaHora?: string;
  estado?: EstadoPedido;
}

export interface PedidoRequest {
  clienteId?: number;
  nombreCliente?: string;
  telefono: string;
  tipoEntrega: TipoEntrega;
  direccion?: string;
  distrito?: string;
  referencia?: string;
  latitud?: number;
  longitud?: number;
  horaRecojo?: string;
  observacion?: string;
  metodoPago: MetodoPago;
  detalles: { idPlato: number; cantidad: number }[];
}

@Injectable({ providedIn: 'root' })
export class PedidoService {
  private readonly baseUrl = `${environment.apiUrl}/pedidos`;
  constructor(private http: HttpClient) {}
  listar(): Observable<Pedido[]> { return this.http.get<Pedido[]>(this.baseUrl); }
  obtenerPorId(id: number): Observable<Pedido> { return this.http.get<Pedido>(`${this.baseUrl}/${id}`); }
  registrar(pedido: PedidoRequest): Observable<Pedido> { return this.http.post<Pedido>(this.baseUrl, pedido); }
  cambiarEstado(id: number, estado: EstadoPedido): Observable<Pedido> { return this.http.patch<Pedido>(`${this.baseUrl}/${id}/estado`, { estado }); }
  cambiarEstadoPago(id: number, estado: EstadoPago, codigoOperacion?: string): Observable<Pedido> {
    return this.http.patch<Pedido>(`${this.baseUrl}/${id}/pago`, { estado, codigoOperacion });
  }
  eliminar(id: number): Observable<void> { return this.http.delete<void>(`${this.baseUrl}/${id}`); }
}

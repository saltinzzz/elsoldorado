import { Routes } from '@angular/router';
import { Home } from './components/home/home';
import { Dashboard } from './components/dashboard/dashboard';
import { Login } from './components/login/login';
import { authGuard, roleGuard } from './guards/auth.guard';
import { ReservaForm } from './components/reserva/reserva-form';
import { PedidoForm } from './components/pedido/pedido-form';
import { Registro } from './components/registro/registro';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: Login },
  { path: 'registro', component: Registro },
  { path: 'reservas', component: ReservaForm, canActivate: [roleGuard(['CLIENTE', 'EMPLEADO', 'ADMIN'])] },
  { path: 'pedidos', component: PedidoForm, canActivate: [roleGuard(['CLIENTE', 'EMPLEADO', 'ADMIN'])] },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: '**', redirectTo: '' }
];

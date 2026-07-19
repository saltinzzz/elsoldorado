import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { PedidoService } from './pedido';
describe('PedidoService',()=>{it('should be created',()=>{TestBed.configureTestingModule({providers:[provideHttpClient()]});expect(TestBed.inject(PedidoService)).toBeTruthy();});});

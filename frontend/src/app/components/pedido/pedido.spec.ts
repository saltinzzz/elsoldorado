import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { PedidoForm } from './pedido-form';

describe('PedidoForm', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [PedidoForm], providers: [provideHttpClient()] }).compileComponents();
  });

  it('should create', () => {
    expect(TestBed.createComponent(PedidoForm).componentInstance).toBeTruthy();
  });

  it('inicia sin identificadores inválidos y solo ofrece delivery o recojo', () => {
    const component = TestBed.createComponent(PedidoForm).componentInstance;
    expect(component.form.controls.clienteId.value).toBeNull();
    expect(component.form.controls.tipoEntrega.value).toBe('DELIVERY');
    expect(component.tiposEntrega.map(x => x.value)).toEqual(['DELIVERY', 'RECOJO_LOCAL']);
  });
});

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { ReservaForm } from './reserva-form';

describe('ReservaForm', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [ReservaForm], providers: [provideHttpClient()] }).compileComponents();
  });

  it('should create', () => {
    expect(TestBed.createComponent(ReservaForm).componentInstance).toBeTruthy();
  });

  it('inicia con cliente y mesa sin seleccionar', () => {
    const component = TestBed.createComponent(ReservaForm).componentInstance;
    expect(component.form.controls.clienteId.value).toBeNull();
    expect(component.form.controls.mesaId.value).toBeNull();
  });
});

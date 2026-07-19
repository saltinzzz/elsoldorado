import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { UsuarioService } from './usuario';

describe('UsuarioService', () => {
  it('should be created', () => {
    TestBed.configureTestingModule({ providers: [provideHttpClient()] });
    expect(TestBed.inject(UsuarioService)).toBeTruthy();
  });
});

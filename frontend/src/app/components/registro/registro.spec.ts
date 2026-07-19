import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Registro } from './registro';

describe('Registro', () => {
  it('should create', async () => {
    await TestBed.configureTestingModule({
      imports: [Registro],
      providers: [provideHttpClient(), provideRouter([])]
    }).compileComponents();

    expect(TestBed.createComponent(Registro).componentInstance).toBeTruthy();
  });
});

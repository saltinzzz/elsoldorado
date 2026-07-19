import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { ReservaService } from './reserva';
describe('ReservaService',()=>{it('should be created',()=>{TestBed.configureTestingModule({providers:[provideHttpClient()]});expect(TestBed.inject(ReservaService)).toBeTruthy();});});

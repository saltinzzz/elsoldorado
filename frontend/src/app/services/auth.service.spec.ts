import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
describe('AuthService',()=>{it('should be created',()=>{TestBed.configureTestingModule({providers:[provideHttpClient(),provideRouter([])]});expect(TestBed.inject(AuthService)).toBeTruthy();});});

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Login } from './login';
describe('Login',()=>{it('should create',async()=>{await TestBed.configureTestingModule({imports:[Login],providers:[provideHttpClient(),provideRouter([])]}).compileComponents();expect(TestBed.createComponent(Login).componentInstance).toBeTruthy();});});

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Home } from './home';
describe('Home',()=>{it('should create',async()=>{await TestBed.configureTestingModule({imports:[Home],providers:[provideHttpClient(),provideRouter([])]}).compileComponents();expect(TestBed.createComponent(Home).componentInstance).toBeTruthy();});});

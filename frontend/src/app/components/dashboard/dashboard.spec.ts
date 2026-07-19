import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { Dashboard } from './dashboard';
describe('Dashboard',()=>{it('should create',async()=>{await TestBed.configureTestingModule({imports:[Dashboard],providers:[provideHttpClient(),provideRouter([])]}).compileComponents();expect(TestBed.createComponent(Dashboard).componentInstance).toBeTruthy();});});

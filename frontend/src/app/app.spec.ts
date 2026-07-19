import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { AppComponent } from './app';
describe('AppComponent',()=>{it('should create',async()=>{await TestBed.configureTestingModule({imports:[AppComponent],providers:[provideHttpClient(),provideRouter([])]}).compileComponents();expect(TestBed.createComponent(AppComponent).componentInstance).toBeTruthy();});});

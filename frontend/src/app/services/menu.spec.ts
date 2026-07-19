import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { MenuService } from './menu';
describe('MenuService',()=>{it('should be created',()=>{TestBed.configureTestingModule({providers:[provideHttpClient()]});expect(TestBed.inject(MenuService)).toBeTruthy();});});

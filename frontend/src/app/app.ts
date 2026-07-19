import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({selector:'app-root',standalone:true,imports:[CommonModule,RouterOutlet,RouterLink,RouterLinkActive],templateUrl:'./app.html',styleUrls:['./app.scss']})
export class AppComponent {
  constructor(private auth:AuthService){}
  get authenticated():boolean{return this.auth.isAuthenticated();}
  get username():string{return this.auth.getDisplayName();}
  get role():string{return this.auth.getRole()??'';}
  get isClient():boolean{return this.auth.hasRole('CLIENTE');}
  get isStaff():boolean{return this.auth.hasRole('EMPLEADO','ADMIN');}
  get roleLabel():string{return this.role==='ADMIN'?'Administrador':this.role==='EMPLEADO'?'Empleado':'Cliente';}
  logout():void{this.auth.logout();}
}

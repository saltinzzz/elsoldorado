import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({ selector:'app-login', standalone:true, imports:[CommonModule,ReactiveFormsModule,RouterLink], templateUrl:'./login.html', styleUrls:['./login.scss'] })
export class Login implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  error=''; success=''; loading=false; mostrarPassword=false;
  loginForm=this.fb.group({ username:['',[Validators.required,Validators.email]], password:['',[Validators.required,Validators.minLength(6)]] });
  constructor(private auth:AuthService, private router:Router, private route:ActivatedRoute) {}
  ngOnInit():void{if(this.route.snapshot.queryParamMap.get('registrado')==='1'){this.success='Cuenta creada correctamente. Ya puedes iniciar sesión.';this.loginForm.controls.username.setValue(this.route.snapshot.queryParamMap.get('email')??'');}}
  usarCuenta(username:string):void { this.loginForm.setValue({username,password:''}); }
  onSubmit():void {
    this.error='';
    if(this.loginForm.invalid){ this.loginForm.markAllAsTouched(); this.error='Completa correctamente el correo y la contraseña.'; return; }
    this.loading=true;
    this.auth.login(this.loginForm.getRawValue()).subscribe({
      next:()=>{this.loading=false;this.router.navigate(['/dashboard']);},
      error:e=>{this.loading=false;this.error=e?.error?.message ?? 'No se pudo iniciar sesión.';}
    });
  }
}

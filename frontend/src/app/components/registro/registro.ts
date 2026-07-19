import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({selector:'app-registro',standalone:true,imports:[CommonModule,ReactiveFormsModule,RouterLink],templateUrl:'./registro.html',styleUrls:['./registro.scss']})
export class Registro {
  private readonly fb=inject(NonNullableFormBuilder);
  loading=false;error='';mostrarPassword=false;mostrarConfirmacion=false;
  form=this.fb.group({
    nombres:['',[Validators.required,Validators.maxLength(80)]],
    apellidos:['',[Validators.required,Validators.maxLength(80)]],
    email:['',[Validators.required,Validators.email,Validators.maxLength(150)]],
    telefono:['',[Validators.required,Validators.pattern(/^9\d{8}$/)]],
    direccion:['',Validators.maxLength(200)],
    password:['',[Validators.required,Validators.minLength(8),Validators.maxLength(72),Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],
    confirmacion:['',[Validators.required]]
  });
  constructor(private auth:AuthService,private router:Router){}
  submit():void{
    this.error='';
    if(this.form.invalid){this.form.markAllAsTouched();this.error='Revisa los campos marcados.';return;}
    const v=this.form.getRawValue();
    if(v.password!==v.confirmacion){this.error='Las contraseñas no coinciden.';return;}
    this.loading=true;
    this.auth.registrar({nombres:v.nombres,apellidos:v.apellidos,email:v.email,telefono:v.telefono,direccion:v.direccion||undefined,password:v.password}).subscribe({
      next:()=>{this.loading=false;this.router.navigate(['/login'],{queryParams:{registrado:'1',email:v.email}});},
      error:e=>{this.loading=false;this.error=e?.error?.message??'No se pudo crear la cuenta.';}
    });
  }
}

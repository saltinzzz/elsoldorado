import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ReservaService, ReservaRequest } from '../../services/reserva';
import { Mesa, MesaService } from '../../services/mesa';
import { AuthService } from '../../services/auth.service';
import { Usuario, UsuarioService } from '../../services/usuario';

@Component({ selector:'app-reserva-form', standalone:true, imports:[CommonModule,ReactiveFormsModule], templateUrl:'./reserva.html', styleUrls:['./reserva.scss'] })
export class ReservaForm implements OnInit {
  private readonly fb=inject(FormBuilder);
  private readonly cdr=inject(ChangeDetectorRef);
  mesas:Mesa[]=[];clientes:Usuario[]=[];loading=false;loadingData=true;error='';success='';minDate=new Date().toISOString().slice(0,10);
  form=this.fb.group({
    clienteId:this.fb.control<number|null>(null),
    nombreCliente:['',Validators.maxLength(100)],
    telefono:['',[Validators.required,Validators.pattern(/^9\d{8}$/)]],
    fecha:['',Validators.required],hora:['',Validators.required],
    cantidadPersonas:[2,[Validators.required,Validators.min(1),Validators.max(20)]],
    mesaId:this.fb.control<number|null>(null),observacion:['',Validators.maxLength(500)]
  });
  constructor(private reservas:ReservaService,private mesasService:MesaService,public auth:AuthService,private usuarios:UsuarioService){}
  ngOnInit():void{
    this.mesasService.listarDisponibles().pipe(finalize(()=>{this.loadingData=false;this.cdr.markForCheck();})).subscribe({next:x=>{this.mesas=x;this.cdr.markForCheck();},error:()=>{this.error='No se pudieron cargar las mesas disponibles.';this.cdr.markForCheck();}});
    if(this.auth.hasRole('CLIENTE'))this.form.patchValue({telefono:this.auth.getPhone()});
    else this.usuarios.listarClientes().subscribe({next:x=>{this.clientes=x;this.cdr.markForCheck();},error:()=>{this.error='No se pudo cargar la lista de clientes.';this.cdr.markForCheck();}});
  }
  seleccionarCliente():void{const id=this.form.controls.clienteId.value;const c=this.clientes.find(x=>x.id===id);if(c)this.form.patchValue({nombreCliente:c.nombreCompleto,telefono:c.telefono??''});else this.form.patchValue({nombreCliente:'',telefono:''});}
  submit():void{
    this.error='';this.success='';const v=this.form.getRawValue();
    if(!this.auth.hasRole('CLIENTE')&&!v.clienteId&&!v.nombreCliente?.trim()){this.error='Selecciona un cliente registrado o ingresa el nombre de un cliente ocasional.';return;}
    if(this.form.invalid){this.form.markAllAsTouched();this.error='Revisa los campos marcados antes de continuar.';return;}
    const date=new Date(`${v.fecha}T${v.hora}`);if(date.getTime()<=Date.now()){this.error='La fecha y hora deben ser futuras.';return;}if((v.hora??'')<'12:00'||(v.hora??'')>'21:00'){this.error='El horario disponible para reservas es de 12:00 a 21:00.';return;}
    const request:ReservaRequest={clienteId:!this.auth.hasRole('CLIENTE')&&v.clienteId?v.clienteId:undefined,nombreCliente:this.auth.hasRole('CLIENTE')?undefined:v.nombreCliente?.trim(),telefono:v.telefono??'',fecha:v.fecha??'',hora:v.hora??'',cantidadPersonas:Number(v.cantidadPersonas),observacion:v.observacion?.trim()||undefined,mesaId:v.mesaId};
    this.loading=true;this.reservas.registrar(request).pipe(finalize(()=>{this.loading=false;this.cdr.markForCheck();})).subscribe({next:r=>{this.success=`Reserva #${r.id} registrada correctamente. Mesa asignada: ${r.mesa?.numero??'por confirmar'}.`;this.form.reset({clienteId:null,nombreCliente:'',telefono:this.auth.hasRole('CLIENTE')?this.auth.getPhone():'',fecha:'',hora:'',cantidadPersonas:2,mesaId:null,observacion:''});this.cdr.markForCheck();window.scrollTo({top:0,behavior:'smooth'});},error:e=>{this.error=e?.error?.message??'No se pudo registrar la reserva.';this.cdr.markForCheck();}});
  }
}

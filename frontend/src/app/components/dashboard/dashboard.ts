import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { AuthService, UserRole } from '../../services/auth.service';
import { MenuService, Plato, PlatoRequest, Categoria } from '../../services/menu';
import { CategoriaService } from '../../services/categoria';
import { Mesa, MesaService } from '../../services/mesa';
import { EstadoPago, EstadoPedido, Pedido, PedidoService } from '../../services/pedido';
import { EstadoReserva, Reserva, ReservaService } from '../../services/reserva';
import { Usuario, UsuarioActualizar, UsuarioCrear, UsuarioService } from '../../services/usuario';

@Component({selector:'app-dashboard',standalone:true,imports:[CommonModule,ReactiveFormsModule,RouterLink],templateUrl:'./dashboard.html',styleUrls:['./dashboard.scss']})
export class Dashboard implements OnInit {
 private readonly fb=inject(NonNullableFormBuilder);
 private readonly cdr=inject(ChangeDetectorRef);
 section='resumen';error='';success='';loading=true;
 searchPedido='';searchReserva='';searchUsuario='';pedidoEstadoFilter='';reservaEstadoFilter='';
 platos:Plato[]=[];categorias:Categoria[]=[];mesas:Mesa[]=[];pedidos:Pedido[]=[];reservas:Reserva[]=[];usuarios:Usuario[]=[];perfil?:Usuario;
 roles:UserRole[]=['CLIENTE','EMPLEADO','ADMIN'];
 estadosPedido:EstadoPedido[]=['PENDIENTE','EN_PREPARACION','EN_CAMINO','ENTREGADO','CANCELADO'];
 estadosPago:EstadoPago[]=['PENDIENTE','PAGADO','ANULADO'];
 estadosReserva:EstadoReserva[]=['PENDIENTE','CONFIRMADA','ATENDIDA','CANCELADA'];
 platoForm=this.fb.group({id:[0],nombre:['',[Validators.required,Validators.maxLength(100)]],descripcion:['',Validators.maxLength(1000)],precio:[0,[Validators.required,Validators.min(.01)]],categoriaId:[0,[Validators.required,Validators.min(1)]],disponible:[true],destacado:[false],visibleEnInicio:[false]});
 categoriaForm=this.fb.group({id:[0],nombre:['',[Validators.required,Validators.maxLength(100)]]});
 mesaForm=this.fb.group({id:[0],numero:[1,[Validators.required,Validators.min(1)]],capacidad:[2,[Validators.required,Validators.min(1),Validators.max(20)]],disponible:[true]});
 usuarioForm=this.fb.group({id:[0],nombres:['',[Validators.required,Validators.maxLength(80)]],apellidos:['',[Validators.required,Validators.maxLength(80)]],email:['',[Validators.required,Validators.email]],password:[''],rol:['CLIENTE' as UserRole,Validators.required],telefono:[''],direccion:['',Validators.maxLength(200)],cargo:['',Validators.maxLength(80)],fechaContratacion:['']});
 perfilForm=this.fb.group({nombres:['',[Validators.required,Validators.maxLength(80)]],apellidos:['',[Validators.required,Validators.maxLength(80)]],telefono:[''],direccion:['',Validators.maxLength(200)]});
 passwordForm=this.fb.group({passwordActual:['',Validators.required],passwordNueva:['',[Validators.required,Validators.minLength(8),Validators.pattern(/^(?=.*[A-Za-z])(?=.*\d).+$/)]],confirmacion:['',Validators.required]});
 constructor(public auth:AuthService,private menu:MenuService,private categoriasService:CategoriaService,private mesasService:MesaService,private pedidosService:PedidoService,private reservasService:ReservaService,private usuariosService:UsuarioService){}
 get isAdmin():boolean{return this.auth.hasRole('ADMIN')} get isStaff():boolean{return this.auth.hasRole('ADMIN','EMPLEADO')} get isClient():boolean{return this.auth.hasRole('CLIENTE')}
 get pedidosPendientes():number{return this.pedidos.filter(p=>p.estado==='PENDIENTE'||p.estado==='EN_PREPARACION').length} get reservasPendientes():number{return this.reservas.filter(r=>r.estado==='PENDIENTE').length}
 get usuariosActivos():number{return this.usuarios.filter(u=>u.activo).length}
 get mesasDisponibles():number{return this.mesas.filter(m=>m.disponible).length}
 get ventasRegistradas():number{return this.pedidos.filter(p=>p.estadoPago==='PAGADO').reduce((s,p)=>s+Number(p.total??0),0)}
 get pedidosHoy():number{const hoy=this.fechaLocal(new Date());return this.pedidos.filter(p=>(p.fechaHora??'').startsWith(hoy)).length}
 get pedidosFiltrados():Pedido[]{const t=this.searchPedido.trim().toLowerCase();return this.pedidos.filter(p=>(!t||`${p.nombreCliente} ${p.telefono} ${p.id??''}`.toLowerCase().includes(t))&&(!this.pedidoEstadoFilter||p.estado===this.pedidoEstadoFilter))}
 get reservasFiltradas():Reserva[]{const t=this.searchReserva.trim().toLowerCase();return this.reservas.filter(r=>(!t||`${r.nombreCliente} ${r.telefono} ${r.id??''}`.toLowerCase().includes(t))&&(!this.reservaEstadoFilter||r.estado===this.reservaEstadoFilter))}
 get usuariosFiltrados():Usuario[]{const t=this.searchUsuario.trim().toLowerCase();return this.usuarios.filter(u=>!t||`${u.nombreCompleto} ${u.email} ${u.rol}`.toLowerCase().includes(t))}
 get ultimosPedidos():Pedido[]{return this.pedidos.slice(0,5)}
 get proximasReservas():Reserva[]{const hoy=this.fechaLocal(new Date());return this.reservas.filter(r=>r.fecha>=hoy&&r.estado!=='CANCELADA'&&r.estado!=='ATENDIDA').sort((a,b)=>`${a.fecha}T${a.hora}`.localeCompare(`${b.fecha}T${b.hora}`)).slice(0,5)}
 ngOnInit():void{this.reload();this.cargarPerfil();}
 reload():void{
   this.loading=true;this.error='';
   forkJoin({pedidos:this.pedidosService.listar(),reservas:this.reservasService.listar()}).subscribe({next:x=>{this.pedidos=x.pedidos;this.reservas=x.reservas;this.loading=false;},error:e=>{this.loading=false;this.error=this.msg(e,'No se pudo cargar el panel.');}});
   if(this.isStaff)this.cargarMesas();if(this.isAdmin){this.cargarPlatos();this.cargarCategorias();this.cargarUsuarios();}
 }
 cargarPerfil():void{this.auth.perfil().subscribe({next:p=>{this.perfil=p;this.auth.updateSession(p);this.perfilForm.setValue({nombres:p.nombres,apellidos:p.apellidos,telefono:p.telefono??'',direccion:p.direccion??''});},error:e=>this.error=this.msg(e,'No se pudo cargar tu perfil.')})}
 cargarUsuarios():void{this.usuariosService.listar().subscribe({next:x=>this.usuarios=x,error:e=>this.error=this.msg(e,'No se pudieron cargar los usuarios.')})}
 cargarPlatos():void{this.menu.obtenerTodosParaGestion().subscribe({next:x=>this.platos=x,error:e=>this.error=this.msg(e,'No se pudo cargar el menú.')})}
 cargarCategorias():void{this.categoriasService.listar().subscribe({next:x=>this.categorias=x,error:e=>this.error=this.msg(e,'No se pudieron cargar las categorías.')})}
 cargarMesas():void{this.mesasService.listar().subscribe({next:x=>this.mesas=x,error:e=>this.error=this.msg(e,'No se pudieron cargar las mesas.')})}
 notice(text:string):void{this.success=text;this.error='';this.cdr.markForCheck();setTimeout(()=>{this.success='';this.cdr.markForCheck();},3500)}
 saveUsuario():void{
   this.error='';const v=this.usuarioForm.getRawValue();
   if(this.usuarioForm.invalid){this.usuarioForm.markAllAsTouched();this.error='Revisa los datos del usuario.';return;}
   if(!v.id&&(!v.password||v.password.length<8||!/[A-Za-z]/.test(v.password)||!/\d/.test(v.password))){this.error='La contraseña inicial debe tener al menos 8 caracteres, con letras y números.';return;}
   if(v.rol==='CLIENTE'&&!/^9\d{8}$/.test(v.telefono)){this.error='El cliente debe tener un teléfono válido.';return;}
   if(v.rol==='EMPLEADO'&&!v.cargo.trim()){this.error='Ingresa el cargo del empleado.';return;}
   const base={nombres:v.nombres.trim(),apellidos:v.apellidos.trim(),email:v.email.trim(),rol:v.rol,telefono:v.telefono||undefined,direccion:v.direccion||undefined,cargo:v.cargo||undefined,fechaContratacion:v.fechaContratacion||undefined};
   const action=v.id?this.usuariosService.actualizar(v.id,base as UsuarioActualizar):this.usuariosService.crear({...base,password:v.password} as UsuarioCrear);
   action.subscribe({next:()=>{this.notice(v.id?'Usuario actualizado.':'Usuario creado.');this.resetUsuario();this.cargarUsuarios();},error:e=>this.error=this.msg(e,'No se pudo guardar el usuario.')});
 }
 editUsuario(u:Usuario):void{this.section='usuarios';this.usuarioForm.setValue({id:u.id,nombres:u.nombres,apellidos:u.apellidos,email:u.email,password:'',rol:u.rol,telefono:u.telefono??'',direccion:u.direccion??'',cargo:u.cargo??'',fechaContratacion:u.fechaContratacion??''})}
 resetUsuario():void{this.usuarioForm.reset({id:0,nombres:'',apellidos:'',email:'',password:'',rol:'CLIENTE',telefono:'',direccion:'',cargo:'',fechaContratacion:''})}
 toggleUsuario(u:Usuario):void{this.usuariosService.cambiarEstado(u.id,!u.activo).subscribe({next:x=>{u.activo=x.activo;this.notice(x.activo?'Usuario activado.':'Usuario desactivado.');},error:e=>this.error=this.msg(e,'No se pudo cambiar el estado.')})}
 resetPassword(u:Usuario):void{const value=prompt(`Nueva contraseña para ${u.nombreCompleto}:`);if(!value)return;if(value.length<8||!/[A-Za-z]/.test(value)||!/\d/.test(value)){this.error='La contraseña debe tener al menos 8 caracteres, con letras y números.';return;}this.usuariosService.restablecerPassword(u.id,value).subscribe({next:()=>this.notice('Contraseña restablecida.'),error:e=>this.error=this.msg(e,'No se pudo restablecer la contraseña.')})}
 savePerfil():void{if(this.perfilForm.invalid){this.perfilForm.markAllAsTouched();return;}this.auth.actualizarPerfil(this.perfilForm.getRawValue()).subscribe({next:p=>{this.perfil=p;this.notice('Perfil actualizado.');},error:e=>this.error=this.msg(e,'No se pudo actualizar el perfil.')})}
 savePassword():void{const v=this.passwordForm.getRawValue();if(this.passwordForm.invalid){this.passwordForm.markAllAsTouched();return;}if(v.passwordNueva!==v.confirmacion){this.error='Las contraseñas nuevas no coinciden.';return;}this.auth.cambiarPassword(v.passwordActual,v.passwordNueva).subscribe({next:()=>{this.passwordForm.reset({passwordActual:'',passwordNueva:'',confirmacion:''});this.notice('Contraseña actualizada.');},error:e=>this.error=this.msg(e,'No se pudo cambiar la contraseña.')})}
 savePlato():void{if(this.platoForm.invalid){this.platoForm.markAllAsTouched();this.error='Completa correctamente el formulario del plato.';return;}const v=this.platoForm.getRawValue();const payload:PlatoRequest={nombre:v.nombre,descripcion:v.descripcion||undefined,precio:v.precio,categoriaId:v.categoriaId,disponible:v.disponible,destacado:v.destacado,visibleEnInicio:v.visibleEnInicio};const action=v.id?this.menu.actualizarPlato(v.id,payload):this.menu.agregarPlato(payload);action.subscribe({next:()=>{this.notice(v.id?'Plato actualizado.':'Plato creado.');this.resetPlato();this.cargarPlatos()},error:e=>this.error=this.msg(e,'No se pudo guardar el plato.')})}
 editPlato(p:Plato):void{this.section='menu';this.platoForm.setValue({id:p.id??0,nombre:p.nombre,descripcion:p.descripcion??'',precio:Number(p.precio),categoriaId:p.categoria?.id??0,disponible:p.disponible,destacado:p.destacado,visibleEnInicio:p.visibleEnInicio})}
 resetPlato():void{this.platoForm.reset({id:0,nombre:'',descripcion:'',precio:0,categoriaId:0,disponible:true,destacado:false,visibleEnInicio:false})}
 togglePlato(p:Plato):void{this.menu.cambiarDisponibilidad(p.id!,!p.disponible).subscribe({next:()=>this.cargarPlatos(),error:e=>this.error=this.msg(e,'No se pudo cambiar la disponibilidad.')})}
 deletePlato(p:Plato):void{if(confirm(`¿Eliminar ${p.nombre}?`))this.menu.eliminarPlato(p.id!).subscribe({next:()=>{this.notice('Plato eliminado.');this.cargarPlatos()},error:e=>this.error=this.msg(e,'No se pudo eliminar.')})}
 saveCategoria():void{if(this.categoriaForm.invalid){this.categoriaForm.markAllAsTouched();return;}const v=this.categoriaForm.getRawValue();const action=v.id?this.categoriasService.actualizar(v.id,v.nombre):this.categoriasService.crear(v.nombre);action.subscribe({next:()=>{this.notice(v.id?'Categoría actualizada.':'Categoría creada.');this.categoriaForm.reset({id:0,nombre:''});this.cargarCategorias()},error:e=>this.error=this.msg(e,'No se pudo guardar la categoría.')})}
 editCategoria(c:Categoria):void{this.categoriaForm.setValue({id:c.id,nombre:c.nombre})}
 deleteCategoria(c:Categoria):void{if(confirm(`¿Eliminar la categoría ${c.nombre}?`))this.categoriasService.eliminar(c.id).subscribe({next:()=>{this.notice('Categoría eliminada.');this.cargarCategorias()},error:e=>this.error=this.msg(e,'No puede eliminarse porque puede tener platos relacionados.')})}
 saveMesa():void{if(this.mesaForm.invalid){this.mesaForm.markAllAsTouched();return;}const v=this.mesaForm.getRawValue();const payload:Mesa={numero:v.numero,capacidad:v.capacidad,disponible:v.disponible};const action=v.id?this.mesasService.actualizar(v.id,payload):this.mesasService.crear(payload);action.subscribe({next:()=>{this.notice(v.id?'Mesa actualizada.':'Mesa creada.');this.mesaForm.reset({id:0,numero:1,capacidad:2,disponible:true});this.cargarMesas()},error:e=>this.error=this.msg(e,'No se pudo guardar la mesa.')})}
 editMesa(m:Mesa):void{this.mesaForm.setValue({id:m.id??0,numero:m.numero,capacidad:m.capacidad,disponible:m.disponible})}
 toggleMesa(m:Mesa):void{this.mesasService.cambiarDisponibilidad(m.id!,!m.disponible).subscribe({next:()=>this.cargarMesas(),error:e=>this.error=this.msg(e,'No se pudo actualizar la mesa.')})}
 deleteMesa(m:Mesa):void{if(confirm(`¿Eliminar la mesa ${m.numero}?`))this.mesasService.eliminar(m.id!).subscribe({next:()=>{this.notice('Mesa eliminada.');this.cargarMesas()},error:e=>this.error=this.msg(e,'No se pudo eliminar la mesa.')})}
 cambiarPedido(p:Pedido,estado:string):void{this.pedidosService.cambiarEstado(p.id!,estado as EstadoPedido).subscribe({next:x=>{p.estado=x.estado;p.estadoPago=x.estadoPago;this.notice('Estado del pedido actualizado.')},error:e=>this.error=this.msg(e,'No se pudo actualizar el pedido.')})}
 cambiarPago(p:Pedido,estado:string):void{let codigo=p.codigoOperacion;if(estado==='PAGADO'&&p.metodoPago!=='EFECTIVO'){const valor=prompt('Código de operación (opcional):',codigo??'');if(valor===null)return;codigo=valor||undefined;}this.pedidosService.cambiarEstadoPago(p.id!,estado as EstadoPago,codigo).subscribe({next:x=>{p.estadoPago=x.estadoPago;p.fechaPago=x.fechaPago;p.codigoOperacion=x.codigoOperacion;this.notice('Estado de pago actualizado.')},error:e=>this.error=this.msg(e,'No se pudo actualizar el pago.')})}
 abrirMapa(p:Pedido):void{if(p.latitud!=null&&p.longitud!=null)window.open(`https://www.openstreetmap.org/?mlat=${p.latitud}&mlon=${p.longitud}#map=17/${p.latitud}/${p.longitud}`,'_blank','noopener');}
 tipoEntregaLabel(p:Pedido):string{return p.tipoEntrega==='RECOJO_LOCAL'?'Recojo en local':'Delivery'}
 metodoPagoLabel(valor:string):string{return valor==='TRANSFERENCIA'?'Transferencia':valor.charAt(0)+valor.slice(1).toLowerCase()}
 cambiarReserva(r:Reserva,estado:string):void{this.reservasService.cambiarEstado(r.id!,estado as EstadoReserva).subscribe({next:x=>{r.estado=x.estado;this.notice('Estado de la reserva actualizado.')},error:e=>this.error=this.msg(e,'No se pudo actualizar la reserva.')})}
 roleLabel(role:UserRole):string{return role==='ADMIN'?'Administrador':role==='EMPLEADO'?'Empleado':'Cliente'}
 private fechaLocal(fecha:Date):string{const y=fecha.getFullYear();const m=String(fecha.getMonth()+1).padStart(2,'0');const d=String(fecha.getDate()).padStart(2,'0');return `${y}-${m}-${d}`}
 private msg(e:any,fallback:string):string{return e?.error?.message??fallback}
}

export interface LoginRequest { username: string; password: string; }
export interface LoginResponse { token:string; id:number; username:string; nombreCompleto:string; role:'ADMIN'|'EMPLEADO'|'CLIENTE'; telefono?:string; direccion?:string; }

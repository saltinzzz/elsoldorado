import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { MenuService, Plato, Categoria } from '../../services/menu';
import { CategoriaService } from '../../services/categoria';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-home', standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './home.html', styleUrls: ['./home.scss']
})
export class Home implements OnInit {
  private readonly cdr=inject(ChangeDetectorRef);
  platos: Plato[] = []; categorias: Categoria[] = []; cargando = true; error = '';
  texto = ''; categoria = '';
  constructor(private menuService: MenuService, private categoriaService: CategoriaService, public auth:AuthService) {}
  ngOnInit(): void {
    this.categoriaService.listar().subscribe({ next: x => {this.categorias = x;this.cdr.markForCheck();},error:()=>this.cdr.markForCheck() });
    this.cargarMenu();
  }
  cargarMenu(): void {
    this.cargando = true; this.error = '';
    this.menuService.obtenerMenuCompleto().pipe(finalize(()=>{this.cargando=false;this.cdr.markForCheck();})).subscribe({
      next: data => { this.platos = data; this.cdr.markForCheck(); },
      error: () => { this.error = 'No se pudo conectar con el servidor. Verifica que Spring Boot esté iniciado.'; this.cdr.markForCheck(); }
    });
  }
  get platosFiltrados(): Plato[] {
    const term = this.texto.trim().toLowerCase();
    return this.platos.filter(p => (!term || `${p.nombre} ${p.descripcion ?? ''}`.toLowerCase().includes(term)) && (!this.categoria || p.categoria?.nombre === this.categoria));
  }
  get destacados():Plato[]{const selected=this.platos.filter(p=>p.destacado||p.visibleEnInicio);return (selected.length?selected:this.platos).slice(0,3);}
  icono(plato:Plato):string{const c=plato.categoria?.nombre?.toLowerCase()??'';return c.includes('bebida')?'🥤':c.includes('postre')?'🍮':c.includes('entrada')?'🥗':'🍛';}
}

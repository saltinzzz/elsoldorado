import { AfterViewInit, ChangeDetectorRef, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { MetodoPago, PedidoRequest, PedidoService, TipoEntrega } from '../../services/pedido';
import { MenuService, Plato } from '../../services/menu';
import { AuthService } from '../../services/auth.service';
import { Usuario, UsuarioService } from '../../services/usuario';

declare const L: any;

@Component({
  selector: 'app-pedido-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './pedido.html',
  styleUrls: ['./pedido.scss']
})
export class PedidoForm implements OnInit, AfterViewInit, OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);
  platos: Plato[] = [];
  clientes: Usuario[] = [];
  loading = false;
  loadingMenu = true;
  locating = false;
  searchingAddress = false;
  error = '';
  success = '';
  mapReady = false;
  mapError = '';
  private map?: any;
  private marker?: any;

  readonly tiposEntrega: { value: TipoEntrega; label: string; detail: string }[] = [
    { value: 'DELIVERY', label: 'Delivery', detail: 'Llevamos el pedido a la dirección indicada.' },
    { value: 'RECOJO_LOCAL', label: 'Recojo en local', detail: 'Lo dejamos listo para la hora seleccionada.' }
  ];
  readonly metodosPago: { value: MetodoPago; label: string }[] = [
    { value: 'EFECTIVO', label: 'Efectivo' },
    { value: 'TARJETA', label: 'Tarjeta al recibir' },
    { value: 'TRANSFERENCIA', label: 'Transferencia' },
    { value: 'YAPE', label: 'Yape' },
    { value: 'PLIN', label: 'Plin' }
  ];

  form = this.fb.group({
    clienteId: this.fb.control<number | null>(null),
    nombreCliente: ['', Validators.maxLength(100)],
    telefono: ['', [Validators.required, Validators.pattern(/^9\d{8}$/)]],
    tipoEntrega: this.fb.control<TipoEntrega>('DELIVERY', { nonNullable: true }),
    direccion: ['', Validators.maxLength(200)],
    distrito: ['', Validators.maxLength(80)],
    referencia: ['', Validators.maxLength(200)],
    latitud: this.fb.control<number | null>(null),
    longitud: this.fb.control<number | null>(null),
    horaRecojo: [''],
    observacion: ['', Validators.maxLength(500)],
    metodoPago: this.fb.control<MetodoPago>('EFECTIVO', { nonNullable: true }),
    detalles: this.fb.array<FormGroup>([])
  });

  constructor(
    private pedidos: PedidoService,
    private menu: MenuService,
    public auth: AuthService,
    private usuarios: UsuarioService
  ) {}

  ngOnInit(): void {
    this.menu.obtenerMenuCompleto().pipe(finalize(() => {
      this.loadingMenu = false;
      this.cdr.markForCheck();
    })).subscribe({
      next: x => { this.platos = x.filter(p => p.disponible); this.cdr.markForCheck(); },
      error: () => { this.error = 'No se pudo cargar la carta. Verifica que el backend esté iniciado.'; this.cdr.markForCheck(); }
    });

    if (this.auth.hasRole('CLIENTE')) {
      this.form.patchValue({ telefono: this.auth.getPhone(), direccion: this.auth.getAddress() });
    } else {
      this.usuarios.listarClientes().subscribe({
        next: x => { this.clientes = x; this.cdr.markForCheck(); },
        error: () => { this.error = 'No se pudo cargar la lista de clientes.'; this.cdr.markForCheck(); }
      });
    }

    this.form.controls.tipoEntrega.valueChanges.subscribe(tipo => {
      this.error = '';
      if (tipo === 'DELIVERY') {
        setTimeout(() => this.initMap(), 0);
      } else {
        this.destroyMap();
      }
      this.cdr.markForCheck();
    });
  }

  ngAfterViewInit(): void { setTimeout(() => this.initMap(), 0); }
  ngOnDestroy(): void { this.destroyMap(); }

  get detalles(): FormArray<FormGroup> { return this.form.controls.detalles; }
  get isDelivery(): boolean { return this.form.controls.tipoEntrega.value === 'DELIVERY'; }

  seleccionarCliente(): void {
    const id = this.form.controls.clienteId.value;
    const cliente = this.clientes.find(x => x.id === id);
    if (cliente) {
      this.form.patchValue({ nombreCliente: cliente.nombreCompleto, telefono: cliente.telefono ?? '', direccion: cliente.direccion ?? '' });
    } else {
      this.form.patchValue({ nombreCliente: '', telefono: '', direccion: '' });
    }
  }

  agregar(plato: Plato): void {
    const actual = this.detalles.controls.find(c => Number(c.controls['idPlato'].value) === plato.id);
    if (actual) {
      const cantidad = actual.controls['cantidad'];
      cantidad.setValue(Math.min(Number(cantidad.value) + 1, 20));
      return;
    }
    this.detalles.push(new FormGroup({
      idPlato: new FormControl(plato.id, { nonNullable: true }),
      nombre: new FormControl(plato.nombre, { nonNullable: true }),
      precio: new FormControl(plato.precio, { nonNullable: true }),
      cantidad: new FormControl(1, { nonNullable: true, validators: [Validators.min(1), Validators.max(20)] })
    }));
  }

  quitar(i: number): void { this.detalles.removeAt(i); }
  total(): number { return this.detalles.controls.reduce((s, c) => s + Number(c.controls['precio'].value) * Number(c.controls['cantidad'].value), 0); }

  submit(): void {
    this.error = '';
    this.success = '';
    const v = this.form.getRawValue();

    if (!this.auth.hasRole('CLIENTE') && !v.clienteId && !v.nombreCliente?.trim()) {
      this.error = 'Selecciona un cliente registrado o ingresa el nombre de un cliente ocasional.';
      return;
    }
    if (this.form.invalid || !this.detalles.length) {
      this.form.markAllAsTouched();
      this.error = !this.detalles.length ? 'Agrega al menos un plato al pedido.' : 'Revisa los datos ingresados.';
      return;
    }
    if (v.tipoEntrega === 'DELIVERY' && (!v.direccion?.trim() || v.direccion.trim().length < 5 || !v.distrito?.trim())) {
      this.error = 'Para delivery, completa la dirección y el distrito.';
      return;
    }
    if (v.tipoEntrega === 'RECOJO_LOCAL' && !v.horaRecojo) {
      this.error = 'Selecciona la hora aproximada de recojo.';
      return;
    }

    const request: PedidoRequest = {
      clienteId: !this.auth.hasRole('CLIENTE') && v.clienteId ? v.clienteId : undefined,
      nombreCliente: this.auth.hasRole('CLIENTE') ? undefined : v.nombreCliente?.trim(),
      telefono: v.telefono ?? '',
      tipoEntrega: v.tipoEntrega,
      direccion: v.tipoEntrega === 'DELIVERY' ? v.direccion?.trim() : undefined,
      distrito: v.tipoEntrega === 'DELIVERY' ? v.distrito?.trim() : undefined,
      referencia: v.tipoEntrega === 'DELIVERY' ? v.referencia?.trim() || undefined : undefined,
      latitud: v.tipoEntrega === 'DELIVERY' ? v.latitud ?? undefined : undefined,
      longitud: v.tipoEntrega === 'DELIVERY' ? v.longitud ?? undefined : undefined,
      horaRecojo: v.tipoEntrega === 'RECOJO_LOCAL' ? v.horaRecojo || undefined : undefined,
      observacion: v.observacion?.trim() || undefined,
      metodoPago: v.metodoPago,
      detalles: v.detalles.map((d: any) => ({ idPlato: Number(d.idPlato), cantidad: Number(d.cantidad) }))
    };

    this.loading = true;
    this.pedidos.registrar(request).pipe(finalize(() => {
      this.loading = false;
      this.cdr.markForCheck();
    })).subscribe({
      next: p => {
        this.success = `Pedido #${p.id} registrado correctamente por S/ ${Number(p.total).toFixed(2)}.`;
        this.resetForm();
        this.cdr.markForCheck();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      },
      error: e => { this.error = e?.error?.message ?? 'No se pudo registrar el pedido.'; this.cdr.markForCheck(); }
    });
  }

  usarUbicacion(): void {
    if (!navigator.geolocation) {
      this.error = 'Tu navegador no permite obtener la ubicación.';
      return;
    }
    this.locating = true;
    navigator.geolocation.getCurrentPosition(
      pos => {
        this.setLocation(pos.coords.latitude, pos.coords.longitude, true);
        this.locating = false;
        this.cdr.markForCheck();
      },
      () => {
        this.error = 'No fue posible obtener tu ubicación. Puedes ingresar la dirección manualmente.';
        this.locating = false;
        this.cdr.markForCheck();
      },
      { enableHighAccuracy: true, timeout: 10000 }
    );
  }

  async buscarDireccion(): Promise<void> {
    const direccion = this.form.controls.direccion.value?.trim();
    const distrito = this.form.controls.distrito.value?.trim();
    if (!direccion) { this.error = 'Escribe una dirección antes de buscarla en el mapa.'; return; }
    this.searchingAddress = true;
    this.error = '';
    try {
      const query = encodeURIComponent(`${direccion}, ${distrito ?? ''}, Trujillo, Perú`);
      const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&limit=1&countrycodes=pe&q=${query}`, { headers: { 'Accept-Language': 'es' } });
      if (!response.ok) throw new Error('Servicio de ubicación no disponible');
      const result = await response.json();
      if (!result.length) throw new Error('Sin resultados');
      this.setLocation(Number(result[0].lat), Number(result[0].lon), false);
    } catch {
      this.error = 'No se encontró esa dirección. Puedes ubicar el marcador manualmente.';
    } finally {
      this.searchingAddress = false;
      this.cdr.markForCheck();
    }
  }

  private initMap(): void {
    if (!this.isDelivery || this.map) return;
    const element = document.getElementById('delivery-map');
    if (!element) return;
    if (typeof L === 'undefined') {
      this.mapError = 'El mapa no pudo cargarse. Puedes continuar ingresando la dirección manualmente.';
      this.cdr.markForCheck();
      return;
    }
    this.mapError = '';
    const lat = this.form.controls.latitud.value ?? -8.1116;
    const lng = this.form.controls.longitud.value ?? -79.0288;
    this.map = L.map(element).setView([lat, lng], 14);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '&copy; OpenStreetMap'
    }).addTo(this.map);
    this.marker = L.marker([lat, lng], { draggable: true }).addTo(this.map);
    this.marker.on('dragend', (event: any) => {
      const point = event.target.getLatLng();
      this.setLocation(point.lat, point.lng, true);
    });
    this.map.on('click', (event: any) => this.setLocation(event.latlng.lat, event.latlng.lng, true));
    this.mapReady = true;
    setTimeout(() => this.map?.invalidateSize(), 0);
    this.cdr.markForCheck();
  }

  private destroyMap(): void {
    if (this.map) this.map.remove();
    this.map = undefined;
    this.marker = undefined;
    this.mapReady = false;
  }

  private setLocation(lat: number, lng: number, reverse: boolean): void {
    const roundedLat = Number(lat.toFixed(7));
    const roundedLng = Number(lng.toFixed(7));
    this.form.patchValue({ latitud: roundedLat, longitud: roundedLng });
    if (this.map) this.map.setView([roundedLat, roundedLng], 16);
    if (this.marker) this.marker.setLatLng([roundedLat, roundedLng]);
    if (reverse) void this.reverseGeocode(roundedLat, roundedLng);
  }

  private async reverseGeocode(lat: number, lng: number): Promise<void> {
    try {
      const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`, { headers: { 'Accept-Language': 'es' } });
      if (!response.ok) return;
      const data = await response.json();
      const address = data.address ?? {};
      this.form.patchValue({
        direccion: data.display_name || this.form.controls.direccion.value,
        distrito: address.city_district || address.suburb || address.city || address.town || this.form.controls.distrito.value
      });
    } catch {
      // La ubicación se conserva aunque el servicio de texto no responda.
    } finally {
      this.cdr.markForCheck();
    }
  }

  private resetForm(): void {
    this.form.reset({
      clienteId: null,
      nombreCliente: '',
      telefono: this.auth.hasRole('CLIENTE') ? this.auth.getPhone() : '',
      tipoEntrega: 'DELIVERY',
      direccion: this.auth.hasRole('CLIENTE') ? this.auth.getAddress() : '',
      distrito: '', referencia: '', latitud: null, longitud: null, horaRecojo: '',
      observacion: '', metodoPago: 'EFECTIVO', detalles: []
    });
    this.detalles.clear();
    if (this.map) {
      this.map.setView([-8.1116, -79.0288], 14);
      this.marker?.setLatLng([-8.1116, -79.0288]);
    } else {
      setTimeout(() => this.initMap(), 0);
    }
  }
}

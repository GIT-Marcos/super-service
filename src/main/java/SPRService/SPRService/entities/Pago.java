package SPRService.SPRService.entities;

import jakarta.persistence.*;
import SPRService.SPRService.enums.MetodosPago;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pagos")
public class Pago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pk_pago", nullable = false, updatable = false)
    private UUID id;

    @Column()
    private String dni;

    @Column(name = "fecha", nullable = false)
    private LocalDate fechaPago;

    @Column(name = "monto_pagado", nullable = false)
    private BigDecimal montoPagado;

    @Column(name = "marca_tarjeta")
    private String marcaTarjeta;

    @Column()
    private String banco;

    @Column()
    private String referencia;

    @Column()
    private BigDecimal descuento;

    @Column(name = "ultimos_4")
    private String ultimos4;

    @Column(nullable = false)
    private Boolean activo;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "metodo_pago")
    private MetodosPago MetodoPago;

    @ManyToOne()
    @JoinColumn(name = "fk_venta")
    private VentaRepuesto ventaRepuesto;

    @ManyToOne()
    @JoinColumn(name = "fk_service")
    private Service service;

    public Pago() {
    }

    public Pago(UUID id, String dni, BigDecimal montoPagado, String marcaTarjeta,
                String banco, String referencia, BigDecimal descuento, String ultimos4, MetodosPago MetodoPago,
                VentaRepuesto ventaRepuesto, Service service) {
        this.id = id;
        this.dni = dni;
        this.fechaPago = LocalDate.now();
        this.montoPagado = montoPagado;
        this.marcaTarjeta = marcaTarjeta;
        this.banco = banco;
        this.referencia = referencia;
        this.descuento = descuento;
        this.ultimos4 = ultimos4;
        this.activo = Boolean.TRUE;
        this.MetodoPago = MetodoPago;
        this.ventaRepuesto = ventaRepuesto;
        this.service = service;
    }

    public void asociarVenta(VentaRepuesto v) {
        if (this.service == null) {
            this.ventaRepuesto = v;
            this.ventaRepuesto.getPagos().add(this);
        }
    }

    public void asociarService(Service s) {
        if (this.ventaRepuesto == null) {
            this.service = s;
            this.service.getPagos().add(this);
        }
    }

    public void cancelarPago() {
        this.activo = Boolean.FALSE;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(LocalDate fechaPago) {
        this.fechaPago = fechaPago;
    }

    public BigDecimal getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(BigDecimal montoPagado) {
        this.montoPagado = montoPagado;
    }

    public String getMarcaTarjeta() {
        return marcaTarjeta;
    }

    public void setMarcaTarjeta(String marcaTarjeta) {
        this.marcaTarjeta = marcaTarjeta;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getUltimos4() {
        return ultimos4;
    }

    public void setUltimos4(String ultimos4) {
        this.ultimos4 = ultimos4;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public MetodosPago getMetodoPago() {
        return MetodoPago;
    }

    public void setMetodoPago(MetodosPago MetodosPago) {
        this.MetodoPago = MetodosPago;
    }

    public VentaRepuesto getVentaRepuesto() {
        return ventaRepuesto;
    }

    public void setVentaRepuesto(VentaRepuesto ventaRepuesto) {
        this.ventaRepuesto = ventaRepuesto;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pago)) return false;

        Pago other = (Pago) o;

        // Importante: id puede ser null si la entidad aún no se ha guardado
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        // Hash code constante para evitar problemas en Sets cuando el ID se genera después de insertar
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", fechaPago=" + fechaPago +
                ", montoPagado=" + montoPagado +
                ", activo=" + activo +
                ", MetodoPago=" + MetodoPago +
                '}';
    }
}
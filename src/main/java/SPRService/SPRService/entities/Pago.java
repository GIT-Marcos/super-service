package SPRService.SPRService.entities;

import jakarta.persistence.*;
import SPRService.SPRService.enums.MetodosPago;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos")
public class Pago implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pk_pago")
    private Long id;

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

    //ENUM MÉTO DO DE PAGO
    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "metodo_pago")
    private MetodosPago MetodoPago;

    //ENUM BANCO
    //ENUM ESTADO PAGO
    //RELACIÓN CON VENTAsERVICE
    //RELACIÓN BI CON VENTA REPUESTO
    //todo: no valida optional
    @ManyToOne(optional = false)
    private VentaRepuesto ventaRepuesto;

    public Pago() {
    }

    public Pago(Long id, String dni, BigDecimal montoPagado, String marcaTarjeta,
                String banco, String referencia, BigDecimal descuento, String ultimos4, MetodosPago MetodoPago,
                VentaRepuesto ventaRepuesto) {
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
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    @Override
    public String toString() {
        return "Pago{" +
                "id=" + id +
                ", dni='" + dni + '\'' +
                ", fechaPago=" + fechaPago +
                ", montoPagado=" + montoPagado +
                ", marcaTarjeta='" + marcaTarjeta + '\'' +
                ", banco='" + banco + '\'' +
                ", referencia='" + referencia + '\'' +
                ", descuento=" + descuento +
                ", ultimos4='" + ultimos4 + '\'' +
                ", activo=" + activo +
                ", MetodoPago=" + MetodoPago +
                '}';
    }

    protected void cancelar() {
        this.activo = Boolean.FALSE;
    }
}

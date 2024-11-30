package com.example.Proyecto_CALIDAD.Clases;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "tratamiento")
public class Tratamiento {

    @Id
    private String id;

    private String nombreTratamiento;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private String frecuenciaSesiones;
}

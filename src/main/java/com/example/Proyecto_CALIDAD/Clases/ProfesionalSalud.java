package com.example.Proyecto_CALIDAD.Clases;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="profesional_salud")
public class ProfesionalSalud {
    @Id
    private String id;
    private String nombre;
    private String apellido;
    private String especialidad;
    private String telefono;
    private String correo;
    private String horario;
    private String numeroLicencia;
}

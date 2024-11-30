package com.example.Proyecto_CALIDAD.Clases;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="paciente")
public class Paciente {
    @Id
    private String codigo;
    private String nombre;
    private String apellido;
    private String dni;
    private String celular;
    private String correo; 
}

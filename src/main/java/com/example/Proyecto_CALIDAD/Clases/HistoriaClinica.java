package com.example.Proyecto_CALIDAD.Clases;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "historia_clinica")
public class HistoriaClinica {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne
    @JoinColumn(name = "profesional_salud_id", nullable = false)
    private ProfesionalSalud profesionalSalud;

    @ManyToOne
    @JoinColumn(name = "tratamiento_id", nullable = false)
    private Tratamiento tratamiento;

    private Date fechaCreacion;
    private String notasProfesional;
    private String diagnostico;
    private String observaciones;
    private String planSeguimiento;
}

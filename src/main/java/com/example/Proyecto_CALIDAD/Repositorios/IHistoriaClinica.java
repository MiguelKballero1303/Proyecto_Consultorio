package com.example.Proyecto_CALIDAD.Repositorios;

import com.example.Proyecto_CALIDAD.Clases.Cita;
import com.example.Proyecto_CALIDAD.Clases.HistoriaClinica;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistoriaClinica extends CrudRepository<HistoriaClinica, String> {

    @Query(value = "SELECT * FROM historia_clinica "
            + "WHERE paciente_id LIKE %:desc% "
            + "OR profesional_salud_id LIKE %:desc% "
            + "OR tratamiento_id LIKE %:desc% " // Ajuste para id_tratamiento
            + "OR notas_profesional LIKE %:desc% "
            + "OR diagnostico LIKE %:desc% "
            + "OR observaciones LIKE %:desc% "
            + "OR plan_seguimiento LIKE %:desc%", nativeQuery = true)
    List<HistoriaClinica> findForAll(@Param("desc") String desc);

    @Query("SELECT h FROM HistoriaClinica h")
    Page<HistoriaClinica> findAll(Pageable pageable);
    
}

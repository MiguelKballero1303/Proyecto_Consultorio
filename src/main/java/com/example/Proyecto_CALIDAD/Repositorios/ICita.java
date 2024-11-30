package com.example.Proyecto_CALIDAD.Repositorios;

import com.example.Proyecto_CALIDAD.Clases.Cita;
import com.example.Proyecto_CALIDAD.Clases.Tratamiento;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICita extends CrudRepository<Cita, String> {

    @Query(value = "SELECT * FROM cita "
            + "WHERE paciente_id LIKE %:desc% "
            + "OR profesional_salud_id LIKE %:desc% "
            + "OR motivo LIKE %:desc% "
            + "OR id LIKE %:desc% "
            + "OR estado LIKE %:desc%", nativeQuery = true)
    List<Cita> findForAll(@Param("desc") String desc);

    @Query("SELECT c FROM Cita c")
    Page<Cita> findAll(Pageable pageable);
}

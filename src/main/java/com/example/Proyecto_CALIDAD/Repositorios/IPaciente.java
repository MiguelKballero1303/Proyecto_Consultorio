package com.example.Proyecto_CALIDAD.Repositorios;

import com.example.Proyecto_CALIDAD.Clases.Paciente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPaciente extends CrudRepository<Paciente, String> {

    @Query(value = "SELECT * FROM paciente "
            + "WHERE nombre LIKE %:desc% "
            + "OR apellido LIKE %:desc% "
            + "OR dni LIKE %:desc% "
            + "OR celular LIKE %:desc% "
            + "OR correo LIKE %:desc%", nativeQuery = true)
    List<Paciente> findForAll(@Param("desc") String desc);

    @Query("SELECT p FROM Paciente p")
    Page<Paciente> findAll(Pageable pageable);
}

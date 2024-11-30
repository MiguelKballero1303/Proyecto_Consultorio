package com.example.Proyecto_CALIDAD.Repositorios;

import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ITratamiento extends CrudRepository<Tratamiento, String> {

    @Query(value = "SELECT * FROM tratamiento "
            + "WHERE nombre_tratamiento LIKE %:desc% "
            + "OR descripcion LIKE %:desc% "
            + "OR fecha_inicio LIKE %:desc% "
            + "OR fecha_fin LIKE %:desc% "
            + "OR frecuencia_sesiones LIKE %:desc%", nativeQuery = true)
    List<Tratamiento> findForAll(@Param("desc") String desc);
    
    @Query("SELECT t FROM Tratamiento t")
    Page<Tratamiento> findAll(Pageable pageable);
}

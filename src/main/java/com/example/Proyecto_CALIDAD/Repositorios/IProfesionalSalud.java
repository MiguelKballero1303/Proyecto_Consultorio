package com.example.Proyecto_CALIDAD.Repositorios;

import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IProfesionalSalud extends CrudRepository<ProfesionalSalud, String> {
    
    @Query(value = "SELECT * FROM profesional_salud "
                 + "WHERE nombre LIKE %:desc% "
                 + "OR apellido LIKE %:desc% "
                 + "OR especialidad LIKE %:desc% "
                 + "OR telefono LIKE %:desc% "
                 + "OR correo LIKE %:desc%", nativeQuery = true)
    List<ProfesionalSalud> findForAll(@Param("desc") String desc);
    
    @Query("SELECT p FROM ProfesionalSalud p")
    Page<ProfesionalSalud> findAll(Pageable pageable);
}

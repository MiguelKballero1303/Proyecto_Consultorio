package com.example.Proyecto_CALIDAD.Repositorios;

import com.example.Proyecto_CALIDAD.Clases.Factura;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFactura extends CrudRepository<Factura, String> {

    @Query(value = "SELECT * FROM factura "
            + "WHERE paciente_id LIKE %:desc% "
            + "OR cita_id LIKE %:desc% "
            + "OR detalles_servicios LIKE %:desc% "
            + "OR estado_pago LIKE %:desc%", nativeQuery = true)
    List<Factura> findForAll(@Param("desc") String desc);

    @Query("SELECT f FROM Factura f")
    Page<Factura> findAll(Pageable pageable);
}

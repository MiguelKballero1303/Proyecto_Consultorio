package com.example.Proyecto_CALIDAD.Interfaces;

import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import java.util.List;
import java.util.Optional;

public interface ITratamientoService {
    List<Tratamiento> Listar();
    Optional<Tratamiento> ConsultarId(String id);
    void Guardar(Tratamiento t);
    void Eliminar(String id);
    List<Tratamiento> Buscar(String desc);
    List<Tratamiento> listarTratamientos(int pagina, int itemsPorPagina);
    int contarTratamientos();
}

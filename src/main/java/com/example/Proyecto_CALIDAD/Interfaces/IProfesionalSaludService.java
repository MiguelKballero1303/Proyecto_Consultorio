package com.example.Proyecto_CALIDAD.Interfaces;

import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import java.util.List;
import java.util.Optional;

public interface IProfesionalSaludService {
    List<ProfesionalSalud> Listar();
    Optional<ProfesionalSalud> ConsultarId(String id);
    void Guardar(ProfesionalSalud p);
    void Eliminar(String id);
    List<ProfesionalSalud> Buscar(String desc);
    List<ProfesionalSalud> listarProfesionales(int pagina, int itemsPorPagina);
    int contarProfesionales();
}

package com.example.Proyecto_CALIDAD.Interfaces;

import com.example.Proyecto_CALIDAD.Clases.Cita;

import java.util.List;
import java.util.Optional;

public interface ICitaService {
    List<Cita> Listar();

    Optional<Cita> ConsultarId(String id);

    void Guardar(Cita c);

    void Eliminar(String id);

    List<Cita> Buscar(String desc);

    List<Cita> listarCitas(int pagina, int itemsPorPagina);

    int contarCitas();
}

package com.example.Proyecto_CALIDAD.Interfaces;

import com.example.Proyecto_CALIDAD.Clases.Paciente;
import java.util.List;
import java.util.Optional;

public interface IPacienteService {
    List<Paciente> Listar();
    Optional<Paciente> ConsultarId(String id);
    void Guardar(Paciente p);
    void Eliminar(String id);
    List<Paciente> Buscar(String desc);
    List<Paciente> listarPacientes(int pagina, int itemsPorPagina);
    int contarPacientes();
}

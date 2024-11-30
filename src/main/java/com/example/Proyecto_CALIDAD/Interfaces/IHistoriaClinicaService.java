package com.example.Proyecto_CALIDAD.Interfaces;

import com.example.Proyecto_CALIDAD.Clases.HistoriaClinica;
import java.util.List;
import java.util.Optional;

public interface IHistoriaClinicaService {
    List<HistoriaClinica> Listar();
    Optional<HistoriaClinica> ConsultarId(String id);
    void Guardar(HistoriaClinica h);
    void Eliminar(String id);
    List<HistoriaClinica> Buscar(String desc);
    List<HistoriaClinica> listarHistorias(int pagina, int itemsPorPagina);
    int contarHistorias();
    
}

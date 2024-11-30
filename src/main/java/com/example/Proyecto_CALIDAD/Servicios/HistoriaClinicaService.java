package com.example.Proyecto_CALIDAD.Servicios;

import com.example.Proyecto_CALIDAD.Clases.HistoriaClinica;
import com.example.Proyecto_CALIDAD.Interfaces.IHistoriaClinicaService;
import com.example.Proyecto_CALIDAD.Repositorios.IHistoriaClinica;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class HistoriaClinicaService implements IHistoriaClinicaService {

    @Autowired
    private IHistoriaClinica data;

    @Override
    public List<HistoriaClinica> Listar() {
        return (List<HistoriaClinica>) data.findAll();
    }

    @Override
    public Optional<HistoriaClinica> ConsultarId(String id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(HistoriaClinica h) {
        data.save(h);
    }

    @Override
    public void Eliminar(String id) {
        data.deleteById(id);
    }

    @Override
    public List<HistoriaClinica> Buscar(String desc) {
        return data.findForAll(desc);
    }
     @Override
    public List<HistoriaClinica> listarHistorias(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<HistoriaClinica> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarHistorias() {
        return (int) data.count();
    }
    
}

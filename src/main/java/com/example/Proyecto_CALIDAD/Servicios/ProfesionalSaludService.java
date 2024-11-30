package com.example.Proyecto_CALIDAD.Servicios;

import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Repositorios.IProfesionalSalud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalSaludService implements IProfesionalSaludService {

    @Autowired
    private IProfesionalSalud data;
    
    @Override
    public List<ProfesionalSalud> Listar() {
        return (List<ProfesionalSalud>) data.findAll();
    }

    @Override
    public Optional<ProfesionalSalud> ConsultarId(String id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(ProfesionalSalud p) {
        data.save(p);
    }

    @Override
    public void Eliminar(String id) {
        data.deleteById(id);
    }

    @Override
    public List<ProfesionalSalud> Buscar(String desc) {
        return data.findForAll(desc);
    }

    @Override
    public List<ProfesionalSalud> listarProfesionales(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<ProfesionalSalud> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarProfesionales() {
        return (int) data.count();
    }
}

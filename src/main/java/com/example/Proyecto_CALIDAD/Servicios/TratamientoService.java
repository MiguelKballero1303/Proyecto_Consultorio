package com.example.Proyecto_CALIDAD.Servicios;

import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import com.example.Proyecto_CALIDAD.Interfaces.ITratamientoService;
import com.example.Proyecto_CALIDAD.Repositorios.ITratamiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TratamientoService implements ITratamientoService {

    @Autowired
    private ITratamiento data;

    @Override
    public List<Tratamiento> Listar() {
        return (List<Tratamiento>) data.findAll();
    }

    @Override
    public Optional<Tratamiento> ConsultarId(String id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Tratamiento t) {
        data.save(t);
    }

    @Override
    public void Eliminar(String id) {
        data.deleteById(id);
    }

    @Override
    public List<Tratamiento> Buscar(String desc) {
        return data.findForAll(desc);
    }

    @Override
    public List<Tratamiento> listarTratamientos(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<Tratamiento> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarTratamientos() {
        return (int) data.count();
    }
}

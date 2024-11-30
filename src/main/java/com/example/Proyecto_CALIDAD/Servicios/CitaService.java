package com.example.Proyecto_CALIDAD.Servicios;

import com.example.Proyecto_CALIDAD.Clases.Cita;
import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import com.example.Proyecto_CALIDAD.Interfaces.ICitaService;
import com.example.Proyecto_CALIDAD.Repositorios.ICita;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CitaService implements ICitaService {

    @Autowired
    private ICita data;

    @Override
    public List<Cita> Listar() {
        return (List<Cita>) data.findAll();
    }

    @Override
    public Optional<Cita> ConsultarId(String id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Cita c) {
        data.save(c);
    }

    @Override
    public void Eliminar(String id) {
        data.deleteById(id);
    }

    @Override
    public List<Cita> Buscar(String desc) {
        return data.findForAll(desc);
    }

    @Override
    public List<Cita> listarCitas(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<Cita> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarCitas() {
        return (int) data.count();
    }
}

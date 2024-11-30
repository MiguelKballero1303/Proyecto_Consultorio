package com.example.Proyecto_CALIDAD.Servicios;

import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Repositorios.IPaciente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService implements IPacienteService {

    @Autowired
    private IPaciente data;

    @Override
    public List<Paciente> Listar() {
        return (List<Paciente>) data.findAll();
    }

    @Override
    public Optional<Paciente> ConsultarId(String id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Paciente p) {
        data.save(p);
    }

    @Override
    public void Eliminar(String id) {
        data.deleteById(id);
    }

    @Override
    public List<Paciente> Buscar(String desc) {
        return data.findForAll(desc);
    }

    @Override
    public List<Paciente> listarPacientes(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<Paciente> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarPacientes() {
        return (int) data.count();
    }
}

package com.example.Proyecto_CALIDAD.Servicios;

import com.example.Proyecto_CALIDAD.Clases.Factura;
import com.example.Proyecto_CALIDAD.Interfaces.IFacturaService;
import com.example.Proyecto_CALIDAD.Repositorios.IFactura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService implements IFacturaService {

    @Autowired
    private IFactura data;

    @Override
    public List<Factura> Listar() {
        return (List<Factura>) data.findAll();
    }

    @Override
    public Optional<Factura> ConsultarId(String id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Factura f) {
        data.save(f);
    }

    @Override
    public void Eliminar(String id) {
        data.deleteById(id);
    }

    @Override
    public List<Factura> Buscar(String desc) {
        return data.findForAll(desc);
    }

    @Override
    public List<Factura> listarFacturas(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<Factura> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarFacturas() {
        return (int) data.count();
    }
}

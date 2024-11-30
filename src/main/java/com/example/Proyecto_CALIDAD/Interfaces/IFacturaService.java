package com.example.Proyecto_CALIDAD.Interfaces;
import com.example.Proyecto_CALIDAD.Clases.Factura;
import java.util.List;
import java.util.Optional;

public interface IFacturaService {
    public List<Factura> Listar();

    public Optional<Factura> ConsultarId(String id);

    public void Guardar(Factura f);

    public void Eliminar(String id);

    public List<Factura> Buscar(String desc);

    List<Factura> listarFacturas(int pagina, int itemsPorPagina);

    int contarFacturas();
}

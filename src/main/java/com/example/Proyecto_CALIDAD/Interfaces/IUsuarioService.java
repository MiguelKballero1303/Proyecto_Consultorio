package com.example.Proyecto_CALIDAD.Interfaces;

import java.util.List;
import java.util.Optional;

import com.example.Proyecto_CALIDAD.Clases.Usuario;

public interface IUsuarioService {
    public List<Usuario> Listar();

    public Optional<Usuario> ConsultarId(int id);

    public void Guardar(Usuario u);

    public void Eliminar(int id);

    public List<Usuario> Buscar(String desc);

    public String obtenerNombreImagenUsuarioConectado();

    List<Usuario> listarUsuarios(int pagina, int itemsPorPagina);

    int contarUsuarios();
    Optional<Usuario> findByEmail(String email);
    void actualizarContraseña(int id, String nuevaContraseña);
}

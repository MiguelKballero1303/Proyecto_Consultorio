package com.example.Proyecto_CALIDAD.Servicios;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Proyecto_CALIDAD.Clases.Usuario;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import com.example.Proyecto_CALIDAD.Repositorios.IUsuario;

@Service
public class UsuarioService implements IUsuarioService, UserDetailsService {

    @Autowired
    private IUsuario data;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuario = data.findByUserr(username);
        return usuario.map(UsuarioServiceDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    @Override
    public List<Usuario> Listar() {
        return (List<Usuario>) data.findAll();
    }

    @Override
    public Optional<Usuario> ConsultarId(int id) {
        return data.findById(id);
    }

    @Override
    public void Guardar(Usuario u) {
        data.save(u);
    }

    @Override
    public void Eliminar(int id) {
        data.deleteById(id);
    }

    @Override
    public List<Usuario> Buscar(String desc) {
        return data.findForAll(desc);
    }

    public void loginFailed(String username) {
        Optional<Usuario> userOptional = data.findByUserr(username);
        userOptional.ifPresent(user -> {
            user.incrementFailedLoginAttempts();
            user.setLastFailedLoginTime(LocalDateTime.now());
            if (user.getFailedLoginAttempts() >= 3) {
                user.setAccountNonLocked(false);
            }
            data.save(user);
        });
    }

    public void loginSucceeded(String username) {
        Optional<Usuario> userOptional = data.findByUserr(username);
        userOptional.ifPresent(user -> {
            user.resetFailedLoginAttempts();
            user.setAccountNonLocked(true);
            data.save(user);
        });
    }

    public void desbloquearCuentas() {
        List<Usuario> usuarios = Listar();
        LocalDateTime now = LocalDateTime.now();

        for (Usuario usuario : usuarios) {
            if (!usuario.isAccountNonLocked() && usuario.getLastFailedLoginTime() != null) {
                long minutes = java.time.Duration.between(usuario.getLastFailedLoginTime(), now).toMinutes();
                if (minutes >= 1) {
                    usuario.resetFailedLoginAttempts();
                    usuario.setAccountNonLocked(true);
                    data.save(usuario);
                }
            }
        }
    }

    public String obtenerNombreImagenUsuarioConectado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Optional<Usuario> usuarioOptional = data.findByUserr(username);
        return usuarioOptional.map(Usuario::getImagenUrl).orElse(""); // Obtener el nombre de la imagen del usuario
                                                                      // conectado
    }

    @Override
    public List<Usuario> listarUsuarios(int pagina, int itemsPorPagina) {
        Pageable pageable = PageRequest.of(pagina - 1, itemsPorPagina);
        Page<Usuario> page = data.findAll(pageable);
        return page.getContent();
    }

    @Override
    public int contarUsuarios() {
        return (int) data.count();
    }
    @Override
    public Optional<Usuario> findByEmail(String email) {
        return data.findByEmail(email);
    }

    @Override
    public void actualizarContraseña(int id, String nuevaContraseña) {
        Optional<Usuario> usuario = data.findById(id);
        usuario.ifPresent(u -> {
            u.setPassword(passwordEncoder.encode(nuevaContraseña));
            data.save(u);
        });
    }
}

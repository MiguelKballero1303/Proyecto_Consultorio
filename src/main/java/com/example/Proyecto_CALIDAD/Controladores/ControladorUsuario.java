package com.example.Proyecto_CALIDAD.Controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto_CALIDAD.Clases.FileStorageService;
import com.example.Proyecto_CALIDAD.Clases.Usuario;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("/Usuario/")
@Controller
public class ControladorUsuario {

    BCryptPasswordEncoder bcript = new BCryptPasswordEncoder();
    String carpeta = "Usuario/";
    private final HttpServletRequest request;
    @Autowired
    IUsuarioService service;

    private final FileStorageService fileStorageService;

    @Autowired
    public ControladorUsuario(FileStorageService fileStorageService, HttpServletRequest request) {
        this.fileStorageService = fileStorageService;
        this.request = request;
    }

    private void setUserDetails(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        GrantedAuthority authority = auth.getAuthorities().stream().findFirst().orElse(null);
        String userRole = (authority != null) ? authority.getAuthority() : "ROL_NO_ASIGNADO";

        switch (userRole) {
            case "ROL_ADMIN":
                userRole = "Administrador General";
                break;
            case "ROL_ENC":
                userRole = "Encargado de Recepción";
                break;
            case "ROL_PROF":
                userRole = "Profesional";
                break;
            default:
                userRole = "Rol no asignado";
                break;
        }

        String nombreImagen = service.obtenerNombreImagenUsuarioConectado();
        model.addAttribute("nombreImagen", nombreImagen);
        model.addAttribute("username", username);
        model.addAttribute("userRole", userRole);
    }

    @PostMapping("/verificarCorreo")
    @ResponseBody
    public Map<String, Object> verificarCorreo(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        Optional<Usuario> usuarioOpt = service.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            response.put("status", "success");
        } else {
            response.put("status", "error");
            response.put("message", "No se encontró un usuario con ese correo electrónico.");
        }
        return response;
    }

    // Mostrar formulario de restablecimiento de contraseña
    @GetMapping("/RestablecerContraseña")
    public String mostrarFormularioRestablecimiento() {
        return "restablecerContraseña"; // Vista del formulario
    }

    // Procesar solicitud de restablecimiento de contraseña
    @PostMapping("/RestablecerContraseña")
    public String restablecerContraseña(@RequestParam("email") String email,
            @RequestParam("nuevaContraseña") String nuevaContraseña,
            Model model) {
        Optional<Usuario> usuarioOpt = service.findByEmail(email);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            service.actualizarContraseña(usuario.getId(), nuevaContraseña);
            model.addAttribute("mensaje", "Contraseña actualizada exitosamente.");
            return "redirect:/Login"; // Redirigir al inicio de sesión después del restablecimiento
        } else {
            model.addAttribute("error", "No se encontró un usuario con ese correo electrónico.");
            return "restablecerContraseña";
        }

    }

    @GetMapping("/NuevoUsuario")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String NuevoUsuario(Model model) {
        setUserDetails(model);
        return carpeta + "nuevoUsuario"; // nuevoUsuario.html
    }

    @PostMapping("/RegistrarPendiente")
    public String RegistrarPendiente(
            @RequestParam("nom") String nom,
            @RequestParam("ape") String ape,
            @RequestParam("dni") String dni,
            @RequestParam("cel") String cel,
            @RequestParam("email") String email,
            @RequestParam("dir") String dir,
            @RequestParam("pas") String pas,
            @RequestParam("use") String use,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            Model model) throws IOException {

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nom);
        nuevoUsuario.setApellido(ape);
        nuevoUsuario.setDni(dni);
        nuevoUsuario.setCelular(cel);
        nuevoUsuario.setEmail(email);
        nuevoUsuario.setDireccion(dir);
        nuevoUsuario.setPassword(bcript.encode(pas));
        nuevoUsuario.setUserr(use);
        nuevoUsuario.setRoles("ROL_PENDIENTE");
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen);
            nuevoUsuario.setImagenUrl(fileName);
        }
        service.Guardar(nuevoUsuario);
        model.addAttribute("mensaje", "Solicitud enviada. Un administrador revisará su información.");

        return "redirect:/Login";
    }

    @PostMapping("/RegistrarUsuario") // localhost/cliente/registrar
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String RegistrarUsuario(
            @RequestParam("nom") String nom,
            @RequestParam("ape") String ape,
            @RequestParam("dni") String dni,
            @RequestParam("cel") String cel,
            @RequestParam("email") String email,
            @RequestParam("dir") String dir,
            @RequestParam("pas") String pas,
            @RequestParam("use") String use,
            @RequestParam("rol") String rol,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            Model model) throws IOException {
        setUserDetails(model);
        Usuario u = new Usuario();
        u.setNombre(nom);
        u.setApellido(ape);
        u.setDni(dni);
        u.setCelular(cel);
        u.setEmail(email);
        u.setDireccion(dir);
        u.setPassword(bcript.encode(pas));
        u.setUserr(use);
        u.setRoles(rol);
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen);
            u.setImagenUrl(fileName);
        }
        service.Guardar(u);

        return ListarUsuario(model, 1, 10);
    }

    @GetMapping("/ListaUsuarios")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String ListarUsuario(Model model, @RequestParam(name = "pagina", defaultValue = "1") int pagina,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {
        setUserDetails(model);
        int totalRegistros = service.contarUsuarios();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<Usuario> usuarios = service.listarUsuarios(pagina, itemsPerPage);
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);
        return carpeta + "listaUsuario";
    }

    @GetMapping("/EliminarUsuario")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String EliminarUsuario(@RequestParam("cod") int cod,
            Model model) {
        setUserDetails(model);
        service.Eliminar(cod);
        return ListarUsuario(model, 1, 10);
    }

    @GetMapping("/EditarUsuario")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String EditarUsuario(@RequestParam("cod") int cod,
            Model model) {
        setUserDetails(model);
        Optional<Usuario> usu = service.ConsultarId(cod);

        model.addAttribute("usuario", usu);
        return carpeta + "editarUsuario"; // editarUsuario.html
    }

    @PostMapping("/ActualizarUsuario")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String ActualizarUsuario(@RequestParam("id") int cod,
            @RequestParam("nombre") String nom,
            @RequestParam("apellido") String ape,
            @RequestParam("dni") String dni,
            @RequestParam("celular") String cel,
            @RequestParam("email") String email,
            @RequestParam("direccion") String dir,
            @RequestParam("password") String pas,
            @RequestParam("userr") String use,
            @RequestParam("rol") String rol,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            Model model) {
        setUserDetails(model);
        Usuario u = new Usuario();
        u.setId(cod);
        u.setNombre(nom);
        u.setApellido(ape);
        u.setDni(dni);
        u.setCelular(cel);
        u.setEmail(email);
        u.setDireccion(dir);
        u.setPassword(bcript.encode(pas));
        u.setUserr(use);
        u.setRoles(rol);

        Optional<Usuario> usuarioOptional = service.ConsultarId(cod);
        usuarioOptional.ifPresent(usuario -> {
            String imagenAnterior = usuario.getImagenUrl();
            if (imagenAnterior != null && !imagenAnterior.isEmpty()) {
                fileStorageService.deleteFile(imagenAnterior);
            }
        });

        if (imagen != null && !imagen.isEmpty()) {
            String fileName = fileStorageService.storeFile(imagen);
            u.setImagenUrl(fileName);
        }
        service.Guardar(u);

        return ListarUsuario(model, 1, 10);
    }

    @PostMapping("/BuscarUsuario")
    @PreAuthorize("hasAuthority('ROL_ADMIN')")
    public String BuscarUsuario(@RequestParam("desc") String desc,
            Model model) {
        setUserDetails(model);
        List<Usuario> usuarios = service.Buscar(desc);
        model.addAttribute("usuarios", usuarios);
        return carpeta + "listaUsuario";
    }
}

package com.example.Proyecto_CALIDAD.Controladores;

import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import com.example.Proyecto_CALIDAD.Interfaces.ITratamientoService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequestMapping("/Tratamiento/")
@Controller
public class ControladorTratamiento {

    String carpeta = "Tratamiento/";

    @Autowired
    ITratamientoService service;

    @Autowired
    IUsuarioService service_u;

    private void obtenerDatosUsuario(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        GrantedAuthority authority = auth.getAuthorities().stream().findFirst().orElse(null);
        String userRole = (authority != null) ? authority.getAuthority() : "ROL_NO_ASIGNADO";
        if ("ROL_ADMIN".equals(userRole)) {
            userRole = "Administrador General";
        } else if ("ROL_ENC".equals(userRole)) {
            userRole = "Encargado de Recepción";
        } else if ("ROL_PROF".equals(userRole)) {
            userRole = "Profesional";
        }
        String nombreImagen = service_u.obtenerNombreImagenUsuarioConectado();
        model.addAttribute("nombreImagen", nombreImagen);
        model.addAttribute("username", username);
        model.addAttribute("userRole", userRole);
    }

    @GetMapping("/NuevoTratamiento")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String NuevoTratamiento(Model model) {
        obtenerDatosUsuario(model);
        return carpeta + "nuevoTratamiento";
    }

    private String generateRandomID() {
        StringBuilder sb = new StringBuilder();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    @PostMapping("/RegistrarTratamiento")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String RegistrarTratamiento(
            @RequestParam("nombreTratamiento") String nombreTratamiento,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("fechaInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam("fechaFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
            @RequestParam("frecuenciaSesiones") String frecuenciaSesiones,
            Model model) {

        obtenerDatosUsuario(model);

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(generateRandomID());
        tratamiento.setNombreTratamiento(nombreTratamiento);
        tratamiento.setDescripcion(descripcion);
        tratamiento.setFechaInicio(fechaInicio);
        tratamiento.setFechaFin(fechaFin);
        tratamiento.setFrecuenciaSesiones(frecuenciaSesiones);

        service.Guardar(tratamiento);
        return ListarTratamientos(model, 1, 10); // Paginación por defecto: página 1, 10 elementos por página
    }

    @GetMapping("/ListaTratamientos")
    public String ListarTratamientos(Model model,
                                     @RequestParam(name = "pagina", defaultValue = "1") int pagina,
                                     @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {

        obtenerDatosUsuario(model);

        int totalRegistros = service.contarTratamientos();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<Tratamiento> tratamientos = service.listarTratamientos(pagina, itemsPerPage);

        // Crear una lista de números de página
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }

        model.addAttribute("tratamientos", tratamientos);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);

        return carpeta + "listaTratamiento";
    }

    @GetMapping("/EliminarTratamiento")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EliminarTratamiento(@RequestParam("idTratamiento") String id, Model model) {
        obtenerDatosUsuario(model);
        service.Eliminar(id);
        return ListarTratamientos(model, 1, 10); // Paginación por defecto
    }

    @GetMapping("/EditarTratamiento")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EditarTratamiento(@RequestParam("idTratamiento") String id, Model model) {
        obtenerDatosUsuario(model);

        Optional<Tratamiento> tratamiento = service.ConsultarId(id);
        if (tratamiento.isPresent()) {
            model.addAttribute("tratamiento", tratamiento.get());
        } else {
            model.addAttribute("error", "Tratamiento no encontrado");
            return ListarTratamientos(model, 1, 10); // Redirigir si no se encuentra el tratamiento
        }
        return carpeta + "editarTratamiento";
    }

    @PostMapping("/ActualizarTratamiento")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String ActualizarTratamiento(@RequestParam("id") String id,
                                        @RequestParam("nombreTratamiento") String nombreTratamiento,
                                        @RequestParam("descripcion") String descripcion,
                                        @RequestParam("fechaInicio") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
                                        @RequestParam("fechaFin") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin,
                                        @RequestParam("frecuenciaSesiones") String frecuenciaSesiones,
                                        Model model) {

        obtenerDatosUsuario(model);

        Tratamiento tratamiento = new Tratamiento();
        tratamiento.setId(id);
        tratamiento.setNombreTratamiento(nombreTratamiento);
        tratamiento.setDescripcion(descripcion);
        tratamiento.setFechaInicio(fechaInicio);
        tratamiento.setFechaFin(fechaFin);
        tratamiento.setFrecuenciaSesiones(frecuenciaSesiones);

        service.Guardar(tratamiento);
        return ListarTratamientos(model, 1, 10); // Paginación por defecto
    }

    @PostMapping("/BuscarTratamientos")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC') or hasAuthority('ROL_OPER')")
    public String BuscarTratamientos(@RequestParam("desc") String desc, Model model) {
        obtenerDatosUsuario(model);
        model.addAttribute("tratamientos", service.Buscar(desc));
        return carpeta + "listaTratamiento";
    }
}

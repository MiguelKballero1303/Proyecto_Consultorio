package com.example.Proyecto_CALIDAD.Controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

@RequestMapping("/ProfesionalSalud/")
@Controller
public class ControladorProfesionalSalud {

    String carpeta = "ProfesionalSalud/";

    @Autowired
    IProfesionalSaludService service;

    @Autowired
    IUsuarioService service_u;

    private void addUserAttributes(Model model) {
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

    @GetMapping("/NuevoProfesional")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String NuevoProfesional(Model model) {
        addUserAttributes(model);
        return carpeta + "nuevoProfesional";
    }

    @PostMapping("/RegistrarProfesional")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String RegistrarProfesional(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("telefono") String telefono,
            @RequestParam("correo") String correo,
            @RequestParam("horario") String horario,
            @RequestParam("numeroLicencia") String numeroLicencia,
            Model model) {

        addUserAttributes(model);

        ProfesionalSalud p = new ProfesionalSalud();
        p.setId(generateRandomID());
        p.setNombre(nombre);
        p.setApellido(apellido);
        p.setEspecialidad(especialidad);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        p.setHorario(horario);
        p.setNumeroLicencia(numeroLicencia);

        service.Guardar(p);
        return ListarProfesional(model, 1, 10); // Paginación por defecto: página 1, 10 elementos por página
    }

    @GetMapping("/ListaProfesionales")
    public String ListarProfesional(Model model,
            @RequestParam(name = "pagina", defaultValue = "1") int pagina,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {

        addUserAttributes(model);

        int totalRegistros = service.contarProfesionales();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<ProfesionalSalud> profesionales = service.listarProfesionales(pagina, itemsPerPage);

        // Crear una lista de números de página
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }

        model.addAttribute("profesionales", profesionales);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);

        return carpeta + "listaProfesional";
    }

    @GetMapping("/EliminarProfesional")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EliminarProfesional(@RequestParam("idP") String id, Model model) {
        addUserAttributes(model);
        service.Eliminar(id);
        return ListarProfesional(model, 1, 10); // Paginación por defecto
    }

    @GetMapping("/EditarProfesional")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EditarProfesional(@RequestParam("idP") String id, Model model) {
        addUserAttributes(model);

        Optional<ProfesionalSalud> profesional = service.ConsultarId(id);
        model.addAttribute("profesional", profesional);
        return carpeta + "editarProfesional";
    }

    @PostMapping("/ActualizarProfesional")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String ActualizarProfesional(@RequestParam("id") String id,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("especialidad") String especialidad,
            @RequestParam("telefono") String telefono,
            @RequestParam("correo") String correo,
            @RequestParam("horario") String horario,
            @RequestParam("numeroLicencia") String numeroLicencia,
            Model model) {

        addUserAttributes(model);

        ProfesionalSalud p = new ProfesionalSalud();
        p.setId(id);
        p.setNombre(nombre);
        p.setApellido(apellido);
        p.setEspecialidad(especialidad);
        p.setTelefono(telefono);
        p.setCorreo(correo);
        p.setHorario(horario);
        p.setNumeroLicencia(numeroLicencia);

        service.Guardar(p);
        return ListarProfesional(model, 1, 10); // Paginación por defecto: página 1, 10 elementos por página
    }

    @PostMapping("/BuscarProfesional")
    public String BuscarProfesional(@RequestParam("desc") String desc, Model model) {
        addUserAttributes(model);
        model.addAttribute("profesionales", service.Buscar(desc));
        return carpeta + "listaProfesional";
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
}

package com.example.Proyecto_CALIDAD.Controladores;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

@RequestMapping("/Paciente/")
@Controller
public class ControladorPaciente {

    String carpeta = "Paciente/";

    @Autowired
    IPacienteService service;

    @Autowired
    IUsuarioService service_u;

    private void setUserAttributes(Model model) {
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

    @GetMapping("/NuevoPaciente")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String NuevoPaciente(Model model) {
        setUserAttributes(model);
        return carpeta + "nuevoPaciente";
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

    @PostMapping("/RegistrarPaciente")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String RegistrarPaciente(
            @RequestParam("nom") String nom,
            @RequestParam("ape") String ape,
            @RequestParam("dni") String dni,
            @RequestParam("cel") String cel,
            @RequestParam("correo") String correo,
            Model model) {

        setUserAttributes(model);

        Paciente p = new Paciente();
        p.setCodigo(generateRandomID());
        p.setNombre(nom);
        p.setApellido(ape);
        p.setDni(dni);
        p.setCelular(cel);
        p.setCorreo(correo);

        service.Guardar(p);
        return ListarPaciente(model, 1, 10); 
    }
    @PostMapping("/RegistrarPacienteAutomatico")
    public ResponseEntity<String> RegistrarPacienteAutomatico(@RequestBody Map<String, Object> registrationData) {
        String nombre = (String) registrationData.get("name");
        String apellido = (String) registrationData.get("last_name");
        String dni = (String) registrationData.get("dni");
        String celular = (String) registrationData.get("celular");
        String correo = (String) registrationData.get("correo");
    
        System.out.println("Datos del paciente recibidos: " + registrationData);
        if (dni == null || nombre == null || celular == null || correo == null) {
            System.out.println("Datos incompletos");
            return ResponseEntity.badRequest().body("Datos del paciente incompletos");
        }
    
        Paciente p = new Paciente();
        p.setCodigo(generateRandomID());
        p.setNombre(nombre);
        p.setApellido(apellido);
        p.setDni(dni);
        p.setCelular(celular);
        p.setCorreo(correo);
        service.Guardar(p);
    
        System.out.println("Paciente registrado exitosamente");
        return ResponseEntity.ok("Paciente registrado exitosamente");
    }
    
    

    @GetMapping("/ListaPacientes")
    public String ListarPaciente(Model model,
            @RequestParam(name = "pagina", defaultValue = "1") int pagina,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {

        setUserAttributes(model);

        int totalRegistros = service.contarPacientes();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<Paciente> pacientes = service.listarPacientes(pagina, itemsPerPage);

        // Crear una lista de números de página
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);

        return carpeta + "listaPaciente";
    }

    @GetMapping("/EliminarPaciente")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EliminarPaciente(@RequestParam("codP") String cod, Model model) {
        setUserAttributes(model);
        service.Eliminar(cod);
        return ListarPaciente(model, 1, 10); // Paginación por defecto
    }

    @GetMapping("/EditarPaciente")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EditarPaciente(@RequestParam("codP") String cod, Model model) {
        setUserAttributes(model);

        Optional<Paciente> pac = service.ConsultarId(cod);
        model.addAttribute("paciente", pac);
        return carpeta + "editarPaciente";
    }

    @PostMapping("/ActualizarPaciente")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String ActualizarPaciente(@RequestParam("codigo") String cod,
            @RequestParam("nombre") String nom,
            @RequestParam("apellido") String ape,
            @RequestParam("dni") String dni,
            @RequestParam("celular") String cel,
            @RequestParam("correo") String correo,
            Model model) {

        setUserAttributes(model);

        Paciente p = new Paciente();
        p.setCodigo(cod);
        p.setNombre(nom);
        p.setApellido(ape);
        p.setDni(dni);
        p.setCelular(cel);
        p.setCorreo(correo);

        service.Guardar(p);
        return ListarPaciente(model, 1, 10); // Paginación por defecto
    }

    @PostMapping("/BuscarPaciente")
    public String BuscarPaciente(@RequestParam("desc") String desc, Model model) {
        setUserAttributes(model);

        List<Paciente> pacientes = service.Buscar(desc);
        model.addAttribute("pacientes", pacientes);
        return carpeta + "listaPaciente";
    }
}

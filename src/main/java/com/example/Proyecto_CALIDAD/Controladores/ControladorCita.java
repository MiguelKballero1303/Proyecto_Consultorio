package com.example.Proyecto_CALIDAD.Controladores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

import com.example.Proyecto_CALIDAD.Clases.Cita;
import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import com.example.Proyecto_CALIDAD.Interfaces.ICitaService;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import com.example.Proyecto_CALIDAD.Servicios.EmailService;
import com.example.Proyecto_CALIDAD.Servicios.GoogleCalendarService;
import com.google.api.services.calendar.model.EventAttendee;

@RequestMapping("/Cita/")
@Controller
public class ControladorCita {

    String carpeta = "Cita/";

    @Autowired
    ICitaService service;

    @Autowired
    IPacienteService pacienteService;

    @Autowired
    IProfesionalSaludService profesionalSaludService;

    @Autowired
    IUsuarioService service_u;

    @Autowired
    private GoogleCalendarService googleCalendarService;


    @Autowired
    private EmailService emailService;

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

    @GetMapping("/NuevaCita")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String NuevaCita(Model model) {
        setUserAttributes(model);
        model.addAttribute("pacientes", pacienteService.Listar());
        model.addAttribute("profesionales", profesionalSaludService.Listar());
        return carpeta + "nuevaCita";
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

    @PostMapping("/RegistrarCita")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String RegistrarCita(
            @RequestParam("idPaciente") String idPaciente,
            @RequestParam("idProfesional") String idProfesional,
            @RequestParam("fechaHora") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date fechaHora,
            @RequestParam("motivo") String motivo,
            @RequestParam("estado") String estado,
            Model model) {

        setUserAttributes(model);

        Cita cita = new Cita();
        cita.setId(generateRandomID());
        cita.setPaciente(pacienteService.ConsultarId(idPaciente).orElse(null));
        cita.setProfesionalSalud(profesionalSaludService.ConsultarId(idProfesional).orElse(null));
        cita.setFechaHora(fechaHora);
        cita.setMotivo(motivo);
        cita.setEstado(estado);

        service.Guardar(cita);

        // Enviar correos electrónicos
        Paciente paciente = pacienteService.ConsultarId(idPaciente).orElse(null);
        ProfesionalSalud profesional = profesionalSaludService.ConsultarId(idProfesional).orElse(null);

        if (paciente != null && paciente.getCorreo() != null) {
            emailService.sendEmail(
                    paciente.getCorreo(),
                    "Confirmación de Cita",
                    "Su cita ha sido registrada exitosamente. Detalles:\n\n" +
                            "Fecha y Hora: " + fechaHora.toString() + "\n" +
                            "Motivo: " + motivo + "\n" +
                            "Estado: " + estado + "\n\n" +
                            "Gracias,\nEquipo de Soporte");
        }

        if (profesional != null && profesional.getCorreo() != null) {
            emailService.sendEmail(
                    profesional.getCorreo(),
                    "Nueva Cita Asignada",
                    "Una nueva cita ha sido asignada a usted. Detalles:\n\n" +
                            "Paciente: " + paciente.getNombre() + "\n" +
                            "Fecha y Hora: " + fechaHora.toString() + "\n" +
                            "Motivo: " + motivo + "\n" +
                            "Estado: " + estado + "\n\n" +
                            "Gracias,\nEquipo de Soporte");
        }
        try {
            List<EventAttendee> attendees = new ArrayList<>();
            if (paciente.getCorreo() != null) {
                attendees.add(new EventAttendee().setEmail(paciente.getCorreo()));
            }
            if (profesional.getCorreo() != null) {
                attendees.add(new EventAttendee().setEmail(profesional.getCorreo()));
            }

            if (!attendees.isEmpty()) {
                String googleMeetLink = googleCalendarService.createGoogleMeetEvent(
                        "Cita Médica: " + motivo,
                        "Online",
                        "Cita médica entre " + paciente.getNombre() + " y " + profesional.getNombre(),
                        fechaHora,
                        new Date(fechaHora.getTime() + 3600000), // Duración de la cita (1 hora)
                        attendees);

                // Enviar correo con el link de Google Meet
                if (paciente.getCorreo() != null) {
                    emailService.sendEmail(
                            paciente.getCorreo(),
                            "Enlace de Google Meet para su Cita",
                            "Su cita ha sido registrada exitosamente. Detalles:\n\n" +
                                    "Fecha y Hora: " + fechaHora.toString() + "\n" +
                                    "Motivo: " + motivo + "\n" +
                                    "Estado: " + estado + "\n" +
                                    "Enlace de Google Meet: " + googleMeetLink + "\n\n" +
                                    "Gracias,\nEquipo de Soporte");
                }

                if (profesional.getCorreo() != null) {
                    emailService.sendEmail(
                            profesional.getCorreo(),
                            "Enlace de Google Meet para su Cita",
                            "Una nueva cita ha sido asignada a usted. Detalles:\n\n" +
                                    "Paciente: " + paciente.getNombre() + "\n" +
                                    "Fecha y Hora: " + fechaHora.toString() + "\n" +
                                    "Motivo: " + motivo + "\n" +
                                    "Estado: " + estado + "\n" +
                                    "Enlace de Google Meet: " + googleMeetLink + "\n\n" +
                                    "Gracias,\nEquipo de Soporte");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al crear el evento de Google Meet.");
            return "errorPage";
        }
        return ListarCitas(model, 1, 10);
    }

    @GetMapping("/ListaCitas")
    public String ListarCitas(Model model, @RequestParam(name = "pagina", defaultValue = "1") int pagina,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {
        setUserAttributes(model);
        int totalRegistros = service.contarCitas();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<Cita> citas = service.listarCitas(pagina, itemsPerPage);

        // Crear una lista de números de página
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }
        model.addAttribute("citas", citas);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);
        return carpeta + "listaCita";
    }

    @GetMapping("/EliminarCita")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EliminarCita(@RequestParam("idCita") String id, Model model) {
        setUserAttributes(model);
        service.Eliminar(id);
        return ListarCitas(model, 1, 10);
    }

    @GetMapping("/EditarCita")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EditarCita(@RequestParam("idCita") String id, Model model) {
        setUserAttributes(model);
        Optional<Cita> cita = service.ConsultarId(id);
        if (cita.isPresent()) {
            model.addAttribute("cita", cita.get());
        }
        model.addAttribute("pacientes", pacienteService.Listar());
        model.addAttribute("profesionales", profesionalSaludService.Listar());
        return carpeta + "editarCita";
    }

    @PostMapping("/ActualizarCita")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String ActualizarCita(@RequestParam("id") String id,
            @RequestParam("idPaciente") String idPaciente,
            @RequestParam("idProfesional") String idProfesional,
            @RequestParam("fechaHora") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") Date fechaHora,
            @RequestParam("motivo") String motivo,
            @RequestParam("estado") String estado,
            Model model) {

        setUserAttributes(model);

        Cita cita = new Cita();
        cita.setId(id);
        cita.setPaciente(pacienteService.ConsultarId(idPaciente).orElse(null));
        cita.setProfesionalSalud(profesionalSaludService.ConsultarId(idProfesional).orElse(null));
        cita.setFechaHora(fechaHora);
        cita.setMotivo(motivo);
        cita.setEstado(estado);

        service.Guardar(cita);
        return ListarCitas(model, 1, 10);
    }

    @PostMapping("/BuscarCita")
    public String BuscarCita(@RequestParam("desc") String desc, Model model) {
        setUserAttributes(model);
        List<Cita> resultados = service.Buscar(desc);
        model.addAttribute("citas", resultados);
        return carpeta + "listaCita";
    }
}
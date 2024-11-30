package com.example.Proyecto_CALIDAD.Controladores;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.example.Proyecto_CALIDAD.Clases.HistoriaClinica;
import com.example.Proyecto_CALIDAD.Interfaces.IHistoriaClinicaService;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Interfaces.ITratamientoService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/HistoriaClinica/")
@Controller
public class ControladorHistoriaClinica {

    String carpeta = "HistoriaClinica/";

    @Autowired
    IHistoriaClinicaService service;

    @Autowired
    IPacienteService pacienteService;

    @Autowired
    IProfesionalSaludService profesionalSaludService;

    @Autowired
    ITratamientoService tratamientoService; // Añadido para manejar tratamientos

    @Autowired
    IUsuarioService service_u;

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

        String nombreImagen = service_u.obtenerNombreImagenUsuarioConectado();
        model.addAttribute("nombreImagen", nombreImagen);
        model.addAttribute("username", username);
        model.addAttribute("userRole", userRole);
    }

    @GetMapping("/NuevaHistoriaClinica")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String NuevaHistoriaClinica(Model model) {
        setUserDetails(model);
        model.addAttribute("pacientes", pacienteService.Listar());
        model.addAttribute("profesionales", profesionalSaludService.Listar());
        model.addAttribute("tratamientos", tratamientoService.Listar()); // Añadido para tratamientos
        return carpeta + "nuevaHistoriaClinica";
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

    @PostMapping("/RegistrarHistoriaClinica")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String RegistrarHistoriaClinica(
            @RequestParam("idPaciente") String idPaciente,
            @RequestParam("idProfesional") String idProfesional,
            @RequestParam("idTratamiento") String idTratamiento, // Cambiado a idTratamiento
            @RequestParam("fechaCreacion") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaCreacion,
            @RequestParam("notasProfesional") String notasProfesional,
            @RequestParam("diagnostico") String diagnostico,
            @RequestParam("observaciones") String observaciones,
            @RequestParam("planSeguimiento") String planSeguimiento,
            Model model) {

        setUserDetails(model);

        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setId(generateRandomID());
        historiaClinica.setPaciente(pacienteService.ConsultarId(idPaciente).orElse(null));
        historiaClinica.setProfesionalSalud(profesionalSaludService.ConsultarId(idProfesional).orElse(null));
        historiaClinica.setTratamiento(tratamientoService.ConsultarId(idTratamiento).orElse(null)); // Añadido
        historiaClinica.setFechaCreacion(fechaCreacion);
        historiaClinica.setNotasProfesional(notasProfesional);
        historiaClinica.setDiagnostico(diagnostico);
        historiaClinica.setObservaciones(observaciones);
        historiaClinica.setPlanSeguimiento(planSeguimiento);

        service.Guardar(historiaClinica);
        return ListarHistoriasClinicas(model, 1, 10);
    }

    @GetMapping("/ListaHistoriasClinicas")
    public String ListarHistoriasClinicas(Model model, @RequestParam(name = "pagina", defaultValue = "1") int pagina,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {
        setUserDetails(model);
        int totalRegistros = service.contarHistorias();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<HistoriaClinica> historiasClinicas = service.listarHistorias(pagina, itemsPerPage);

        // Crear una lista de números de página
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }
        model.addAttribute("historiasClinicas", historiasClinicas);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);
        return carpeta + "listaHistoriaClinica";
    }

    @GetMapping("/EliminarHistoriaClinica")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EliminarHistoriaClinica(@RequestParam("idHistoriaClinica") String id, Model model) {
        setUserDetails(model);
        service.Eliminar(id);
        return ListarHistoriasClinicas(model, 1, 10);
    }

    @GetMapping("/EditarHistoriaClinica")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EditarHistoriaClinica(@RequestParam("idHistoriaClinica") String id, Model model) {
        setUserDetails(model);
        Optional<HistoriaClinica> historiaClinica = service.ConsultarId(id);
        if (historiaClinica.isPresent()) {
            model.addAttribute("historiaClinica", historiaClinica.get());
        }
        model.addAttribute("pacientes", pacienteService.Listar());
        model.addAttribute("profesionales", profesionalSaludService.Listar());
        model.addAttribute("tratamientos", tratamientoService.Listar()); // Añadido para tratamientos
        return carpeta + "editarHistoriaClinica";
    }

    @PostMapping("/ActualizarHistoriaClinica")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String ActualizarHistoriaClinica(@RequestParam("id") String id,
            @RequestParam("idPaciente") String idPaciente,
            @RequestParam("idProfesional") String idProfesional,
            @RequestParam("idTratamiento") String idTratamiento, // Cambiado a idTratamiento
            @RequestParam("fechaCreacion") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaCreacion,
            @RequestParam("notasProfesional") String notasProfesional,
            @RequestParam("diagnostico") String diagnostico,
            @RequestParam("observaciones") String observaciones,
            @RequestParam("planSeguimiento") String planSeguimiento,
            Model model) {

        setUserDetails(model);

        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setId(id);
        historiaClinica.setPaciente(pacienteService.ConsultarId(idPaciente).orElse(null));
        historiaClinica.setProfesionalSalud(profesionalSaludService.ConsultarId(idProfesional).orElse(null));
        historiaClinica.setTratamiento(tratamientoService.ConsultarId(idTratamiento).orElse(null)); // Añadido
        historiaClinica.setFechaCreacion(fechaCreacion);
        historiaClinica.setNotasProfesional(notasProfesional);
        historiaClinica.setDiagnostico(diagnostico);
        historiaClinica.setObservaciones(observaciones);
        historiaClinica.setPlanSeguimiento(planSeguimiento);

        service.Guardar(historiaClinica);
        return ListarHistoriasClinicas(model, 1, 10);
    }

    @PostMapping("/BuscarHistoriaClinica")
    public String BuscarHistoriaClinica(@RequestParam("desc") String desc, Model model) {
        setUserDetails(model);
        List<HistoriaClinica> historiasClinicas = service.Buscar(desc);
        model.addAttribute("historiasClinicas", historiasClinicas);
        return carpeta + "listaHistoriaClinica";
    }

    @GetMapping("/DescargarPDF")
    public void descargarHistoriaClinicaPDF(@RequestParam("idHistoriaClinica") String id,
            HttpServletResponse response) {
        try {
            Optional<HistoriaClinica> historiaClinicaOpt = service.ConsultarId(id);
            if (historiaClinicaOpt.isPresent()) {
                HistoriaClinica historiaClinica = historiaClinicaOpt.get();

                // Prepara el documento PDF
                Document document = new Document();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, baos);
                document.open();

                // Crea el modelo para pasar al método buildPdfDocument
                Map<String, Object> model = new HashMap<>();
                model.put("historiaClinica", historiaClinica);

                // Llama al método buildPdfDocument con el nuevo parámetro
                new HistoriaClinicaPdf().buildPdfDocument(model, document, writer, null, response);

                document.close();
                response.setHeader("Content-Disposition", "attachment; filename=historia_clinica_" + id + ".pdf");
                response.setContentType("application/pdf");
                ServletOutputStream outputStream = response.getOutputStream();
                baos.writeTo(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Clase interna para generar el PDF
    @Component
    public class HistoriaClinicaPdf extends AbstractPdfView {

        @Override
        protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                HttpServletRequest request, HttpServletResponse response) throws Exception {
            // Obtén el objeto HistoriaClinica del modelo
            HistoriaClinica historiaClinica = (HistoriaClinica) model.get("historiaClinica");

            // Define fuentes
            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font fontSubtitulo = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font fontTexto = new Font(Font.HELVETICA, 12);
            Font fontNegrita = new Font(Font.HELVETICA, 12, Font.BOLD); // Fuente en negrita

            // Agrega el título
            Paragraph titulo = new Paragraph("Historia Clínica", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            // Agrega los detalles
            document.add(new Paragraph("Paciente:", fontNegrita));
            document.add(new Paragraph("Nombre: " + historiaClinica.getPaciente().getNombre(), fontTexto));
            document.add(new Paragraph("Apellido: " + historiaClinica.getPaciente().getApellido(), fontTexto));
            document.add(new Paragraph("DNI: " + historiaClinica.getPaciente().getDni(), fontTexto));

            document.add(new Paragraph("\nProfesional:", fontNegrita));
            document.add(new Paragraph("Nombre: " + historiaClinica.getProfesionalSalud().getNombre(), fontTexto));
            document.add(new Paragraph("Apellido: " + historiaClinica.getProfesionalSalud().getApellido(), fontTexto));

            document.add(new Paragraph("\nFecha de Creación:", fontNegrita));
            document.add(new Paragraph(historiaClinica.getFechaCreacion().toString(), fontTexto));

            document.add(new Paragraph("\nDiagnóstico:", fontNegrita));
            document.add(new Paragraph(historiaClinica.getDiagnostico(), fontTexto));

            document.add(new Paragraph("\nTratamiento:", fontNegrita));
            document.add(new Paragraph(historiaClinica.getTratamiento().getNombreTratamiento(), fontTexto));

            document.add(new Paragraph("\nObservaciones:", fontNegrita));
            document.add(new Paragraph(historiaClinica.getObservaciones(), fontTexto));

            document.add(new Paragraph("\nPlan de Seguimiento:", fontNegrita));
            document.add(new Paragraph(historiaClinica.getPlanSeguimiento(), fontTexto));

            document.add(new Paragraph("\nNotas del Profesional:", fontNegrita));
            document.add(new Paragraph(historiaClinica.getNotasProfesional(), fontTexto));
        }
    }

}

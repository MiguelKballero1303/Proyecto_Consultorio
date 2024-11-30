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

import com.example.Proyecto_CALIDAD.Clases.Factura;
import com.example.Proyecto_CALIDAD.Interfaces.ICitaService;
import com.example.Proyecto_CALIDAD.Interfaces.IFacturaService;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/Factura/")
@Controller
public class ControladorFactura {

    String carpeta = "Factura/";

    @Autowired
    IFacturaService service;

    @Autowired
    IPacienteService pacienteService;

    @Autowired
    ICitaService citaService;

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

    @GetMapping("/NuevaFactura")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String NuevaFactura(Model model) {
        setUserDetails(model);
        model.addAttribute("pacientes", pacienteService.Listar());
        model.addAttribute("citas", citaService.Listar());
        return carpeta + "nuevaFactura";
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

    @PostMapping("/RegistrarFactura")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String RegistrarFactura(
            @RequestParam("idPaciente") String idPaciente,
            @RequestParam("idCita") String idCita,
            @RequestParam("montoTotal") Double montoTotal,
            @RequestParam("detallesServicios") String detallesServicios,
            @RequestParam("fechaEmision") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaEmision,
            @RequestParam("estadoPago") String estadoPago,
            Model model) {

                setUserDetails(model);

        Factura factura = new Factura();
        factura.setId(generateRandomID());
        factura.setPaciente(pacienteService.ConsultarId(idPaciente).orElse(null));
        factura.setCita(citaService.ConsultarId(idCita).orElse(null));
        factura.setMontoTotal(montoTotal);
        factura.setDetallesServicios(detallesServicios);
        factura.setFechaEmision(fechaEmision);
        factura.setEstadoPago(estadoPago);

        service.Guardar(factura);
        return ListarFacturas(model, 1, 10);
    }

    @GetMapping("/ListaFacturas")
    public String ListarFacturas(Model model, @RequestParam(name = "pagina", defaultValue = "1") int pagina,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") int itemsPerPage) {
                setUserDetails(model);

        int totalRegistros = service.contarFacturas();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / itemsPerPage);
        List<Factura> facturas = service.listarFacturas(pagina, itemsPerPage);
        List<Integer> numeroPaginas = new ArrayList<>();
        for (int i = 1; i <= totalPaginas; i++) {
            numeroPaginas.add(i);
        }
        model.addAttribute("facturas", facturas);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("numeroPaginas", numeroPaginas);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("itemsPerPage", itemsPerPage);
        return carpeta + "listaFactura";
    }

    @GetMapping("/EliminarFactura")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EliminarFactura(@RequestParam("idFactura") String id, Model model) {
        setUserDetails(model);
        service.Eliminar(id);
        return ListarFacturas(model, 1, 10);
    }

    @GetMapping("/EditarFactura")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String EditarFactura(@RequestParam("idFactura") String id, Model model) {
        setUserDetails(model);
        Optional<Factura> factura = service.ConsultarId(id);
        if (factura.isPresent()) {
            model.addAttribute("factura", factura.get());
        }
        model.addAttribute("pacientes", pacienteService.Listar());
        model.addAttribute("citas", citaService.Listar());
        return carpeta + "editarFactura";
    }

    @PostMapping("/ActualizarFactura")
    @PreAuthorize("hasAuthority('ROL_ADMIN') or hasAuthority('ROL_ENC')")
    public String ActualizarFactura(@RequestParam("id") String id,
            @RequestParam("idPaciente") String idPaciente,
            @RequestParam("idCita") String idCita,
            @RequestParam("montoTotal") Double montoTotal,
            @RequestParam("detallesServicios") String detallesServicios,
            @RequestParam("fechaEmision") @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaEmision,
            @RequestParam("estadoPago") String estadoPago,
            Model model) {

                setUserDetails(model);

        Factura factura = new Factura();
        factura.setId(id);
        factura.setPaciente(pacienteService.ConsultarId(idPaciente).orElse(null));
        factura.setCita(citaService.ConsultarId(idCita).orElse(null));
        factura.setMontoTotal(montoTotal);
        factura.setDetallesServicios(detallesServicios);
        factura.setFechaEmision(fechaEmision);
        factura.setEstadoPago(estadoPago);

        service.Guardar(factura);
        return ListarFacturas(model, 1, 10);
    }

    @PostMapping("/BuscarFactura")
    public String BuscarFactura(@RequestParam("desc") String desc, Model model) {
        setUserDetails(model);
        List<Factura> resultados = service.Buscar(desc);
        model.addAttribute("facturas", resultados);
        return carpeta + "listaFactura";
    }

    @GetMapping("/DescargarPDF")
    public void descargarFacturaPDF(@RequestParam("idFactura") String id, HttpServletResponse response) {
        try {
            Optional<Factura> facturaOpt = service.ConsultarId(id);
            if (facturaOpt.isPresent()) {
                Factura factura = facturaOpt.get();

                // Prepara el documento PDF
                Document document = new Document();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PdfWriter writer = PdfWriter.getInstance(document, baos);
                document.open();

                // Crea el modelo para pasar al método buildPdfDocument
                Map<String, Object> model = new HashMap<>();
                model.put("factura", factura);

                // Llama al método buildPdfDocument con el nuevo parámetro
                new FacturaPdf().buildPdfDocument(model, document, writer, null, response);

                document.close();
                response.setHeader("Content-Disposition", "attachment; filename=factura_" + id + ".pdf");
                response.setContentType("application/pdf");
                ServletOutputStream outputStream = response.getOutputStream();
                baos.writeTo(outputStream);
                outputStream.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Component
    public class FacturaPdf extends AbstractPdfView {

        @Override
        protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
                HttpServletRequest request, HttpServletResponse response) throws Exception {
            // Obtén el objeto Factura del modelo
            Factura factura = (Factura) model.get("factura");

            // Define fuentes
            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font fontSubtitulo = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font fontTexto = new Font(Font.HELVETICA, 12);
            Font fontNegrita = new Font(Font.HELVETICA, 12, Font.BOLD);

            // Agrega el título
            Paragraph titulo = new Paragraph("Factura", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph("\n"));

            // Agrega los detalles de la factura
            document.add(new Paragraph("Número de Factura:", fontNegrita));
            document.add(new Paragraph(factura.getId(), fontTexto));

            document.add(new Paragraph("\nPaciente:", fontNegrita));
            document.add(new Paragraph("Nombre: " + factura.getPaciente().getNombre(), fontTexto));
            document.add(new Paragraph("Apellido: " + factura.getPaciente().getApellido(), fontTexto));
            document.add(new Paragraph("DNI: " + factura.getPaciente().getDni(), fontTexto));

            document.add(new Paragraph("\nCita:", fontNegrita));
            document.add(new Paragraph("Fecha de la Cita: " + factura.getCita().getFechaHora().toString(), fontTexto));

            document.add(new Paragraph("\nDetalles del Servicio:", fontNegrita));
            document.add(new Paragraph(factura.getDetallesServicios(), fontTexto));

            document.add(new Paragraph("\nMonto Total:", fontNegrita));
            document.add(new Paragraph("S/. " + factura.getMontoTotal(), fontTexto));

            document.add(new Paragraph("\nFecha de Emisión:", fontNegrita));
            document.add(new Paragraph(factura.getFechaEmision().toString(), fontTexto));

            document.add(new Paragraph("\nEstado de Pago:", fontNegrita));
            document.add(new Paragraph(factura.getEstadoPago(), fontTexto));
        }
    }

}

package com.example.Proyecto_CALIDAD.Controladores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.example.Proyecto_CALIDAD.Clases.Cita;
import com.example.Proyecto_CALIDAD.Clases.Factura;
import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Interfaces.ICitaService;
import com.example.Proyecto_CALIDAD.Interfaces.IFacturaService;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

public class ControladorFacturaTest {

    @InjectMocks
    private ControladorFactura controladorFactura;

    @Mock
    private IFacturaService facturaServiceMock;

    @Mock
    private IPacienteService pacienteServiceMock;

    @Mock
    private ICitaService citaServiceMock;

    @Mock
    private IUsuarioService usuarioServiceMock;

    @Mock
    private Model modelMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNuevaFactura() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(pacienteServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(citaServiceMock.Listar()).thenReturn(new ArrayList<>());

        String resultado = controladorFactura.NuevaFactura(modelMock);
        assertEquals("Factura/nuevaFactura", resultado);
    }

    @Test
    public void testRegistrarFactura() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idPaciente = "123";
        String idCita = "456";
        Double montoTotal = 100.0;
        String detallesServicios = "Consulta";
        Date fechaEmision = new Date();
        String estadoPago = "Pendiente";

        Paciente paciente = new Paciente();
        paciente.setNombre("Paciente");

        Cita cita = new Cita();
        cita.setFechaHora(new Date());

        when(pacienteServiceMock.ConsultarId(idPaciente)).thenReturn(Optional.of(paciente));
        when(citaServiceMock.ConsultarId(idCita)).thenReturn(Optional.of(cita));

        String resultado = controladorFactura.RegistrarFactura(idPaciente, idCita, montoTotal, detallesServicios, fechaEmision, estadoPago, modelMock);

        ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
        verify(facturaServiceMock).Guardar(facturaCaptor.capture());
        Factura facturaGuardada = facturaCaptor.getValue();

        assertEquals(montoTotal, facturaGuardada.getMontoTotal());
        assertEquals(detallesServicios, facturaGuardada.getDetallesServicios());
        assertEquals(fechaEmision, facturaGuardada.getFechaEmision());
        assertEquals(estadoPago, facturaGuardada.getEstadoPago());
        assertEquals("Factura/listaFactura", resultado);
    }

    @Test
    public void testListarFacturas() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(facturaServiceMock.contarFacturas()).thenReturn(10);
        when(facturaServiceMock.listarFacturas(1, 10)).thenReturn(new ArrayList<>());

        String resultado = controladorFactura.ListarFacturas(modelMock, 1, 10);
        assertEquals("Factura/listaFactura", resultado);
    }

    @Test
    public void testEliminarFactura() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idFactura = "123456";
        String resultado = controladorFactura.EliminarFactura(idFactura, modelMock);

        verify(facturaServiceMock).Eliminar(idFactura);
        assertEquals("Factura/listaFactura", resultado);
    }

    @Test
    public void testEditarFactura() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idFactura = "123456";
        Factura factura = new Factura();
        when(facturaServiceMock.ConsultarId(idFactura)).thenReturn(Optional.of(factura));
        when(pacienteServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(citaServiceMock.Listar()).thenReturn(new ArrayList<>());

        String resultado = controladorFactura.EditarFactura(idFactura, modelMock);
        assertEquals("Factura/editarFactura", resultado);
    }

    @Test
    public void testActualizarFactura() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String id = "123456";
        String idPaciente = "123";
        String idCita = "456";
        Double montoTotal = 100.0;
        String detallesServicios = "Consulta";
        Date fechaEmision = new Date();
        String estadoPago = "Pendiente";

        Paciente paciente = new Paciente();
        paciente.setNombre("Paciente");

        Cita cita = new Cita();
        cita.setFechaHora(new Date());

        when(pacienteServiceMock.ConsultarId(idPaciente)).thenReturn(Optional.of(paciente));
        when(citaServiceMock.ConsultarId(idCita)).thenReturn(Optional.of(cita));

        String resultado = controladorFactura.ActualizarFactura(id, idPaciente, idCita, montoTotal, detallesServicios, fechaEmision, estadoPago, modelMock);

        ArgumentCaptor<Factura> facturaCaptor = ArgumentCaptor.forClass(Factura.class);
        verify(facturaServiceMock).Guardar(facturaCaptor.capture());
        Factura facturaGuardada = facturaCaptor.getValue();

        assertEquals(id, facturaGuardada.getId());
        assertEquals(montoTotal, facturaGuardada.getMontoTotal());
        assertEquals(detallesServicios, facturaGuardada.getDetallesServicios());
        assertEquals(fechaEmision, facturaGuardada.getFechaEmision());
        assertEquals(estadoPago, facturaGuardada.getEstadoPago());
        assertEquals("Factura/listaFactura", resultado);
    }

    @Test
    public void testBuscarFactura() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String descripcion = "Consulta";
        List<Factura> facturasEncontradas = new ArrayList<>();
        Factura factura = new Factura();
        factura.setDetallesServicios("Consulta de prueba");
        facturasEncontradas.add(factura);

        when(facturaServiceMock.Buscar(descripcion)).thenReturn(facturasEncontradas);

        String resultado = controladorFactura.BuscarFactura(descripcion, modelMock);

        verify(modelMock).addAttribute("facturas", facturasEncontradas);
        assertEquals("Factura/listaFactura", resultado);
    }
}

package com.example.Proyecto_CALIDAD.Controladores;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
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
import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import com.example.Proyecto_CALIDAD.Interfaces.ICitaService;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import com.example.Proyecto_CALIDAD.Servicios.EmailService;
import com.example.Proyecto_CALIDAD.Servicios.GoogleCalendarService;

public class ControladorCitaTest {

    @InjectMocks
    private ControladorCita controladorCita;

    @Mock
    private ICitaService citaServiceMock;

    @Mock
    private IPacienteService pacienteServiceMock;

    @Mock
    private IProfesionalSaludService profesionalSaludServiceMock;

    @Mock
    private IUsuarioService usuarioServiceMock;

    @Mock
    private GoogleCalendarService googleCalendarServiceMock;


    @Mock
    private EmailService emailServiceMock;

    @Mock
    private Model modelMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNuevaCita() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(pacienteServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(profesionalSaludServiceMock.Listar()).thenReturn(new ArrayList<>());

        String resultado = controladorCita.NuevaCita(modelMock);
        assertEquals("Cita/nuevaCita", resultado);
    }

    @Test
    public void testRegistrarCita() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idPaciente = "123";
        String idProfesional = "456";
        Date fechaHora = new Date();
        String motivo = "Consulta";
        String estado = "Pendiente";

        Paciente paciente = new Paciente();
        paciente.setNombre("Paciente");
        paciente.setCorreo("paciente@example.com");

        ProfesionalSalud profesional = new ProfesionalSalud();
        profesional.setNombre("Profesional");
        profesional.setCorreo("profesional@example.com");

        when(pacienteServiceMock.ConsultarId(idPaciente)).thenReturn(Optional.of(paciente));
        when(profesionalSaludServiceMock.ConsultarId(idProfesional)).thenReturn(Optional.of(profesional));

        String resultado = controladorCita.RegistrarCita(idPaciente, idProfesional, fechaHora, motivo, estado, modelMock);

        ArgumentCaptor<Cita> citaCaptor = ArgumentCaptor.forClass(Cita.class);
        verify(citaServiceMock).Guardar(citaCaptor.capture());
        Cita citaGuardada = citaCaptor.getValue();

        assertEquals(motivo, citaGuardada.getMotivo());
        assertEquals(fechaHora, citaGuardada.getFechaHora());
        assertEquals("Cita/listaCita", resultado);
    }

    @Test
    public void testListarCitas() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(citaServiceMock.contarCitas()).thenReturn(10);
        when(citaServiceMock.listarCitas(1, 10)).thenReturn(new ArrayList<>());

        String resultado = controladorCita.ListarCitas(modelMock, 1, 10);
        assertEquals("Cita/listaCita", resultado);
    }

    @Test
    public void testEliminarCita() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idCita = "123456";
        String resultado = controladorCita.EliminarCita(idCita, modelMock);

        verify(citaServiceMock).Eliminar(idCita);
        assertEquals("Cita/listaCita", resultado);
    }

    @Test
    public void testEditarCita() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idCita = "123456";
        Cita cita = new Cita();
        when(citaServiceMock.ConsultarId(idCita)).thenReturn(Optional.of(cita));
        when(pacienteServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(profesionalSaludServiceMock.Listar()).thenReturn(new ArrayList<>());

        String resultado = controladorCita.EditarCita(idCita, modelMock);
        assertEquals("Cita/editarCita", resultado);
    }

    @Test
    public void testActualizarCita() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idCita = "123456";
        String idPaciente = "123";
        String idProfesional = "456";
        Date fechaHora = new Date();
        String motivo = "Consulta";
        String estado = "Pendiente";

        Paciente paciente = new Paciente();
        paciente.setNombre("Paciente");
        ProfesionalSalud profesional = new ProfesionalSalud();
        profesional.setNombre("Profesional");

        when(pacienteServiceMock.ConsultarId(idPaciente)).thenReturn(Optional.of(paciente));
        when(profesionalSaludServiceMock.ConsultarId(idProfesional)).thenReturn(Optional.of(profesional));

        String resultado = controladorCita.ActualizarCita(idCita, idPaciente, idProfesional, fechaHora, motivo, estado, modelMock);

        verify(citaServiceMock).Guardar(any(Cita.class));
        assertEquals("Cita/listaCita", resultado);
    }
    @Test
    public void testBuscarCita() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        String descripcion = "Consulta";
        List<Cita> citasEncontradas = new ArrayList<>();
        Cita cita = new Cita();
        cita.setMotivo("Consulta de prueba");
        citasEncontradas.add(cita);
        when(citaServiceMock.Buscar(descripcion)).thenReturn(citasEncontradas);
        String resultado = controladorCita.BuscarCita(descripcion, modelMock);
        verify(modelMock).addAttribute("citas", citasEncontradas);
        assertEquals("Cita/listaCita", resultado);
    }

}

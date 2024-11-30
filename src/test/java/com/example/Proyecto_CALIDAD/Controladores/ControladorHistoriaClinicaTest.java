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

import com.example.Proyecto_CALIDAD.Clases.HistoriaClinica;
import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import com.example.Proyecto_CALIDAD.Interfaces.IHistoriaClinicaService;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Interfaces.ITratamientoService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

public class ControladorHistoriaClinicaTest {

    @InjectMocks
    private ControladorHistoriaClinica controladorHistoriaClinica;

    @Mock
    private IHistoriaClinicaService historiaClinicaServiceMock;

    @Mock
    private IPacienteService pacienteServiceMock;

    @Mock
    private IProfesionalSaludService profesionalSaludServiceMock;

    @Mock
    private ITratamientoService tratamientoServiceMock;

    @Mock
    private IUsuarioService usuarioServiceMock;

    @Mock
    private Model modelMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNuevaHistoriaClinica() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(pacienteServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(profesionalSaludServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(tratamientoServiceMock.Listar()).thenReturn(new ArrayList<>());

        String resultado = controladorHistoriaClinica.NuevaHistoriaClinica(modelMock);
        assertEquals("HistoriaClinica/nuevaHistoriaClinica", resultado);
    }

    @Test
    public void testRegistrarHistoriaClinica() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idPaciente = "123";
        String idProfesional = "456";
        String idTratamiento = "789";
        Date fechaCreacion = new Date();
        String notasProfesional = "Notas";
        String diagnostico = "Diagnóstico";
        String observaciones = "Observaciones";
        String planSeguimiento = "Plan";

        Paciente paciente = new Paciente();
        paciente.setNombre("Paciente");
        ProfesionalSalud profesional = new ProfesionalSalud();
        profesional.setNombre("Profesional");
        Tratamiento tratamiento = new Tratamiento();

        when(pacienteServiceMock.ConsultarId(idPaciente)).thenReturn(Optional.of(paciente));
        when(profesionalSaludServiceMock.ConsultarId(idProfesional)).thenReturn(Optional.of(profesional));
        when(tratamientoServiceMock.ConsultarId(idTratamiento)).thenReturn(Optional.of(tratamiento));

        String resultado = controladorHistoriaClinica.RegistrarHistoriaClinica(
                idPaciente, idProfesional, idTratamiento, fechaCreacion,
                notasProfesional, diagnostico, observaciones, planSeguimiento, modelMock);

        ArgumentCaptor<HistoriaClinica> historiaCaptor = ArgumentCaptor.forClass(HistoriaClinica.class);
        verify(historiaClinicaServiceMock).Guardar(historiaCaptor.capture());
        HistoriaClinica historiaGuardada = historiaCaptor.getValue();

        assertEquals(notasProfesional, historiaGuardada.getNotasProfesional());
        assertEquals(fechaCreacion, historiaGuardada.getFechaCreacion());
        assertEquals("HistoriaClinica/listaHistoriaClinica", resultado);
    }

    @Test
    public void testListarHistoriasClinicas() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(historiaClinicaServiceMock.contarHistorias()).thenReturn(10);
        when(historiaClinicaServiceMock.listarHistorias(1, 10)).thenReturn(new ArrayList<>());

        String resultado = controladorHistoriaClinica.ListarHistoriasClinicas(modelMock, 1, 10);
        assertEquals("HistoriaClinica/listaHistoriaClinica", resultado);
    }

    @Test
    public void testEliminarHistoriaClinica() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idHistoriaClinica = "123456";
        String resultado = controladorHistoriaClinica.EliminarHistoriaClinica(idHistoriaClinica, modelMock);

        verify(historiaClinicaServiceMock).Eliminar(idHistoriaClinica);
        assertEquals("HistoriaClinica/listaHistoriaClinica", resultado);
    }

    @Test
    public void testEditarHistoriaClinica() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idHistoriaClinica = "123456";
        HistoriaClinica historiaClinica = new HistoriaClinica();
        when(historiaClinicaServiceMock.ConsultarId(idHistoriaClinica)).thenReturn(Optional.of(historiaClinica));
        when(pacienteServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(profesionalSaludServiceMock.Listar()).thenReturn(new ArrayList<>());
        when(tratamientoServiceMock.Listar()).thenReturn(new ArrayList<>());

        String resultado = controladorHistoriaClinica.EditarHistoriaClinica(idHistoriaClinica, modelMock);
        assertEquals("HistoriaClinica/editarHistoriaClinica", resultado);
    }

    @Test
    public void testActualizarHistoriaClinica() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idHistoriaClinica = "123456";
        String idPaciente = "123";
        String idProfesional = "456";
        String idTratamiento = "789";
        Date fechaCreacion = new Date();
        String notasProfesional = "Notas";
        String diagnostico = "Diagnóstico";
        String observaciones = "Observaciones";
        String planSeguimiento = "Plan";

        Paciente paciente = new Paciente();
        ProfesionalSalud profesional = new ProfesionalSalud();
        Tratamiento tratamiento = new Tratamiento();

        when(pacienteServiceMock.ConsultarId(idPaciente)).thenReturn(Optional.of(paciente));
        when(profesionalSaludServiceMock.ConsultarId(idProfesional)).thenReturn(Optional.of(profesional));
        when(tratamientoServiceMock.ConsultarId(idTratamiento)).thenReturn(Optional.of(tratamiento));

        String resultado = controladorHistoriaClinica.ActualizarHistoriaClinica(
                idHistoriaClinica, idPaciente, idProfesional, idTratamiento,
                fechaCreacion, notasProfesional, diagnostico, observaciones,
                planSeguimiento, modelMock);

        verify(historiaClinicaServiceMock).Guardar(any(HistoriaClinica.class));
        assertEquals("HistoriaClinica/listaHistoriaClinica", resultado);
    }

    @Test
    public void testBuscarHistoriaClinica() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        String diagnostico = "TDAH";
        List<HistoriaClinica> historiasClinicasEncontradas = new ArrayList<>();
        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setDiagnostico(diagnostico);
        historiasClinicasEncontradas.add(historiaClinica);
        when(historiaClinicaServiceMock.Buscar(diagnostico)).thenReturn(historiasClinicasEncontradas);
        String resultado = controladorHistoriaClinica.BuscarHistoriaClinica(diagnostico, modelMock);
        verify(modelMock).addAttribute("historiasClinicas", historiasClinicasEncontradas);
        assertEquals("HistoriaClinica/listaHistoriaClinica", resultado);
    }

}

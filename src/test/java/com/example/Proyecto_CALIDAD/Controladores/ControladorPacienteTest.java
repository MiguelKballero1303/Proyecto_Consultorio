package com.example.Proyecto_CALIDAD.Controladores;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import com.example.Proyecto_CALIDAD.Clases.Paciente;
import com.example.Proyecto_CALIDAD.Interfaces.IPacienteService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

public class ControladorPacienteTest {

    @InjectMocks
    private ControladorPaciente controladorPaciente;

    @Mock
    private IPacienteService pacienteServiceMock;

    @Mock
    private IUsuarioService usuarioServiceMock;

    @Mock
    private Model modelMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNuevoPaciente() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        String resultado = controladorPaciente.NuevoPaciente(modelMock);
        assertEquals("Paciente/nuevoPaciente", resultado);
    }

    @Test
    public void testRegistrarPaciente() {
        Model modelMock = mock(Model.class);
        Authentication authMock = mock(Authentication.class);
        when(authMock.getName()).thenReturn("username");
        SecurityContextHolder.getContext().setAuthentication(authMock);
        String nombre = "nombre";
        String apellido = "apellido";
        String dni = "dni";
        String celular = "celular";
        String correo = "correo";
        String resultado = controladorPaciente.RegistrarPaciente(nombre, apellido, dni, celular, correo, modelMock);
        ArgumentCaptor<Paciente> pacienteCaptor = ArgumentCaptor.forClass(Paciente.class);
        verify(pacienteServiceMock).Guardar(pacienteCaptor.capture());
        Paciente pacienteGuardado = pacienteCaptor.getValue();
        assertEquals(nombre, pacienteGuardado.getNombre());
        assertEquals(apellido, pacienteGuardado.getApellido());
        assertEquals(dni, pacienteGuardado.getDni());
        assertEquals(celular, pacienteGuardado.getCelular());
        assertEquals(correo, pacienteGuardado.getCorreo());
        assertEquals("Paciente/listaPaciente", resultado);
    }

    @Test
    public void testListarPaciente() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");
        List<Paciente> listaPacientes = new ArrayList<>();
        listaPacientes.add(new Paciente());
        when(pacienteServiceMock.contarPacientes()).thenReturn(10);
        when(pacienteServiceMock.listarPacientes(1, 10)).thenReturn(listaPacientes);
        String resultado = controladorPaciente.ListarPaciente(modelMock, 1, 10);
        assertEquals("Paciente/listaPaciente", resultado);
    }

    @Test
    public void testEliminarPaciente() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");
        String codigoPaciente = "123456";
        String resultado = controladorPaciente.EliminarPaciente(codigoPaciente, modelMock);
        verify(pacienteServiceMock).Eliminar(codigoPaciente);
        assertEquals("Paciente/listaPaciente", resultado);
    }

    @Test
    public void testEditarPaciente() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");
        String codigoPaciente = "123456";
        Optional<Paciente> pacienteOptional = Optional.of(new Paciente());
        when(pacienteServiceMock.ConsultarId(codigoPaciente)).thenReturn(pacienteOptional);
        String resultado = controladorPaciente.EditarPaciente(codigoPaciente, modelMock);
        assertEquals("Paciente/editarPaciente", resultado);
    }

    @Test
    public void testActualizarPaciente() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");
        Paciente paciente = new Paciente();
        paciente.setCodigo("123456");
        paciente.setNombre("nombre");
        paciente.setApellido("apellido");
        paciente.setDni("dni");
        paciente.setCelular("cel");
        paciente.setCorreo("correo");
        String resultado = controladorPaciente.ActualizarPaciente("123456", "nombre", "apellido", "dni", "cel",
                "correo", modelMock);
        verify(pacienteServiceMock).Guardar(paciente);
        assertEquals("Paciente/listaPaciente", resultado);
    }

    @Test
    public void testBuscarPaciente() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");
        String descripcion = "descripcion";
        List<Paciente> listaPacientes = new ArrayList<>();
        listaPacientes.add(new Paciente());
        when(pacienteServiceMock.Buscar(descripcion)).thenReturn(listaPacientes);
        String resultado = controladorPaciente.BuscarPaciente(descripcion, modelMock);
        assertEquals("Paciente/listaPaciente", resultado);
    }
    @Test
    public void testRegistrarPacienteAutomaticoConDatosCompletos() {
                Map<String, Object> registrationData = new HashMap<>();
                registrationData.put("name", "nombre");
                registrationData.put("last_name", "apellido");
                registrationData.put("dni", "dni");
                registrationData.put("celular", "celular");
                registrationData.put("correo", "correo");
                ResponseEntity<String> response = controladorPaciente.RegistrarPacienteAutomatico(registrationData);
                ArgumentCaptor<Paciente> pacienteCaptor = ArgumentCaptor.forClass(Paciente.class);
                verify(pacienteServiceMock).Guardar(pacienteCaptor.capture());
                Paciente pacienteGuardado = pacienteCaptor.getValue();
                assertEquals("nombre", pacienteGuardado.getNombre());
                assertEquals("apellido", pacienteGuardado.getApellido());
                assertEquals("dni", pacienteGuardado.getDni());
                assertEquals("celular", pacienteGuardado.getCelular());
                assertEquals("correo", pacienteGuardado.getCorreo());
                assertEquals(HttpStatus.OK, response.getStatusCode());
                assertEquals("Paciente registrado exitosamente", response.getBody());
    }

    @Test
    public void testRegistrarPacienteAutomaticoConDatosIncompletos() {
        Map<String, Object> registrationData = new HashMap<>();
        registrationData.put("name", "nombre");
        ResponseEntity<String> response = controladorPaciente.RegistrarPacienteAutomatico(registrationData);
        verify(pacienteServiceMock, never()).Guardar(any(Paciente.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Datos del paciente incompletos", response.getBody());
    }
}

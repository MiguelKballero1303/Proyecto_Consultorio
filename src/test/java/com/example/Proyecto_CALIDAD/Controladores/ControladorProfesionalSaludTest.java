package com.example.Proyecto_CALIDAD.Controladores;

import com.example.Proyecto_CALIDAD.Clases.ProfesionalSalud;
import com.example.Proyecto_CALIDAD.Interfaces.IProfesionalSaludService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ControladorProfesionalSaludTest {

    @InjectMocks
    private ControladorProfesionalSalud controladorProfesionalSalud;

    @Mock
    private IProfesionalSaludService profesionalSaludServiceMock;

    @Mock
    private IUsuarioService usuarioServiceMock;

    @Mock
    private Model modelMock;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testNuevoProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());

        String resultado = controladorProfesionalSalud.NuevoProfesional(modelMock);

        assertEquals("ProfesionalSalud/nuevoProfesional", resultado);
    }

    @Test
    public void testRegistrarProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());

        String nombre = "nombre";
        String apellido = "apellido";
        String especialidad = "especialidad";
        String telefono = "telefono";
        String correo = "correo";
        String horario = "horario";
        String numeroLicencia = "123456";

        String resultado = controladorProfesionalSalud.RegistrarProfesional(nombre, apellido, especialidad, telefono, correo, horario, numeroLicencia, modelMock);

        ArgumentCaptor<ProfesionalSalud> profesionalSaludCaptor = ArgumentCaptor.forClass(ProfesionalSalud.class);
        verify(profesionalSaludServiceMock).Guardar(profesionalSaludCaptor.capture());

        ProfesionalSalud profesionalSaludGuardado = profesionalSaludCaptor.getValue();

        assertEquals(nombre, profesionalSaludGuardado.getNombre());
        assertEquals(apellido, profesionalSaludGuardado.getApellido());
        assertEquals(especialidad, profesionalSaludGuardado.getEspecialidad());
        assertEquals(telefono, profesionalSaludGuardado.getTelefono());
        assertEquals(correo, profesionalSaludGuardado.getCorreo());
        assertEquals(horario, profesionalSaludGuardado.getHorario());
        assertEquals(numeroLicencia, profesionalSaludGuardado.getNumeroLicencia());

        assertEquals("ProfesionalSalud/listaProfesional", resultado);
    }

    @Test
    public void testListarProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");

        List<ProfesionalSalud> listaProfesionales = new ArrayList<>();
        listaProfesionales.add(new ProfesionalSalud());

        when(profesionalSaludServiceMock.contarProfesionales()).thenReturn(10);
        when(profesionalSaludServiceMock.listarProfesionales(1, 10)).thenReturn(listaProfesionales);

        String resultado = controladorProfesionalSalud.ListarProfesional(modelMock, 1, 10);

        assertEquals("ProfesionalSalud/listaProfesional", resultado);
    }

    @Test
    public void testEliminarProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");

        String idProfesional = "123456";

        String resultado = controladorProfesionalSalud.EliminarProfesional(idProfesional, modelMock);

        verify(profesionalSaludServiceMock).Eliminar(idProfesional);
        assertEquals("ProfesionalSalud/listaProfesional", resultado);
    }

    @Test
    public void testEditarProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");

        String idProfesional = "123456";
        Optional<ProfesionalSalud> profesionalSaludOptional = Optional.of(new ProfesionalSalud());

        when(profesionalSaludServiceMock.ConsultarId(idProfesional)).thenReturn(profesionalSaludOptional);

        String resultado = controladorProfesionalSalud.EditarProfesional(idProfesional, modelMock);

        assertEquals("ProfesionalSalud/editarProfesional", resultado);
    }

    @Test
    public void testActualizarProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");

        ProfesionalSalud profesionalSalud = new ProfesionalSalud();
        profesionalSalud.setId("123456");
        profesionalSalud.setNombre("nombre");
        profesionalSalud.setApellido("apellido");
        profesionalSalud.setEspecialidad("especialidad");
        profesionalSalud.setTelefono("telefono");
        profesionalSalud.setCorreo("correo");
        profesionalSalud.setHorario("horario");
        profesionalSalud.setNumeroLicencia("123456");

        String resultado = controladorProfesionalSalud.ActualizarProfesional("123456", "nombre", "apellido", "especialidad", "telefono", "correo", "horario", "123456", modelMock);

        ArgumentCaptor<ProfesionalSalud> profesionalSaludCaptor = ArgumentCaptor.forClass(ProfesionalSalud.class);
        verify(profesionalSaludServiceMock).Guardar(profesionalSaludCaptor.capture());

        ProfesionalSalud profesionalSaludActualizado = profesionalSaludCaptor.getValue();

        assertEquals("123456", profesionalSaludActualizado.getId());
        assertEquals("nombre", profesionalSaludActualizado.getNombre());
        assertEquals("apellido", profesionalSaludActualizado.getApellido());
        assertEquals("especialidad", profesionalSaludActualizado.getEspecialidad());
        assertEquals("telefono", profesionalSaludActualizado.getTelefono());
        assertEquals("correo", profesionalSaludActualizado.getCorreo());
        assertEquals("horario", profesionalSaludActualizado.getHorario());
        assertEquals("123456", profesionalSaludActualizado.getNumeroLicencia());

        assertEquals("ProfesionalSalud/listaProfesional", resultado);
    }

    @Test
    public void testBuscarProfesional() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");
        when(authMock.getAuthorities()).thenReturn(new ArrayList<>());
        when(usuarioServiceMock.obtenerNombreImagenUsuarioConectado()).thenReturn("nombreImagen");

        String descripcion = "descripcion";
        List<ProfesionalSalud> listaProfesionales = new ArrayList<>();
        listaProfesionales.add(new ProfesionalSalud());

        when(profesionalSaludServiceMock.Buscar(descripcion)).thenReturn(listaProfesionales);

        String resultado = controladorProfesionalSalud.BuscarProfesional(descripcion, modelMock);

        assertEquals("ProfesionalSalud/listaProfesional", resultado);
    }
}

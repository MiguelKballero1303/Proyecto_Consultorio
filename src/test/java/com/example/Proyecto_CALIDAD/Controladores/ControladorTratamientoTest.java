package com.example.Proyecto_CALIDAD.Controladores;

import java.util.ArrayList;
import java.util.Date;
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

import com.example.Proyecto_CALIDAD.Clases.Tratamiento;
import com.example.Proyecto_CALIDAD.Interfaces.ITratamientoService;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

public class ControladorTratamientoTest {

    @InjectMocks
    private ControladorTratamiento controladorTratamiento;

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
    public void testNuevoTratamiento() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String resultado = controladorTratamiento.NuevoTratamiento(modelMock);
        assertEquals("Tratamiento/nuevoTratamiento", resultado);
    }

    @Test
    public void testRegistrarTratamiento() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String nombreTratamiento = "Tratamiento 1";
        String descripcion = "Descripción del tratamiento";
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        String frecuenciaSesiones = "2 veces por semana";

        String resultado = controladorTratamiento.RegistrarTratamiento(nombreTratamiento, descripcion, fechaInicio, fechaFin, frecuenciaSesiones, modelMock);

        ArgumentCaptor<Tratamiento> tratamientoCaptor = ArgumentCaptor.forClass(Tratamiento.class);
        verify(tratamientoServiceMock).Guardar(tratamientoCaptor.capture());
        Tratamiento tratamientoGuardado = tratamientoCaptor.getValue();

        assertEquals(nombreTratamiento, tratamientoGuardado.getNombreTratamiento());
        assertEquals(descripcion, tratamientoGuardado.getDescripcion());
        assertEquals(fechaInicio, tratamientoGuardado.getFechaInicio());
        assertEquals(fechaFin, tratamientoGuardado.getFechaFin());
        assertEquals(frecuenciaSesiones, tratamientoGuardado.getFrecuenciaSesiones());

        assertEquals("Tratamiento/listaTratamiento", resultado);
    }

    @Test
    public void testListarTratamientos() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(tratamientoServiceMock.contarTratamientos()).thenReturn(10);
        when(tratamientoServiceMock.listarTratamientos(1, 10)).thenReturn(new ArrayList<>());

        String resultado = controladorTratamiento.ListarTratamientos(modelMock, 1, 10);
        assertEquals("Tratamiento/listaTratamiento", resultado);
    }

    @Test
    public void testEliminarTratamiento() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idTratamiento = "123456";
        String resultado = controladorTratamiento.EliminarTratamiento(idTratamiento, modelMock);

        verify(tratamientoServiceMock).Eliminar(idTratamiento);
        assertEquals("Tratamiento/listaTratamiento", resultado);
    }

    @Test
    public void testEditarTratamiento() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idTratamiento = "123456";
        Tratamiento tratamiento = new Tratamiento();
        when(tratamientoServiceMock.ConsultarId(idTratamiento)).thenReturn(Optional.of(tratamiento));

        String resultado = controladorTratamiento.EditarTratamiento(idTratamiento, modelMock);
        assertEquals("Tratamiento/editarTratamiento", resultado);
    }

    @Test
    public void testActualizarTratamiento() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        String idTratamiento = "123456";
        String nombreTratamiento = "Tratamiento actualizado";
        String descripcion = "Descripción actualizada";
        Date fechaInicio = new Date();
        Date fechaFin = new Date();
        String frecuenciaSesiones = "1 vez por semana";

        String resultado = controladorTratamiento.ActualizarTratamiento(idTratamiento, nombreTratamiento, 
        descripcion, fechaInicio, fechaFin, frecuenciaSesiones, modelMock);

        verify(tratamientoServiceMock).Guardar(any(Tratamiento.class));
        assertEquals("Tratamiento/listaTratamiento", resultado);
    }

    @Test
    public void testBuscarTratamientos() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("username");

        when(tratamientoServiceMock.Buscar("tratamiento")).thenReturn(new ArrayList<>());

        String resultado = controladorTratamiento.BuscarTratamientos("tratamiento", modelMock);
        assertEquals("Tratamiento/listaTratamiento", resultado);
    }
}

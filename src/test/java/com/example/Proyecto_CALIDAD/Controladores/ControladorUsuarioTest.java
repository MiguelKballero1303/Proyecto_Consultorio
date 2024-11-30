package com.example.Proyecto_CALIDAD.Controladores;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.example.Proyecto_CALIDAD.Clases.FileStorageService;
import com.example.Proyecto_CALIDAD.Clases.Usuario;
import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

public class ControladorUsuarioTest {

    private ControladorUsuario controladorUsuario;

    private IUsuarioService usuarioServiceMock;
    private FileStorageService fileStorageServiceMock;
    private MultipartFile imagenMock;

    @BeforeEach
    public void setUp() {
        usuarioServiceMock = mock(IUsuarioService.class);
        fileStorageServiceMock = mock(FileStorageService.class);
        controladorUsuario = new ControladorUsuario(fileStorageServiceMock, null);
        controladorUsuario.service = usuarioServiceMock;

        imagenMock = mock(MultipartFile.class);
    }

    private void setupAuthMock() {
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
        when(authMock.getName()).thenReturn("user");
    }

    @Test
    void testActualizarUsuario() {
        Model modelMock = mock(Model.class);
        Authentication authMock = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authMock);
    
        int codigoU = 1;
        String nombre = "nombre";
        String apellido = "apellido";
        String dni = "dni";
        String celular = "celular";
        String email = "email";
        String direccion = "direccion";
        String pas = "pas";
        String user = "user";
        String rol = "rol";
        MultipartFile imagen = null;
    
        String resultado = controladorUsuario.ActualizarUsuario(codigoU, nombre, apellido, dni, celular, email,
                direccion, pas, user, rol, imagen, modelMock);
    
        assertEquals("Usuario/listaUsuario", resultado);
    }
    

    @Test
    void testBuscarUsuario() {
        Model modelMock = mock(Model.class);
        setupAuthMock();

        String descripcion = "descripcion";
        List<Usuario> listaU = new ArrayList<>();
        listaU.add(new Usuario());

        when(usuarioServiceMock.Buscar(descripcion)).thenReturn(listaU);

        String resultado = controladorUsuario.BuscarUsuario(descripcion, modelMock);

        assertEquals("Usuario/listaUsuario", resultado);
    }

    @Test
    void testEditarUsuario() {
        Model modelMock = mock(Model.class);
        setupAuthMock();

        int codigoU = 1;
        Optional<Usuario> usuarioOptional = Optional.of(new Usuario());

        when(usuarioServiceMock.ConsultarId(codigoU)).thenReturn(usuarioOptional);

        String resultado = controladorUsuario.EditarUsuario(codigoU, modelMock);

        assertEquals("Usuario/editarUsuario", resultado);
    }
    @Test
    void testActualizarUsuarioConImagen() throws IOException {
        Model modelMock = mock(Model.class);
        setupAuthMock();
    
        Usuario u = new Usuario();
        u.setId(1);
        u.setNombre("nom");
        u.setApellido("ape");
        u.setDni("dni");
        u.setCelular("cel");
        u.setEmail("email");
        u.setDireccion("dir");
        u.setPassword("$2a$10$JW.Ozix.6TbV/n2GR8bo1ODJsQPrHW7Yi4dHAHafzY/Xi2cZ8MNAq");
        u.setUserr("use");
        u.setRoles("rol");
        u.setImagenUrl("imagen.jpg"); 
    
        when(imagenMock.getOriginalFilename()).thenReturn("imagenActualizada.jpg");
        when(fileStorageServiceMock.storeFile(imagenMock)).thenReturn("imagenActualizada.jpg");
        when(usuarioServiceMock.ConsultarId(1)).thenReturn(Optional.of(u));
    
        String resultado = controladorUsuario.ActualizarUsuario(1, "nom", "ape", "dni", "cel", "email", "dir",
                "1234", "use", "rol", imagenMock, modelMock);
    
        verify(fileStorageServiceMock).deleteFile("imagen.jpg"); 
        verify(usuarioServiceMock).Guardar(argThat(argument ->
            argument.getId() == 1 &&
            argument.getNombre().equals(u.getNombre()) &&
            argument.getApellido().equals(u.getApellido()) &&
            argument.getDni().equals(u.getDni()) &&
            argument.getEmail().equals(u.getEmail()) &&
            argument.getCelular().equals(u.getCelular()) &&
            argument.getDireccion().equals(u.getDireccion()) &&
            argument.getUserr().equals(u.getUserr()) &&
            argument.getRoles().equals(u.getRoles()) &&
            argument.getImagenUrl().equals("imagenActualizada.jpg")
        ));
    
        assertEquals("Usuario/listaUsuario", resultado);
    }

    @Test
    void testEliminarUsuario() {
        Model modelMock = mock(Model.class);
        setupAuthMock();

        int codigoU = 1;

        String resultado = controladorUsuario.EliminarUsuario(codigoU, modelMock);

        assertEquals("Usuario/listaUsuario", resultado);
    }

    @Test
    void testListarUsuario() {
        Model modelMock = mock(Model.class);
        setupAuthMock();

        int pagina = 1;
        int itemsPerPage = 10;
        List<Usuario> listaU = new ArrayList<>();
        listaU.add(new Usuario());

        when(usuarioServiceMock.listarUsuarios(pagina, itemsPerPage)).thenReturn(listaU);
        when(usuarioServiceMock.contarUsuarios()).thenReturn(10);

        String resultado = controladorUsuario.ListarUsuario(modelMock, pagina, itemsPerPage);

        assertEquals("Usuario/listaUsuario", resultado);
    }

    @Test
    void testNuevoUsuario() {
        Model modelMock = mock(Model.class);
        setupAuthMock();

        String resultado = controladorUsuario.NuevoUsuario(modelMock);

        assertEquals("Usuario/nuevoUsuario", resultado);
    }

    @Test
    void testRegistrarUsuario() throws IOException {
        Model modelMock = mock(Model.class);
        setupAuthMock();

        Usuario u = new Usuario();
        u.setNombre("nom");
        u.setApellido("ape");
        u.setDni("dni");
        u.setCelular("cel");
        u.setEmail("email");
        u.setDireccion("dir");
        u.setPassword("$2a$10$JW.Ozix.6TbV/n2GR8bo1ODJsQPrHW7Yi4dHAHafzY/Xi2cZ8MNAq"); // Contraseña encriptada
        u.setUserr("use");
        u.setRoles("rol");

        when(imagenMock.getOriginalFilename()).thenReturn("imagen.jpg");
        when(fileStorageServiceMock.storeFile(imagenMock)).thenReturn("imagen.jpg");

        String resultado = controladorUsuario.RegistrarUsuario("nom", "ape", "dni", "cel", "email", "dir",
                "1234", "use", "rol", imagenMock, modelMock);

        verify(usuarioServiceMock).Guardar(argThat(argument ->
            argument.getNombre().equals(u.getNombre()) &&
            argument.getApellido().equals(u.getApellido()) &&
            argument.getDni().equals(u.getDni()) &&
            argument.getEmail().equals(u.getEmail()) &&
            argument.getCelular().equals(u.getCelular()) &&
            argument.getDireccion().equals(u.getDireccion()) &&
            argument.getUserr().equals(u.getUserr()) &&
            argument.getRoles().equals(u.getRoles()) &&
            argument.getImagenUrl().equals("imagen.jpg")
        ));

        assertEquals("Usuario/listaUsuario", resultado);
    }

    
}

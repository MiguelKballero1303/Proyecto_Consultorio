package com.example.Proyecto_CALIDAD.Controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.Proyecto_CALIDAD.Interfaces.IUsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class ControladorMenu {

    @Autowired
    IUsuarioService service_u;

    @GetMapping("/")
    public String PaginaPrincipal() {
        return "PaginaPrincipal"; // Redirige a PaginaPrincipal.html
    }

    @GetMapping("/register")
    public String registrarUsuario() {
        return "register"; //register.html
    }

    @GetMapping("/forgot-password")
    public String restablecerContrasena() {
        return "forgot-password"; //forgot-password.html
    }

    @GetMapping("/Login")
    public String Login(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
    
        // Manejo de errores de autenticación
        String errorMessage = null;
        if (session != null) {
            AuthenticationException ex = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
            if (ex != null) {
                errorMessage = ex.getMessage();
            }
        }
    
        // Verificar si existe un mensaje de error desde RedirectAttributes
        if (model.containsAttribute("error")) {
            // Si ya existe un error redirigido, le damos prioridad a este
            errorMessage = (String) model.getAttribute("error");
        }
    
        model.addAttribute("error", errorMessage);
        return "login"; // login.html
    }
    
    

    @GetMapping("/Menu")
    public String Menu(Model model, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        GrantedAuthority authority = auth.getAuthorities().stream().findFirst().orElse(null);
        String userRole = (authority != null) ? authority.getAuthority() : "ROL_NO_ASIGNADO";

        if ("ROL_PENDIENTE".equals(userRole)) {
            redirectAttributes.addFlashAttribute("error", "Su registro está en revisión. No puede acceder hasta que sea aprobado.");
            return "redirect:/Login";
        } else {
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
            return "Principal/Principal";
        }
    }

}

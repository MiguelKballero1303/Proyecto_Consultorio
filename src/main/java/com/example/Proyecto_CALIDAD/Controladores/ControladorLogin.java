
package com.example.Proyecto_CALIDAD.Controladores;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ControladorLogin {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "login"; //login.html
    }

    @GetMapping("/acceso-denegado")
    public String AccesoDenegado() {
        return "access-denied"; //login.html
    }    
}



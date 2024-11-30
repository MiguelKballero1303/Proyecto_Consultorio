package com.example.Proyecto_CALIDAD;

import com.example.Proyecto_CALIDAD.Servicios.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTasks {

    @Autowired
    private UsuarioService usuarioService;

    @Scheduled(fixedRate = 60000) // Ejecutar cada 60 segundos
    public void desbloquearCuentas() {
        usuarioService.desbloquearCuentas();
    }
}

package com.example.Proyecto_CALIDAD.Clases;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity //Entidad de base de datos
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String nombre;
    public String apellido;
    public String dni;
    public String celular;
    public String email;
    public String direccion;
    public String password;
    public String userr;
    public String roles;
    private String imagenUrl;    
    private int failedLoginAttempts = 0; 
    private LocalDateTime lastFailedLoginTime;
    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    // Método para incrementar el contador de intentos fallidos
    public void incrementFailedLoginAttempts() {
        failedLoginAttempts++;
    }

    // Método para reiniciar el contador de intentos fallidos
    public void resetFailedLoginAttempts() {
        failedLoginAttempts = 0;
    }
    
    private boolean accountNonLocked=true; // Campo para representar el estado de bloqueo de la cuenta

    // Getters y setters para los campos existentes

    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }
    public LocalDateTime getLastFailedLoginTime() {
        return lastFailedLoginTime;
    }

    public void setLastFailedLoginTime(LocalDateTime lastFailedLoginTime) {
        this.lastFailedLoginTime = lastFailedLoginTime;
    }
}

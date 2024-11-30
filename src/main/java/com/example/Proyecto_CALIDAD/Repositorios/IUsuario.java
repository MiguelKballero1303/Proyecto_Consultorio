package com.example.Proyecto_CALIDAD.Repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Proyecto_CALIDAD.Clases.Usuario;

@Repository
public interface IUsuario extends CrudRepository<Usuario, Integer> {

    public Optional<Usuario> findByUserr(String username);

    @Query(value = "SELECT * FROM usuario "
            + "WHERE nombre LIKE %:desc% "
            + "OR apellido LIKE %:desc% "
            + "OR dni LIKE %:desc% "
            + "OR celular LIKE %:desc% "
            + "OR email LIKE %:desc% "
            + "OR direccion LIKE %:desc% "
            + "OR password LIKE %:desc% "
            + "OR userr LIKE %:desc%", nativeQuery = true)
    List<Usuario> findForAll(@Param("desc") String desc);

    @Query("SELECT u FROM Usuario u")
    Page<Usuario> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM usuario WHERE email = :email", nativeQuery = true)
    Optional<Usuario> findByEmail(@Param("email") String email);
}

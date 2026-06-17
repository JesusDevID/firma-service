package com.equidad.firmaservice.repository;
import java.util.List;
import com.equidad.firmaservice.model.FirmaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface FirmaRepository
        extends JpaRepository<FirmaEntity, Long> {

    List<FirmaEntity> findByEstado(String estado);

    List<FirmaEntity> findByCorreo(String correo);


}
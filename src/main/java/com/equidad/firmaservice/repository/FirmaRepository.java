package com.equidad.firmaservice.repository;
import java.util.List;
import java.util.Optional;
import com.equidad.firmaservice.model.FirmaEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;



@Repository
public interface FirmaRepository
        extends JpaRepository<FirmaEntity, Long>, JpaSpecificationExecutor<FirmaEntity> {

    List<FirmaEntity> findByEstado(String estado);

    List<FirmaEntity> findByCorreo(String correo);

    Optional<FirmaEntity> findFirstByDocumentoIdOrderByIdDesc(String documentoId);

}

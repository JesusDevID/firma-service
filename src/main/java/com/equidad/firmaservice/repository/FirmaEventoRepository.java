package com.equidad.firmaservice.repository;

import com.equidad.firmaservice.model.FirmaEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface FirmaEventoRepository
        extends JpaRepository<FirmaEventoEntity, Long> {

    Optional<FirmaEventoEntity> findByProveedorAndIdEventoExterno(
            String proveedor,
            String idEventoExterno);

    List<FirmaEventoEntity> findByFirmaIdOrderByFechaRecepcionDesc(Long firmaId);
}

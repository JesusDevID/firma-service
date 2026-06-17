package com.equidad.firmaservice.service;

import com.equidad.firmaservice.client.SignioClient;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.FirmaResponseDTO;
import com.equidad.firmaservice.dto.SignioResponseDTO;
import com.equidad.firmaservice.model.EstadoFirma;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.repository.FirmaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FirmaService {

    private static final Logger logger =
            LoggerFactory.getLogger(FirmaService.class);

    private final SignioClient signioClient;
    private final FirmaRepository firmaRepository;

    public FirmaService(SignioClient signioClient,
                        FirmaRepository firmaRepository) {

        this.signioClient = signioClient;
        this.firmaRepository = firmaRepository;
    }

    public FirmaResponseDTO procesarFirma(FirmaRequestDTO request) {

        logger.info("Procesando documento: {}",
                request.getIdDocumento());

        logger.info("Correo receptor: {}",
                request.getCorreo());

        SignioResponseDTO respuestaSignio =
                signioClient.enviarDocumento(
                        request.getIdDocumento());

        logger.info("Estado Signio: {}",
                respuestaSignio.getEstado());

        EstadoFirma estado;

        if (respuestaSignio.getEstado().equals("OK")) {

            estado = EstadoFirma.ENVIADO;

            logger.info("Documento enviado correctamente");

        } else {

            estado = EstadoFirma.ERROR;

            logger.error("Error enviando documento a Signio");
        }

        FirmaEntity firmaEntity = new FirmaEntity();

        firmaEntity.setDocumentoId(
                request.getIdDocumento());

        firmaEntity.setCorreo(
                request.getCorreo());

        firmaEntity.setEstado(
                estado.name());

        firmaEntity.setRespuestaSignio(
                respuestaSignio.getMensaje());

        firmaEntity.setFechaCreacion(
                LocalDateTime.now());

        firmaRepository.save(firmaEntity);

        logger.info("Firma guardada en base de datos");

        return new FirmaResponseDTO(
                "Solicitud procesada correctamente",
                estado.name()
        );
    }

    public List<FirmaEntity> obtenerTodas() {

        logger.info("Consultando todas las firmas");

        return firmaRepository.findAll();
    }

    public FirmaEntity obtenerFirmaPorId(Long id) {

        logger.info("Buscando firma con ID: {}", id);

        return firmaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Firma no encontrada"));
    }

    public List<FirmaEntity> obtenerPorEstado(String estado) {

        logger.info("Buscando firmas por estado: {}", estado);

        return firmaRepository.findByEstado(estado);
    }

    public List<FirmaEntity> obtenerPorCorreo(String correo) {

        logger.info("Buscando firmas por correo: {}", correo);

        return firmaRepository.findByCorreo(correo);
    }

    public FirmaEntity marcarComoFirmado(Long id) {

        logger.info("Marcando firma como FIRMADA: {}", id);

        FirmaEntity firma =
                firmaRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Firma no encontrada"));

        firma.setEstado("FIRMADO");

        return firmaRepository.save(firma);
    }
}


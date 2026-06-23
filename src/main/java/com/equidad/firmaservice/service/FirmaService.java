package com.equidad.firmaservice.service;

import com.equidad.firmaservice.client.FirmaProviderClient;
import com.equidad.firmaservice.dto.FirmaEventoResponseDTO;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.FirmaResponseDTO;
import com.equidad.firmaservice.dto.SignioWebhookEventDTO;
import com.equidad.firmaservice.dto.SignioResponseDTO;
import com.equidad.firmaservice.exception.BusinessException;
import com.equidad.firmaservice.exception.ResourceNotFoundException;
import com.equidad.firmaservice.model.EstadoFirma;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.model.FirmaEventoEntity;
import com.equidad.firmaservice.repository.FirmaEventoRepository;
import com.equidad.firmaservice.repository.FirmaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FirmaService {

    private static final Logger logger =
            LoggerFactory.getLogger(FirmaService.class);

    private final FirmaProviderClient firmaProviderClient;
    private final FirmaRepository firmaRepository;
    private final FirmaEventoRepository firmaEventoRepository;

    public FirmaService(FirmaProviderClient firmaProviderClient,
                        FirmaRepository firmaRepository,
                        FirmaEventoRepository firmaEventoRepository) {

        this.firmaProviderClient = firmaProviderClient;
        this.firmaRepository = firmaRepository;
        this.firmaEventoRepository = firmaEventoRepository;
    }

    public FirmaResponseDTO procesarFirma(FirmaRequestDTO request) {

        logger.info("Iniciando proceso de firma");

        SignioResponseDTO respuestaSignio =
                firmaProviderClient.enviarDocumento(request);

        logger.info("Respuesta mock de Signio recibida con estado: {}",
                respuestaSignio.getEstado());

        EstadoFirma estado;

        if ("OK".equals(respuestaSignio.getEstado())) {

            estado = EstadoFirma.ENVIADO;

            logger.info("Documento enviado correctamente al cliente mock");

        } else {

            estado = EstadoFirma.ERROR;

            logger.error("Error enviando documento al cliente mock");
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

        firmaEntity.setProveedorFirma("SIGNIO");

        firmaEntity.setUsuarioCreacion(usuarioActual());
        firmaEntity.setUsuarioActualizacion(usuarioActual());

        firmaRepository.save(firmaEntity);

        logger.info("Firma guardada con estado: {}", estado.name());

        return new FirmaResponseDTO(
                "Solicitud procesada correctamente",
                estado.name()
        );
    }

    public List<FirmaEntity> obtenerTodas() {

        logger.info("Consultando todas las firmas sin paginación");

        return firmaRepository.findAll();
    }

    public Page<FirmaEntity> buscarFirmas(String estado,
                                          String correo,
                                          LocalDateTime fechaInicio,
                                          LocalDateTime fechaFin,
                                          Pageable pageable) {

        logger.info("Consultando firmas con filtros y paginación");

        Specification<FirmaEntity> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        if (estado != null && !estado.isBlank()) {
            EstadoFirma estadoFirma = validarEstado(estado);
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("estado"), estadoFirma.name()));
        }

        if (correo != null && !correo.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("correo"), correo));
        }

        if (fechaInicio != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(
                            root.get("fechaCreacion"), fechaInicio));
        }

        if (fechaFin != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(
                            root.get("fechaCreacion"), fechaFin));
        }

        return firmaRepository.findAll(specification, pageable);
    }

    public FirmaEntity obtenerFirmaPorId(Long id) {

        logger.info("Buscando firma con ID: {}", id);

        return firmaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Firma no encontrada"));
    }

    public List<FirmaEntity> obtenerPorEstado(String estado) {

        logger.info("Buscando firmas por estado: {}", estado);

        return firmaRepository.findByEstado(validarEstado(estado).name());
    }

    public List<FirmaEntity> obtenerPorCorreo(String correo) {

        logger.info("Buscando firmas por correo");

        return firmaRepository.findByCorreo(correo);
    }

    public FirmaEntity marcarComoFirmado(Long id) {

        return cambiarEstado(id, EstadoFirma.FIRMADO);
    }

    public FirmaEntity rechazarFirma(Long id) {

        return cambiarEstado(id, EstadoFirma.RECHAZADO);
    }

    public FirmaEntity expirarFirma(Long id) {

        return cambiarEstado(id, EstadoFirma.EXPIRADO);
    }

    public List<FirmaEventoResponseDTO> obtenerEventosFirma(Long id) {

        logger.info("Consultando historial de eventos de firma. id={}", id);

        if (!firmaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Firma no encontrada");
        }

        return firmaEventoRepository
                .findByFirmaIdOrderByFechaRecepcionDesc(id)
                .stream()
                .map(this::mapearEventoResponse)
                .toList();
    }

    public FirmaEntity procesarEventoSignio(SignioWebhookEventDTO event) {

        logger.info("Procesando webhook de Signio. documentoId={}, estadoExterno={}",
                event.getDocumentoId(), event.getEstado());

        if (event.getIdEventoExterno() != null
                && !event.getIdEventoExterno().isBlank()) {

            var eventoExistente =
                    firmaEventoRepository.findByProveedorAndIdEventoExterno(
                            "SIGNIO",
                            event.getIdEventoExterno());

            if (eventoExistente.isPresent()) {
                logger.info("Webhook Signio duplicado ignorado. idEventoExterno={}",
                        event.getIdEventoExterno());
                return eventoExistente.get().getFirma();
            }
        }

        FirmaEntity firma =
                firmaRepository.findFirstByDocumentoIdOrderByIdDesc(
                                event.getDocumentoId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Firma no encontrada para el documento informado"));

        EstadoFirma estado = mapearEstadoSignio(event.getEstado());
        String estadoAnterior = firma.getEstado();

        firma.setEstado(estado.name());
        firma.setProveedorFirma("SIGNIO");
        firma.setIdTransaccionExterna(event.getIdTransaccionExterna());
        firma.setUltimoEventoExterno(event.getEstado());
        firma.setFechaUltimoEvento(
                event.getFechaEvento() != null
                        ? event.getFechaEvento()
                        : LocalDateTime.now());
        firma.setRespuestaSignio(event.getMensaje());
        firma.setUsuarioActualizacion("webhook-signio");

        FirmaEntity firmaActualizada = firmaRepository.save(firma);
        guardarEventoSignio(event, firmaActualizada, estadoAnterior, estado.name());

        logger.info("Webhook de Signio aplicado. firmaId={}, nuevoEstado={}",
                firmaActualizada.getId(), estado.name());

        return firmaActualizada;
    }

    private void guardarEventoSignio(SignioWebhookEventDTO event,
                                     FirmaEntity firma,
                                     String estadoAnterior,
                                     String estadoNuevo) {

        FirmaEventoEntity evento = new FirmaEventoEntity();
        evento.setFirma(firma);
        evento.setProveedor("SIGNIO");
        evento.setIdEventoExterno(event.getIdEventoExterno());
        evento.setIdTransaccionExterna(event.getIdTransaccionExterna());
        evento.setDocumentoId(event.getDocumentoId());
        evento.setEstadoExterno(event.getEstado());
        evento.setEstadoAnterior(estadoAnterior);
        evento.setEstadoNuevo(estadoNuevo);
        evento.setMensaje(event.getMensaje());
        evento.setFechaEvento(event.getFechaEvento());

        firmaEventoRepository.save(evento);
    }

    private FirmaEventoResponseDTO mapearEventoResponse(FirmaEventoEntity evento) {

        FirmaEventoResponseDTO response = new FirmaEventoResponseDTO();
        response.setProveedor(evento.getProveedor());
        response.setIdEventoExterno(evento.getIdEventoExterno());
        response.setIdTransaccionExterna(evento.getIdTransaccionExterna());
        response.setDocumentoId(evento.getDocumentoId());
        response.setEstadoExterno(evento.getEstadoExterno());
        response.setEstadoAnterior(evento.getEstadoAnterior());
        response.setEstadoNuevo(evento.getEstadoNuevo());
        response.setMensaje(evento.getMensaje());
        response.setFechaEvento(evento.getFechaEvento());
        response.setFechaRecepcion(evento.getFechaRecepcion());
        return response;
    }

    private FirmaEntity cambiarEstado(Long id, EstadoFirma nuevoEstado) {

        logger.info("Cambiando estado de firma. id={}, nuevoEstado={}",
                id, nuevoEstado.name());

        FirmaEntity firma =
                firmaRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Firma no encontrada"));

        firma.setEstado(nuevoEstado.name());
        firma.setUsuarioActualizacion(usuarioActual());

        FirmaEntity firmaActualizada = firmaRepository.save(firma);

        logger.info("Estado de firma actualizado. id={}, nuevoEstado={}",
                id, nuevoEstado.name());

        return firmaActualizada;
    }

    private EstadoFirma validarEstado(String estado) {

        try {
            return EstadoFirma.valueOf(estado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Estado de firma inválido: " + estado);
        }
    }

    private EstadoFirma mapearEstadoSignio(String estadoExterno) {

        if (estadoExterno == null || estadoExterno.isBlank()) {
            throw new BusinessException("Estado externo de Signio inválido");
        }

        return switch (estadoExterno.trim().toUpperCase()) {
            case "PENDING", "PENDIENTE" -> EstadoFirma.PENDIENTE;
            case "SENT", "ENVIADO" -> EstadoFirma.ENVIADO;
            case "PROCESSING", "IN_PROCESS", "EN_PROCESO" ->
                    EstadoFirma.EN_PROCESO;
            case "SIGNED", "COMPLETED", "FIRMADO" -> EstadoFirma.FIRMADO;
            case "REJECTED", "DECLINED", "RECHAZADO" ->
                    EstadoFirma.RECHAZADO;
            case "EXPIRED", "EXPIRADO" -> EstadoFirma.EXPIRADO;
            case "ERROR", "FAILED" -> EstadoFirma.ERROR;
            default -> throw new BusinessException(
                    "Estado externo de Signio no soportado: " + estadoExterno);
        };
    }

    private String usuarioActual() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || authentication.getName() == null
                || authentication.getName().isBlank()) {
            return "sistema";
        }

        return authentication.getName();
    }
}

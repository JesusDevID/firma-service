package com.equidad.firmaservice.service;

import com.equidad.firmaservice.client.SignioClient;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.FirmaResponseDTO;
import com.equidad.firmaservice.dto.SignioWebhookEventDTO;
import com.equidad.firmaservice.dto.SignioResponseDTO;
import com.equidad.firmaservice.exception.BusinessException;
import com.equidad.firmaservice.exception.ResourceNotFoundException;
import com.equidad.firmaservice.model.EstadoFirma;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.repository.FirmaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirmaServiceTest {

    @Mock
    private SignioClient signioClient;

    @Mock
    private FirmaRepository firmaRepository;

    @Test
    void procesarFirmaGuardaFirmaEnEstadoEnviado() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaRequestDTO request = new FirmaRequestDTO();
        request.setIdDocumento("DOC-1");
        request.setCorreo("usuario@correo.com");
        request.setPdfBase64("base64");

        SignioResponseDTO signioResponse = new SignioResponseDTO();
        signioResponse.setEstado("OK");
        signioResponse.setMensaje("mock ok");

        when(signioClient.enviarDocumento("DOC-1")).thenReturn(signioResponse);

        FirmaResponseDTO response = service.procesarFirma(request);

        ArgumentCaptor<FirmaEntity> captor =
                ArgumentCaptor.forClass(FirmaEntity.class);
        verify(firmaRepository).save(captor.capture());

        assertThat(response.getEstado()).isEqualTo(EstadoFirma.ENVIADO.name());
        assertThat(captor.getValue().getEstado())
                .isEqualTo(EstadoFirma.ENVIADO.name());
        assertThat(captor.getValue().getCorreo())
                .isEqualTo("usuario@correo.com");
    }

    @Test
    void procesarFirmaGuardaFirmaEnEstadoErrorCuandoMockFalla() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaRequestDTO request = new FirmaRequestDTO();
        request.setIdDocumento("DOC-2");
        request.setCorreo("usuario@correo.com");
        request.setPdfBase64("base64");

        SignioResponseDTO signioResponse = new SignioResponseDTO();
        signioResponse.setEstado("ERROR");
        signioResponse.setMensaje("mock error");

        when(signioClient.enviarDocumento("DOC-2")).thenReturn(signioResponse);

        FirmaResponseDTO response = service.procesarFirma(request);

        assertThat(response.getEstado()).isEqualTo(EstadoFirma.ERROR.name());
    }

    @Test
    void obtenerTodasRetornaRepositoryFindAll() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        when(firmaRepository.findAll()).thenReturn(List.of(new FirmaEntity()));

        List<FirmaEntity> resultado = service.obtenerTodas();

        assertThat(resultado).hasSize(1);
    }

    @Test
    void obtenerPorEstadoValidoNormalizaEstado() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        when(firmaRepository.findByEstado(EstadoFirma.FIRMADO.name()))
                .thenReturn(List.of(new FirmaEntity()));

        List<FirmaEntity> resultado = service.obtenerPorEstado("firmado");

        assertThat(resultado).hasSize(1);
        verify(firmaRepository).findByEstado(EstadoFirma.FIRMADO.name());
    }

    @Test
    void obtenerPorCorreoConsultaRepository() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        when(firmaRepository.findByCorreo("usuario@correo.com"))
                .thenReturn(List.of(new FirmaEntity()));

        List<FirmaEntity> resultado =
                service.obtenerPorCorreo("usuario@correo.com");

        assertThat(resultado).hasSize(1);
    }

    @Test
    void obtenerFirmaPorIdCuandoNoExisteLanzaResourceNotFound() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);

        when(firmaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerFirmaPorId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Firma no encontrada");
    }

    @Test
    void rechazarFirmaActualizaEstado() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaEntity firma = new FirmaEntity();
        firma.setEstado(EstadoFirma.ENVIADO.name());

        when(firmaRepository.findById(1L)).thenReturn(Optional.of(firma));
        when(firmaRepository.save(firma)).thenReturn(firma);

        FirmaEntity actualizada = service.rechazarFirma(1L);

        assertThat(actualizada.getEstado())
                .isEqualTo(EstadoFirma.RECHAZADO.name());
        verify(firmaRepository).save(firma);
    }

    @Test
    void marcarComoFirmadoActualizaEstado() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaEntity firma = new FirmaEntity();

        when(firmaRepository.findById(1L)).thenReturn(Optional.of(firma));
        when(firmaRepository.save(firma)).thenReturn(firma);

        FirmaEntity actualizada = service.marcarComoFirmado(1L);

        assertThat(actualizada.getEstado())
                .isEqualTo(EstadoFirma.FIRMADO.name());
    }

    @Test
    void expirarFirmaActualizaEstado() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaEntity firma = new FirmaEntity();

        when(firmaRepository.findById(1L)).thenReturn(Optional.of(firma));
        when(firmaRepository.save(firma)).thenReturn(firma);

        FirmaEntity actualizada = service.expirarFirma(1L);

        assertThat(actualizada.getEstado())
                .isEqualTo(EstadoFirma.EXPIRADO.name());
    }

    @Test
    void obtenerPorEstadoInvalidoLanzaBusinessException() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);

        assertThatThrownBy(() -> service.obtenerPorEstado("NO_EXISTE"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Estado de firma inválido");
    }

    @Test
    @SuppressWarnings("unchecked")
    void buscarFirmasUsaRepositoryConSpecificationYPaginacion() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        PageRequest pageable = PageRequest.of(0, 10);

        when(firmaRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(new FirmaEntity())));

        var resultado = service.buscarFirmas(
                EstadoFirma.FIRMADO.name(),
                "usuario@correo.com",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now(),
                pageable);

        assertThat(resultado.getTotalElements()).isEqualTo(1);
    }

    @Test
    void procesarEventoSignioActualizaFirmaPorDocumento() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaEntity firma = new FirmaEntity();
        firma.setDocumentoId("DOC-100");
        firma.setEstado(EstadoFirma.ENVIADO.name());

        SignioWebhookEventDTO event = new SignioWebhookEventDTO();
        event.setDocumentoId("DOC-100");
        event.setEstado("SIGNED");
        event.setIdTransaccionExterna("TX-1");
        event.setMensaje("Firmado desde webhook");

        when(firmaRepository.findFirstByDocumentoIdOrderByIdDesc("DOC-100"))
                .thenReturn(Optional.of(firma));
        when(firmaRepository.save(firma)).thenReturn(firma);

        FirmaEntity actualizada = service.procesarEventoSignio(event);

        assertThat(actualizada.getEstado())
                .isEqualTo(EstadoFirma.FIRMADO.name());
        assertThat(actualizada.getProveedorFirma()).isEqualTo("SIGNIO");
        assertThat(actualizada.getIdTransaccionExterna()).isEqualTo("TX-1");
        assertThat(actualizada.getUltimoEventoExterno()).isEqualTo("SIGNED");
        assertThat(actualizada.getFechaUltimoEvento()).isNotNull();
        verify(firmaRepository).save(firma);
    }

    @Test
    void procesarEventoSignioConEstadoNoSoportadoLanzaBusinessException() {
        FirmaService service = new FirmaService(signioClient, firmaRepository);
        FirmaEntity firma = new FirmaEntity();

        SignioWebhookEventDTO event = new SignioWebhookEventDTO();
        event.setDocumentoId("DOC-100");
        event.setEstado("UNKNOWN");

        when(firmaRepository.findFirstByDocumentoIdOrderByIdDesc("DOC-100"))
                .thenReturn(Optional.of(firma));

        assertThatThrownBy(() -> service.procesarEventoSignio(event))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Estado externo de Signio no soportado");
    }
}

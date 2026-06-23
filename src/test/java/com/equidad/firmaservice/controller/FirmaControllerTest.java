package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.model.EstadoFirma;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.dto.FirmaEventoResponseDTO;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.FirmaResponseDTO;
import com.equidad.firmaservice.service.FirmaService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FirmaControllerTest {

    @Test
    void healthRetornaServicioActivo() {
        FirmaController controller = new FirmaController(mock(FirmaService.class));

        assertThat(controller.health()).isEqualTo("Servicio activo");
    }

    @Test
    void enviarFirmaDelegacionAlService() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        FirmaRequestDTO request = new FirmaRequestDTO();
        FirmaResponseDTO expected =
                new FirmaResponseDTO("ok", EstadoFirma.ENVIADO.name());

        when(service.procesarFirma(request)).thenReturn(expected);

        assertThat(controller.enviarFirma(request)).isSameAs(expected);
    }

    @Test
    void obtenerFirmasSinParametrosMantieneListadoLegacy() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        when(service.obtenerTodas()).thenReturn(List.of(new FirmaEntity()));

        Object response = controller.obtenerFirmas(
                null, null, null, null, null, null);

        assertThat(response).isInstanceOf(List.class);
        verify(service).obtenerTodas();
    }

    @Test
    void obtenerFirmasConParametrosUsaPaginacionYFiltros() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        when(service.buscarFirmas(
                eq(EstadoFirma.FIRMADO.name()),
                eq("usuario@correo.com"),
                any(),
                any(),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new FirmaEntity())));

        Object response = controller.obtenerFirmas(
                0,
                10,
                EstadoFirma.FIRMADO.name(),
                "usuario@correo.com",
                java.time.LocalDateTime.now().minusDays(1),
                java.time.LocalDateTime.now());

        assertThat(response).isInstanceOf(PageImpl.class);
    }

    @Test
    void rechazarDocumentoRetornaApiResponse() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        FirmaEntity firma = new FirmaEntity();
        firma.setEstado(EstadoFirma.RECHAZADO.name());
        when(service.rechazarFirma(1L)).thenReturn(firma);

        var response = controller.rechazarDocumento(1L);

        assertThat(response.getCodigo()).isEqualTo(200);
        assertThat(response.getData().getEstado())
                .isEqualTo(EstadoFirma.RECHAZADO.name());
    }

    @Test
    void expirarDocumentoRetornaApiResponse() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        FirmaEntity firma = new FirmaEntity();
        firma.setEstado(EstadoFirma.EXPIRADO.name());
        when(service.expirarFirma(1L)).thenReturn(firma);

        var response = controller.expirarDocumento(1L);

        assertThat(response.getCodigo()).isEqualTo(200);
        assertThat(response.getData().getEstado())
                .isEqualTo(EstadoFirma.EXPIRADO.name());
    }

    @Test
    void endpointsLegacyDeleganAlService() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        FirmaEntity firma = new FirmaEntity();

        when(service.obtenerFirmaPorId(1L)).thenReturn(firma);
        when(service.obtenerPorEstado(EstadoFirma.FIRMADO.name()))
                .thenReturn(List.of(firma));
        when(service.obtenerPorCorreo("usuario@correo.com"))
                .thenReturn(List.of(firma));
        when(service.marcarComoFirmado(1L)).thenReturn(firma);

        assertThat(controller.obtenerFirmaPorId(1L)).isSameAs(firma);
        assertThat(controller.obtenerPorEstado(EstadoFirma.FIRMADO.name()))
                .containsExactly(firma);
        assertThat(controller.obtenerPorCorreo("usuario@correo.com"))
                .containsExactly(firma);
        assertThat(controller.firmarDocumento(1L)).isSameAs(firma);
    }

    @Test
    void obtenerEventosFirmaRetornaApiResponse() {
        FirmaService service = mock(FirmaService.class);
        FirmaController controller = new FirmaController(service);
        FirmaEventoResponseDTO evento = new FirmaEventoResponseDTO();
        evento.setEstadoNuevo(EstadoFirma.FIRMADO.name());

        when(service.obtenerEventosFirma(1L)).thenReturn(List.of(evento));

        var response = controller.obtenerEventosFirma(1L);

        assertThat(response.getCodigo()).isEqualTo(200);
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getEstadoNuevo())
                .isEqualTo(EstadoFirma.FIRMADO.name());
    }
}

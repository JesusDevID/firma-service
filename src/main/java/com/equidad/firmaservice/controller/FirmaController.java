package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.dto.ApiResponse;
import com.equidad.firmaservice.dto.FirmaEventoResponseDTO;
import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.FirmaResponseDTO;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.service.FirmaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/firmas")
@Tag(name = "Firmas", description = "Operaciones de gestión de firmas")
public class FirmaController {

    private final FirmaService firmaService;

    public FirmaController(FirmaService firmaService) {
        this.firmaService = firmaService;
    }

    @GetMapping("/health")
    @Operation(
            summary = "Estado básico del servicio",
            description = "Endpoint público y liviano para comprobar que el microservicio responde.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Servicio activo")
    })
    public String health() {
        return "Servicio activo";
    }

    @PostMapping("/enviar")
    @Operation(
            summary = "Enviar una solicitud de firma",
            description = "Crea una solicitud de firma y la envía al proveedor configurado. En modo mock no hace llamadas externas.")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Solicitud procesada correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Solicitud inválida"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente")
    })
    public FirmaResponseDTO enviarFirma(
            @Valid @RequestBody FirmaRequestDTO request) {

        return firmaService.procesarFirma(request);
    }

    @GetMapping
    @Operation(
            summary = "Listar firmas con paginación y filtros opcionales",
            description = "Sin parámetros devuelve una lista legacy. Con page, size o filtros devuelve una página. Fechas en formato ISO yyyy-MM-ddTHH:mm:ss.")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firmas consultadas correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Filtros inválidos"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente")
    })
    public Object obtenerFirmas(
            @Parameter(description = "Número de página iniciando en 0", example = "0")
            @RequestParam(required = false) Integer page,
            @Parameter(description = "Tamaño de página. Máximo aplicado: 100", example = "10")
            @RequestParam(required = false) Integer size,
            @Parameter(description = "Estado interno de firma", example = "FIRMADO")
            @RequestParam(required = false) String estado,
            @Parameter(description = "Correo exacto del firmante", example = "cliente@test.com")
            @RequestParam(required = false) String correo,
            @Parameter(description = "Fecha inicial de creación en formato ISO", example = "2026-06-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final de creación en formato ISO", example = "2026-06-30T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        boolean usarPaginacionOFiltros =
                page != null
                        || size != null
                        || estado != null
                        || correo != null
                        || fechaInicio != null
                        || fechaFin != null;

        if (!usarPaginacionOFiltros) {
            return firmaService.obtenerTodas();
        }

        int pagina = page != null ? page : 0;
        int tamano = size != null ? Math.min(size, 100) : 10;

        Page<FirmaEntity> firmas =
                firmaService.buscarFirmas(
                        estado,
                        correo,
                        fechaInicio,
                        fechaFin,
                        PageRequest.of(Math.max(pagina, 0), Math.max(tamano, 1)));

        return firmas;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consultar una firma por ID")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firma encontrada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Firma no encontrada")
    })
    public FirmaEntity obtenerFirmaPorId(
            @Parameter(description = "ID interno de la firma", example = "1")
            @PathVariable Long id) {

        return firmaService.obtenerFirmaPorId(id);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Consultar firmas por estado")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firmas consultadas correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Estado inválido"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente")
    })
    public List<FirmaEntity> obtenerPorEstado(
            @Parameter(description = "Estado interno de firma", example = "FIRMADO")
            @PathVariable String estado) {

        return firmaService.obtenerPorEstado(estado);
    }

    @GetMapping("/correo/{correo}")
    @Operation(summary = "Consultar firmas por correo")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firmas consultadas correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente")
    })
    public List<FirmaEntity> obtenerPorCorreo(
            @Parameter(description = "Correo exacto del firmante", example = "cliente@test.com")
            @PathVariable String correo) {

        return firmaService.obtenerPorCorreo(correo);
    }

    @PutMapping("/{id}/firmar")
    @Operation(summary = "Marcar una firma como firmada")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firma marcada como firmada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Firma no encontrada")
    })
    public FirmaEntity firmarDocumento(
            @Parameter(description = "ID interno de la firma", example = "1")
            @PathVariable Long id) {

        return firmaService.marcarComoFirmado(id);
    }

    @PutMapping("/{id}/rechazar")
    @Operation(summary = "Marcar una firma como rechazada")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firma marcada como rechazada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Firma no encontrada")
    })
    public ApiResponse<FirmaEntity> rechazarDocumento(
            @Parameter(description = "ID interno de la firma", example = "1")
            @PathVariable Long id) {

        return ApiResponse.exitoso(
                firmaService.rechazarFirma(id));
    }

    @PutMapping("/{id}/expirar")
    @Operation(summary = "Marcar una firma como expirada")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Firma marcada como expirada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Firma no encontrada")
    })
    public ApiResponse<FirmaEntity> expirarDocumento(
            @Parameter(description = "ID interno de la firma", example = "1")
            @PathVariable Long id) {

        return ApiResponse.exitoso(
                firmaService.expirarFirma(id));
    }

    @GetMapping("/{id}/eventos")
    @Operation(summary = "Consultar historial de eventos de una firma")
    @SecurityRequirement(name = "bearerAuth")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Historial consultado correctamente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Token inválido o ausente"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Firma no encontrada")
    })
    public ApiResponse<List<FirmaEventoResponseDTO>> obtenerEventosFirma(
            @Parameter(description = "ID interno de la firma", example = "1")
            @PathVariable Long id) {

        return ApiResponse.exitoso(
                firmaService.obtenerEventosFirma(id));
    }
}

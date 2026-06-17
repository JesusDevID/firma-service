package com.equidad.firmaservice.controller;

import com.equidad.firmaservice.dto.FirmaRequestDTO;
import com.equidad.firmaservice.dto.FirmaResponseDTO;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.service.FirmaService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firmas")
public class FirmaController {

    private final FirmaService firmaService;

    public FirmaController(FirmaService firmaService) {
        this.firmaService = firmaService;
    }

    @GetMapping("/health")
    public String health() {
        return "Servicio activo";
    }

    @PostMapping("/enviar")
    public FirmaResponseDTO enviarFirma(
            @Valid @RequestBody FirmaRequestDTO request) {

        return firmaService.procesarFirma(request);
    }

    @GetMapping
    public List<FirmaEntity> obtenerFirmas() {
        return firmaService.obtenerTodas();
    }

    @GetMapping("/{id}")

    public FirmaEntity obtenerFirmaPorId(
            @PathVariable Long id) {

        return firmaService.obtenerFirmaPorId(id);
    }
    @GetMapping("/estado/{estado}")
    public List<FirmaEntity> obtenerPorEstado(
            @PathVariable String estado) {

        return firmaService.obtenerPorEstado(estado);
    }

    @GetMapping("/correo/{correo}")
    public List<FirmaEntity> obtenerPorCorreo(
            @PathVariable String correo) {

        return firmaService.obtenerPorCorreo(correo);
    }

    @PutMapping("/{id}/firmar")
    public FirmaEntity firmarDocumento(
            @PathVariable Long id) {

        return firmaService.marcarComoFirmado(id);
    }

}
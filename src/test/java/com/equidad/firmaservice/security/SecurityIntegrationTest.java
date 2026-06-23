package com.equidad.firmaservice.security;

import com.equidad.firmaservice.model.EstadoFirma;
import com.equidad.firmaservice.model.FirmaEntity;
import com.equidad.firmaservice.repository.FirmaEventoRepository;
import com.equidad.firmaservice.repository.FirmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private FirmaRepository firmaRepository;

    @Autowired
    private FirmaEventoRepository firmaEventoRepository;

    @BeforeEach
    void setUp() {
        firmaEventoRepository.deleteAll();
        firmaRepository.deleteAll();
    }

    @Test
    void firmasSinTokenRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/firmas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void firmasConTokenValidoPermiteAcceso() throws Exception {
        String token = jwtUtil.generarToken("admin");

        mockMvc.perform(get("/firmas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void firmasConTokenInvalidoRetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/firmas")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actuatorHealthLivenessEsPublico() throws Exception {
        mockMvc.perform(get("/actuator/health/liveness"))
                .andExpect(status().isOk());
    }

    @Test
    void webhookSignioConSecretoValidoNoRequiereJwt() throws Exception {
        FirmaEntity firma = new FirmaEntity();
        firma.setDocumentoId("DOC-WEBHOOK");
        firma.setCorreo("cliente@test.com");
        firma.setEstado(EstadoFirma.ENVIADO.name());
        firmaRepository.save(firma);

        String body = """
                {
                  "documentoId": "DOC-WEBHOOK",
                  "estado": "SIGNED",
                  "idEventoExterno": "EVT-WEBHOOK-1",
                  "idTransaccionExterna": "TX-WEBHOOK-1",
                  "mensaje": "Documento firmado correctamente"
                }
                """;

        mockMvc.perform(post("/webhooks/signio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Signio-Webhook-Secret", "TOKEN_WEBHOOK_TEST")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.estado", is(EstadoFirma.FIRMADO.name())))
                .andExpect(jsonPath("$.data.ultimoEventoExterno", is("SIGNED")));
    }

    @Test
    void webhookSignioConSecretoInvalidoRetornaUnauthorized() throws Exception {
        String body = """
                {
                  "documentoId": "DOC-WEBHOOK",
                  "estado": "SIGNED"
                }
                """;

        mockMvc.perform(post("/webhooks/signio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Signio-Webhook-Secret", "secreto-incorrecto")
                        .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo", is(401)));
    }
}

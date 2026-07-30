package org.example.userstories.controller;

import org.example.userstories.mapper.PaymentMapper;
import org.example.userstories.model.Payment;
import org.example.userstories.model.PaymentRequest;
import org.example.userstories.model.PaymentResponse;
import org.example.userstories.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findAll_returnsOkWithPaymentList() throws Exception {
        Payment payment = new Payment();
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID(), 150.0, "EUR", "ACC-001",
                "DE89370400440532013000", "PENDING", LocalDateTime.now()
        );

        when(paymentService.findAll()).thenReturn(List.of(payment));
        when(paymentMapper.toResponse(payment)).thenReturn(response);

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(150.0))
                .andExpect(jsonPath("$[0].currency").value("EUR"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    void create_returnsCreatedWithSavedPayment() throws Exception {
        PaymentRequest request = new PaymentRequest(200.0, "USD", "ACC-002", "FR7630006000011234567890189");
        Payment entity = new Payment();
        Payment saved = new Payment();
        PaymentResponse response = new PaymentResponse(
                UUID.randomUUID(), 200.0, "USD", "ACC-002",
                "FR7630006000011234567890189", "PENDING", LocalDateTime.now()
        );

        when(paymentMapper.toEntity(request)).thenReturn(entity);
        when(paymentService.save(entity)).thenReturn(saved);
        when(paymentMapper.toResponse(saved)).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(200.0))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void findAll_returnsEmptyListWhenNoPayments()
            throws Exception {
        when(paymentService.findAll())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}

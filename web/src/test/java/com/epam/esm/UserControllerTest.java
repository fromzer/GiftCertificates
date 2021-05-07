package com.epam.esm;

import com.epam.esm.controller.UserController;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.GiftOrder;
import com.epam.esm.model.UserGift;
import com.epam.esm.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserController controller;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    private UserGift user;
    private List<GiftCertificate> certificates;
    private GiftOrder order;

    @BeforeEach
    public void create() {
        user = UserGift.builder()
                .id(1l)
                .login("alex")
                .firstName("Alex")
                .lastName("Pusher")
                .build();
        GiftCertificate certificate = GiftCertificate.builder()
                .id(1l)
                .name("New certificate name")
                .description("New certificate description")
                .price(BigDecimal.valueOf(12.13))
                .duration(1)
                .createDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .lastUpdateDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();
        certificates = new ArrayList<>();
        certificates.add(certificate);
        order = GiftOrder.builder()
                .id(1l)
                .user(user)
                .certificates(certificates)
                .cost(certificate.getPrice())
                .purchaseDate(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();
    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void shouldGetUserById() throws Exception {
        when(userService.findById(any())).thenReturn(user);
        mockMvc.perform(get("/api/v1/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()));
    }

    @Test
    public void shouldGetUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateUserOrder() throws Exception {
        when(userService.createUserOrder(anyLong(), any())).thenReturn(1l);
        mockMvc.perform(
                post("/api/v1/users/{id}/orders", user.getId())
                        .content(objectMapper.writeValueAsString(certificates))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetUserOrders() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/orders", user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetUserOrderInfo() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}/orders/{orderId}", user.getId(), order.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

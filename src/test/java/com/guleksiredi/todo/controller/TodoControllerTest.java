package com.guleksiredi.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guleksiredi.todo.dto.request.TodoRequest;
import com.guleksiredi.todo.dto.response.TodoResponse;
import com.guleksiredi.todo.service.TodoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@AutoConfigureMockMvc(addFilters = true)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private TodoRequest todoRequest;
    private TodoResponse todoResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(post("/api/v1/todo").with(csrf()))
                .defaultRequest(put("/api/v1/todo/1").with(csrf()))
                .defaultRequest(delete("/api/v1/todo/1").with(csrf()))
                .build();

        todoRequest = new TodoRequest();
        todoRequest.setTitle("Test Todo");
        todoRequest.setDescription("This is a test todo");
        todoRequest.setDueDate(LocalDate.now());
        todoRequest.setCompleted(false);

        todoResponse = TodoResponse.builder()
                .id(1L)
                .title("Test Todo")
                .description("This is a test todo")
                .dueDate(LocalDate.now())
                .completed(false)
                .build();
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testCreateTodo() throws Exception {
        when(todoService.addTodo(any(TodoRequest.class))).thenReturn(todoResponse);

        mockMvc.perform(post("/api/v1/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetTodoById() throws Exception {
        when(todoService.getTodoById(1L)).thenReturn(todoResponse);

        mockMvc.perform(get("/api/v1/todo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetUserTodos() throws Exception {
        when(todoService.getUserTodos()).thenReturn(List.of(todoResponse));

        mockMvc.perform(get("/api/v1/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testUpdateTodo() throws Exception {
        when(todoService.updateTodo(any(Long.class), any(TodoRequest.class))).thenReturn(todoResponse);

        mockMvc.perform(put("/api/v1/todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteTodo() throws Exception {
        doNothing().when(todoService).deleteTodo(1L);

        mockMvc.perform(delete("/api/v1/todo/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllTodos_Admin() throws Exception {
        when(todoService.getAllTodos()).thenReturn(List.of(todoResponse));

        mockMvc.perform(get("/api/v1/todo/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Todo"));
    }

}

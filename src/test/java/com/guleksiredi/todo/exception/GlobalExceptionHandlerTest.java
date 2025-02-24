package com.guleksiredi.todo.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guleksiredi.todo.controller.AuthController;
import com.guleksiredi.todo.controller.TodoController;
import com.guleksiredi.todo.exception.service.ResourceNotFoundException;
import com.guleksiredi.todo.exception.service.UsernameAlreadyExistsException;
import com.guleksiredi.todo.service.TodoService;
import com.guleksiredi.todo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AuthController.class, TodoController.class})
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private TodoService todoService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void handleUsernameAlreadyExistsException() throws Exception {
        doThrow(new UsernameAlreadyExistsException("Username already exists"))
                .when(userService).registerUser(any());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"existingUser\", \"password\":\"password123\", \"role\":\"USER\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void handleResourceNotFoundException() throws Exception {
        when(todoService.getTodoById(999L)).thenThrow(new ResourceNotFoundException("Todo not found"));

        mockMvc.perform(get("/api/v1/todo/999"))
                .andExpect(status().isNotFound());
    }
    @Test
    void handleValidationExceptions() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType("application/json")
                        .content("{\"username\":\"\", \"password\":\"123\", \"role\":\"USER\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void handleGenericException() throws Exception {
        mockMvc.perform(post("/api/v1/unknown"))
                .andExpect(status().isInternalServerError());
    }
}
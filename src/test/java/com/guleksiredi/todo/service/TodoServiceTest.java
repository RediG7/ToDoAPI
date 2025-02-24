package com.guleksiredi.todo.service;

import com.guleksiredi.todo.dto.request.TodoRequest;
import com.guleksiredi.todo.dto.response.TodoResponse;
import com.guleksiredi.todo.entity.Todo;
import com.guleksiredi.todo.entity.User;
import com.guleksiredi.todo.enums.UserRole;
import com.guleksiredi.todo.exception.service.ResourceNotFoundException;
import com.guleksiredi.todo.repository.TodoRepository;
import com.guleksiredi.todo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TodoService todoService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .password("password")
                .role(UserRole.USER)
                .build();

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                mockUser, null, mockUser.getAuthorities());
        SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
    }

    @Test
    void shouldReturnUserTodos() {
        Todo todo = Todo.builder().id(1L).title("Task 1").user(mockUser).build();
        when(todoRepository.findByUserId(mockUser.getId())).thenReturn(List.of(todo));

        List<TodoResponse> todos = todoService.getUserTodos();

        assertEquals(1, todos.size());
        assertEquals("Task 1", todos.get(0).getTitle());
    }

    @Test
    void shouldReturnTodoById() {
        Todo todo = Todo.builder().id(1L).title("Task 1").user(mockUser).build();
        when(todoRepository.findByIdAndUserId(1L, mockUser.getId())).thenReturn(Optional.of(todo));

        TodoResponse response = todoService.getTodoById(1L);

        assertEquals("Task 1", response.getTitle());
    }

    @Test
    void shouldThrowWhenTodoNotFound() {
        when(todoRepository.findByIdAndUserId(1L, mockUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> todoService.getTodoById(1L));
    }

    @Test
    void shouldAddTodo() {
        TodoRequest request = new TodoRequest();
        request.setTitle("New Task");
        request.setDueDate(LocalDate.now());

        Todo todo = Todo.builder().title("New Task").user(mockUser).build();
        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        TodoResponse response = todoService.addTodo(request);

        assertEquals("New Task", response.getTitle());
    }

    @Test
    void shouldUpdateTodo() {
        TodoRequest request = new TodoRequest();
        request.setTitle("Updated Task");
        request.setDueDate(LocalDate.now());

        Todo existingTodo = Todo.builder().id(1L).title("Old Task").user(mockUser).build();
        when(todoRepository.findByIdAndUserId(1L, mockUser.getId())).thenReturn(Optional.of(existingTodo));
        when(todoRepository.save(any(Todo.class))).thenReturn(existingTodo);

        TodoResponse response = todoService.updateTodo(1L, request);

        assertEquals("Updated Task", response.getTitle());
    }

    @Test
    void shouldDeleteTodo() {
        Todo todo = Todo.builder().id(1L).title("Task to Delete").user(mockUser).build();
        when(todoRepository.findByIdAndUserId(1L, mockUser.getId())).thenReturn(Optional.of(todo));

        todoService.deleteTodo(1L);

        verify(todoRepository, times(1)).delete(todo);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        TodoRequest request = new TodoRequest();
        request.setTitle("Task");
        request.setDueDate(LocalDate.now());

        when(userRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> todoService.addTodo(request));
    }
}
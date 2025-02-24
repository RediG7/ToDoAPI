package com.guleksiredi.todo.service;

import com.guleksiredi.todo.dto.request.TodoRequest;
import com.guleksiredi.todo.dto.response.TodoResponse;
import com.guleksiredi.todo.entity.Todo;
import com.guleksiredi.todo.entity.User;
import com.guleksiredi.todo.exception.service.ResourceNotFoundException;
import com.guleksiredi.todo.repository.TodoRepository;
import com.guleksiredi.todo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    public List<TodoResponse> getUserTodos() {
        Long userId = getCurrentUserId();
        return todoRepository.findByUserId(userId).stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    public TodoResponse getTodoById(Long id) {
        Long userId = getCurrentUserId();
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo with ID %s was not found".formatted(id)));
        return mapToTodoResponse(todo);
    }

    public TodoResponse addTodo(TodoRequest todoRequest) {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID %s was not found".formatted(userId)));

        Todo todo = Todo.builder()
                .title(todoRequest.getTitle())
                .description(todoRequest.getDescription())
                .dueDate(todoRequest.getDueDate())
                .completed(todoRequest.isCompleted())
                .user(user)
                .build();

        return mapToTodoResponse(todoRepository.save(todo));
    }

    public TodoResponse updateTodo(Long id, TodoRequest todoRequest) {
        Long userId = getCurrentUserId();
        Todo existingTodo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo with ID %s was not found".formatted(id)));
        existingTodo.setTitle(todoRequest.getTitle());
        existingTodo.setDescription(todoRequest.getDescription());
        existingTodo.setDueDate(todoRequest.getDueDate());
        existingTodo.setCompleted(todoRequest.isCompleted());

        return mapToTodoResponse(todoRepository.save(existingTodo));
    }

    public void deleteTodo(Long id) {
        Long userId = getCurrentUserId();
        Todo todo = todoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Todo with ID %s was not found".formatted(id)));
        todoRepository.delete(todo);
    }

    public List<TodoResponse> getAllTodos() {
        if (!isCurrentUserAdmin()) {
            throw new RuntimeException("You do not have permission to view all todos");
        }
        return todoRepository.findAll().stream()
                .map(this::mapToTodoResponse)
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }
        throw new RuntimeException("User ID not found in security context");
    }

    private boolean isCurrentUserAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    private TodoResponse mapToTodoResponse(Todo todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .dueDate(todo.getDueDate())
                .completed(todo.isCompleted())
                .build();
    }
}


package com.guleksiredi.todo.controller;

import com.guleksiredi.todo.dto.request.TodoRequest;
import com.guleksiredi.todo.dto.response.TodoResponse;
import com.guleksiredi.todo.service.TodoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@Tag(name = "Todo Controller", description = "Manage todos for users and admins")
@SecurityRequirement(name = "basicAuth")
@RestController
@RequestMapping(value = "api/v1/todo", produces = MediaType.APPLICATION_JSON_VALUE)
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<TodoResponse>> getUserTodos() {
        List<TodoResponse> todos = todoService.getUserTodos();
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TodoResponse> getTodoById(
            @PathVariable("id")
            @Positive(message = "ID must be positive") Long id) {
        TodoResponse todo = todoService.getTodoById(id);
        return ResponseEntity.ok(todo);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(
            @Valid @RequestBody TodoRequest todoRequest
    ) {
        TodoResponse createdTodo = todoService.addTodo(todoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(
            @PathVariable("id")
            @Positive(message = "ID must be positive") Long id,
            @Valid @RequestBody TodoRequest todoRequest) {
        TodoResponse updatedTodo = todoService.updateTodo(id, todoRequest);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(
            @PathVariable("id")
            @Positive(message = "ID must be positive") Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<TodoResponse>> getAllTodos() {
        List<TodoResponse> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }
}

package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.ToDo;
import play.libs.Json;
import play.mvc.*;
import repositories.ToDoRepository;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class ToDoController extends Controller {

    private final ToDoRepository repository;

    @Inject
    public ToDoController(ToDoRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Result> getTodos() {
        return repository.getAll().thenApply(todos -> ok(Json.toJson(todos)));
    }

    public CompletionStage<Result> searchTodos(String query, String priority, Long categoryId, boolean completed) {
        if (query.isEmpty()) query = null;
        if (priority.isEmpty()) priority = null;
        if (categoryId == -1) categoryId = null;

        return repository.search(query, priority, categoryId, completed)
                .thenApply(todos -> ok(Json.toJson(todos)));
    }

    public CompletionStage<Result> addTodo(Http.Request request) {
        JsonNode json = request.body().asJson();
        ToDo todo = Json.fromJson(json, ToDo.class);
        if (todo.title == null || todo.title.trim().isEmpty()) {
            return CompletableFuture.completedFuture(badRequest("Title is required"));
        }
        return repository.add(todo).thenApply(savedTodo -> ok(Json.toJson(savedTodo)));
    }

    public CompletionStage<Result> updateTodo(Long id, Http.Request request) {
        JsonNode json = request.body().asJson();
        ToDo todo = Json.fromJson(json, ToDo.class);
        todo.id = id; // Pastikan ID ToDo sesuai dengan parameter
        return repository.update(todo).thenApply(updatedTodo -> ok(Json.toJson(updatedTodo)));
    }

    public CompletionStage<Result> deleteTodo(Long id) {
        return repository.delete(id).thenApply(deleted -> {
            if (deleted) {
                return ok("ToDo deleted");
            } else {
                return notFound("ToDo not found");
            }
        });
    }
}
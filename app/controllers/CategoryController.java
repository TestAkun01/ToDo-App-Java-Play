package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Category;
import play.libs.Json;
import play.mvc.*;
import repositories.CategoryRepository;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CategoryController extends Controller {

    private final CategoryRepository repository;

    @Inject
    public CategoryController(CategoryRepository repository) {
        this.repository = repository;
    }

    // Mendapatkan semua kategori
    public CompletionStage<Result> getAll() {
        return repository.getAll().thenApply(categories -> ok(Json.toJson(categories)));
    }

    // Mendapatkan kategori berdasarkan ID
    public CompletionStage<Result> getById(Long id) {
        return repository.getById(id).thenApply(category -> {
            if (category != null) {
                return ok(Json.toJson(category));
            } else {
                return notFound("Category not found");
            }
        });
    }

    // Menambahkan kategori baru
    public CompletionStage<Result> add(Http.Request request) {
        JsonNode json = request.body().asJson();
        Category category = Json.fromJson(json, Category.class);
        if (category.name == null || category.name.trim().isEmpty()) {
            return CompletableFuture.completedFuture(badRequest("Name is required"));
        }
        return repository.add(category).thenApply(savedCategory -> ok(Json.toJson(savedCategory)));
    }

    // Memperbarui kategori
    public CompletionStage<Result> update(Long id, Http.Request request) {
        JsonNode json = request.body().asJson();
        Category category = Json.fromJson(json, Category.class);
        category.id = id; // Pastikan ID kategori sesuai dengan parameter
        return repository.update(category).thenApply(updatedCategory -> ok(Json.toJson(updatedCategory)));
    }

    // Menghapus kategori
    public CompletionStage<Result> delete(Long id) {
        return repository.delete(id).thenApply(deleted -> {
            if (deleted) {
                return ok("Category deleted");
            } else {
                return notFound("Category not found");
            }
        });
    }
}
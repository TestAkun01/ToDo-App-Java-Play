package repositories;

import models.Category;
import play.db.Database;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class CategoryRepository {

    private final Database db;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public CategoryRepository(Database db, DatabaseExecutionContext executionContext) {
        this.db = db;
        this.executionContext = executionContext;
    }

    // Mendapatkan semua kategori
    public CompletionStage<List<Category>> getAll() {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    List<Category> categories = new ArrayList<>();
                    try (Statement stmt = connection.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT * FROM category")) {
                        while (rs.next()) {
                            categories.add(new Category(rs.getLong("id"), rs.getString("name")));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return categories;
                }), executionContext);
    }

    // Mendapatkan kategori berdasarkan ID
    public CompletionStage<Category> getById(Long id) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "SELECT * FROM category WHERE id = ?")) {
                        stmt.setLong(1, id);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                return new Category(rs.getLong("id"), rs.getString("name"));
                            } else {
                                return null;
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }), executionContext);
    }

    // Menambahkan kategori baru
    public CompletionStage<Category> add(Category category) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "INSERT INTO category (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, category.name);
                        stmt.executeUpdate();
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                category.id = rs.getLong(1);
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return category;
                }), executionContext);
    }

    // Memperbarui kategori
    public CompletionStage<Category> update(Category category) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "UPDATE category SET name = ? WHERE id = ?")) {
                        stmt.setString(1, category.name);
                        stmt.setLong(2, category.id);
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return category;
                }), executionContext);
    }

    // Menghapus kategori
    public CompletionStage<Boolean> delete(Long id) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "DELETE FROM category WHERE id = ?")) {
                        stmt.setLong(1, id);
                        return stmt.executeUpdate() > 0;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }), executionContext);
    }
}
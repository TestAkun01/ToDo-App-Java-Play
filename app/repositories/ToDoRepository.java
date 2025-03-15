package repositories;

import models.ToDo;
import play.db.Database;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ToDoRepository {

    private final Database db;
    private final DatabaseExecutionContext executionContext;

    @Inject
    public ToDoRepository(Database db, DatabaseExecutionContext executionContext) {
        this.db = db;
        this.executionContext = executionContext;
    }

    public CompletionStage<List<ToDo>> getAll() {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    List<ToDo> todos = new ArrayList<>();
                    try (Statement stmt = connection.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT * FROM todo")) {
                        while (rs.next()) {
                            todos.add(new ToDo(
                                    rs.getLong("id"),
                                    rs.getString("title"),
                                    rs.getBoolean("completed"),
                                    rs.getString("priority"),
                                    rs.getDate("deadline"),
                                    rs.getLong("categoryId")
                            ));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return todos;
                }), executionContext);
    }

    public CompletionStage<List<ToDo>> search(String query, String priority, Long categoryId, boolean completed) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    List<ToDo> todos = new ArrayList<>();
                    String sql = "SELECT * FROM todo WHERE 1=1";
                    if (query != null) sql += " AND title LIKE ?";
                    if (priority != null) sql += " AND priority = ?";
                    if (categoryId != null) sql += " AND categoryId = ?";
                    if (completed) sql += " AND completed = ?";

                    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                        int index = 1;
                        if (query != null) stmt.setString(index++, "%" + query + "%");
                        if (priority != null) stmt.setString(index++, priority);
                        if (categoryId != null) stmt.setLong(index++, categoryId);
                        if (completed) stmt.setBoolean(index++, true);

                        try (ResultSet rs = stmt.executeQuery()) {
                            while (rs.next()) {
                                todos.add(new ToDo(
                                        rs.getLong("id"),
                                        rs.getString("title"),
                                        rs.getBoolean("completed"),
                                        rs.getString("priority"),
                                        rs.getDate("deadline"),
                                        rs.getLong("categoryId")
                                ));
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return todos;
                }), executionContext);
    }

    public CompletionStage<ToDo> add(ToDo todo) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "INSERT INTO todo (title, completed, priority, deadline, categoryId) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                        stmt.setString(1, todo.title);
                        stmt.setBoolean(2, todo.completed);
                        stmt.setString(3, todo.priority);
                        stmt.setDate(4, new java.sql.Date(todo.deadline.getTime()));
                        stmt.setLong(5, todo.categoryId);
                        stmt.executeUpdate();
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                todo.id = rs.getLong(1);
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return todo;
                }), executionContext);
    }

    public CompletionStage<ToDo> update(ToDo todo) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "UPDATE todo SET title = ?, completed = ?, priority = ?, deadline = ?, categoryId = ? WHERE id = ?")) {
                        stmt.setString(1, todo.title);
                        stmt.setBoolean(2, todo.completed);
                        stmt.setString(3, todo.priority);
                        stmt.setDate(4, new java.sql.Date(todo.deadline.getTime()));
                        stmt.setLong(5, todo.categoryId);
                        stmt.setLong(6, todo.id);
                        stmt.executeUpdate();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return todo;
                }), executionContext);
    }

    public CompletionStage<Boolean> delete(Long id) {
        return CompletableFuture.supplyAsync(() ->
                db.withConnection(connection -> {
                    try (PreparedStatement stmt = connection.prepareStatement(
                            "DELETE FROM todo WHERE id = ?")) {
                        stmt.setLong(1, id);
                        return stmt.executeUpdate() > 0;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }), executionContext);
    }
}
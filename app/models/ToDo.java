package models;

import play.data.validation.Constraints;
import java.io.Serializable;
import java.util.Date;

public class ToDo implements Serializable {
    public Long id;

    @Constraints.Required(message = "Title is required")
    @Constraints.MinLength(value = 3, message = "Title must be at least 3 characters")
    public String title;

    public boolean completed;

    @Constraints.Required(message = "Priority is required")
    public String priority;

    public Date deadline;

    public Long categoryId;

    public ToDo(Long id, String title, boolean completed, String priority, Date deadline, Long categoryId) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.priority = priority;
        this.deadline = deadline;
        this.categoryId = categoryId;
    }
}
CREATE TABLE tasks (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    board_column_id UUID NOT NULL,
    title VARCHAR(160) NOT NULL,
    description VARCHAR(2000),
    priority VARCHAR(30) NOT NULL,
    created_by UUID NOT NULL,
    assigned_to UUID,
    position INTEGER NOT NULL,
    due_date TIMESTAMP,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_tasks_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_tasks_board_column
        FOREIGN KEY (board_column_id)
        REFERENCES board_columns(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_tasks_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT fk_tasks_assigned_to
        FOREIGN KEY (assigned_to)
        REFERENCES users(id)
);

CREATE INDEX idx_tasks_project_id ON tasks(project_id);
CREATE INDEX idx_tasks_board_column_id ON tasks(board_column_id);
CREATE INDEX idx_tasks_created_by ON tasks(created_by);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to);
CREATE INDEX idx_tasks_archived ON tasks(archived);
CREATE INDEX idx_tasks_position ON tasks(position);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
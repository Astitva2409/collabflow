CREATE TABLE task_comments (
    id UUID PRIMARY KEY,
    task_id UUID NOT NULL,
    author_id UUID NOT NULL,
    content VARCHAR(2000) NOT NULL,
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_task_comments_task
        FOREIGN KEY (task_id)
        REFERENCES tasks(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_task_comments_author
        FOREIGN KEY (author_id)
        REFERENCES users(id)
);

CREATE INDEX idx_task_comments_task_id ON task_comments(task_id);
CREATE INDEX idx_task_comments_author_id ON task_comments(author_id);
CREATE INDEX idx_task_comments_archived ON task_comments(archived);
CREATE INDEX idx_task_comments_created_at ON task_comments(created_at);
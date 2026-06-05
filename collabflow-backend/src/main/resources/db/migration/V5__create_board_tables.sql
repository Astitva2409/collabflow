CREATE TABLE boards (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_boards_project
        FOREIGN KEY (project_id)
        REFERENCES projects(id)
        ON DELETE CASCADE
);

CREATE TABLE board_columns (
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL,
    name VARCHAR(80) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_board_columns_board
        FOREIGN KEY (board_id)
        REFERENCES boards(id)
        ON DELETE CASCADE,

    CONSTRAINT uk_board_columns_board_position
        UNIQUE (board_id, position),

    CONSTRAINT uk_board_columns_board_name
        UNIQUE (board_id, name)
);

CREATE INDEX idx_boards_project_id ON boards(project_id);
CREATE INDEX idx_board_columns_board_id ON board_columns(board_id);
package com.astitva.collabflow.workspace.entity;

import com.astitva.collabflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "workspace_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workspace_members_workspace_user",
                        columnNames = {"workspace_id", "user_id"}
                )
        }
)
public class WorkspaceMember {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkspaceRole role;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (role == null) {
            role = WorkspaceRole.MEMBER;
        }

        joinedAt = LocalDateTime.now();
    }
}
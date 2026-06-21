package com.astitva.collabflow.activity.entity;

/**
 * Represents the type of activity performed in the system.
 */
public enum ActivityType {

    WORKSPACE_CREATED,

    MEMBER_ADDED,
    MEMBER_ROLE_UPDATED,
    MEMBER_REMOVED,

    PROJECT_CREATED,
    PROJECT_UPDATED,
    PROJECT_ARCHIVED,

    TASK_CREATED,
    TASK_UPDATED,
    TASK_MOVED,
    TASK_ASSIGNED,
    TASK_UNASSIGNED,
    TASK_ARCHIVED,

    COMMENT_ADDED,
    COMMENT_DELETED
}
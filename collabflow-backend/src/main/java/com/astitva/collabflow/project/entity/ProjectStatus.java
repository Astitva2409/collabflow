package com.astitva.collabflow.project.entity;

/**
 * Represents current lifecycle state of a project.
 */
public enum ProjectStatus {

    /**
     * Project is active and work is ongoing.
     */
    ACTIVE,

    /**
     * Project is temporarily paused.
     */
    ON_HOLD,

    /**
     * Project work has been completed.
     */
    COMPLETED,

    /**
     * Project is archived and hidden from active project lists.
     */
    ARCHIVED
}
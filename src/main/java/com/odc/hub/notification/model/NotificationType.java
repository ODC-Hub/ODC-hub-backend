package com.odc.hub.notification.model;

public enum NotificationType {
    // Planning
    PLANNING_ITEM_CREATED,
    PLANNING_ITEM_UPDATED,
    PLANNING_ITEM_DELETED,

    QUIZ_SUBMITTED,
    QUIZ_GRADED,

    // Resources / Homework
    RESOURCE_CREATED,
    HOMEWORK_ASSIGNED,
    HOMEWORK_SUBMITTED,
    HOMEWORK_REVIEWED,

    PROJECT_ASSIGNED,

    USER_REGISTRATION_REQUESTED

}
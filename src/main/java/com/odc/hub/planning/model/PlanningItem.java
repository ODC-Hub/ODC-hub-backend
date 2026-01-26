package com.odc.hub.planning.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "planning_items")
public class PlanningItem {

    public PlanningItem() {
    }

    public PlanningItem(String id, String title, String description, PlanningItemType type, LocalDateTime startDate,
            LocalDateTime endDate, List<String> userIds, List<String> tags, String createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userIds = userIds;
        this.tags = tags;
        this.createdBy = createdBy;
    }

    public static PlanningItemBuilder builder() {
        return new PlanningItemBuilder();
    }

    public static class PlanningItemBuilder {
        private String id;
        private String title;
        private String description;
        private PlanningItemType type;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private List<String> userIds;
        private List<String> tags;
        private String createdBy;

        public PlanningItemBuilder id(String id) {
            this.id = id;
            return this;
        }

        public PlanningItemBuilder title(String title) {
            this.title = title;
            return this;
        }

        public PlanningItemBuilder description(String description) {
            this.description = description;
            return this;
        }

        public PlanningItemBuilder type(PlanningItemType type) {
            this.type = type;
            return this;
        }

        public PlanningItemBuilder startDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public PlanningItemBuilder endDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public PlanningItemBuilder userIds(List<String> userIds) {
            this.userIds = userIds;
            return this;
        }

        public PlanningItemBuilder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public PlanningItemBuilder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public PlanningItem build() {
            return new PlanningItem(id, title, description, type, startDate, endDate, userIds, tags, createdBy);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PlanningItemType getType() {
        return type;
    }

    public void setType(PlanningItemType type) {
        this.type = type;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Id
    private String id;

    private String title;
    private String description;

    private PlanningItemType type; // SESSION, EVENT, DEADLINE

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Field("userIds")
    private List<String> userIds;

    private List<String> tags;

    private String createdBy; // admin / formateur
}

package com.odc.hub.filrouge.model;

import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.enums.WorkItemType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "work_items")
public class WorkItemDocument {

    @Id
    private String id;

    private String projectId;
    private String sprintId;

    private String title;
    private String description;

    private WorkItemType type;
    private WorkItemStatus status;

    private Integer effort;
    private Instant deadline;

    private List<String> assignedUserIds;

    private Integer carryCount;

}

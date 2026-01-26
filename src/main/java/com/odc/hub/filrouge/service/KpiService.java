package com.odc.hub.filrouge.service;

import com.odc.hub.filrouge.dto.ProjectKpiResponse;
import com.odc.hub.filrouge.dto.SprintKpiResponse;
import com.odc.hub.filrouge.enums.WorkItemStatus;
import com.odc.hub.filrouge.model.SprintDocument;
import com.odc.hub.filrouge.model.WorkItemDocument;
import com.odc.hub.filrouge.repository.SprintRepository;
import com.odc.hub.filrouge.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiService {

    private final SprintRepository sprintRepository;
    private final WorkItemRepository workItemRepository;

    /* PROJECT KPIs */
    public ProjectKpiResponse computeProjectKpis(String projectId) {

        List<SprintDocument> sprints =
                sprintRepository.findByProjectId(projectId);

        List<SprintKpiResponse> sprintKpis = new ArrayList<>();

        int totalPlanned = 0;
        int totalDone = 0;

        for (SprintDocument sprint : sprints) {

            SprintKpiResponse sprintKpi = computeSprintKpi(sprint);
            sprintKpis.add(sprintKpi);

            totalPlanned += sprintKpi.plannedEffort();
            totalDone += sprintKpi.completedEffort();
        }

        double globalProgress =
                totalPlanned == 0 ? 0 : (double) totalDone / totalPlanned * 100;

        return new ProjectKpiResponse(projectId, globalProgress, sprintKpis);
    }

    /* SPRINT KPIs */
    public SprintKpiResponse computeSprintKpi(SprintDocument sprint) {

        List<WorkItemDocument> items =
                workItemRepository.findBySprintId(sprint.getId());

        int doneEffort = items.stream()
                .filter(i -> i.getStatus() == WorkItemStatus.DONE)
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        int overdueCount = (int) items.stream()
                .filter(i ->
                        i.getDeadline().isBefore(Instant.now()) &&
                                i.getStatus() != WorkItemStatus.DONE
                )
                .count();

        double progress =
                sprint.getPlannedEffort() == 0 ? 0 :
                        (double) doneEffort / sprint.getPlannedEffort() * 100;

        double riskScore = calculateRiskScore(items);

        return new SprintKpiResponse(
                sprint.getId(),
                sprint.getPlannedEffort(),
                doneEffort,
                progress,
                overdueCount,
                riskScore
        );
    }

    /* SIMPLE & EXPLAINABLE RISK SCORE */
    private double calculateRiskScore(List<WorkItemDocument> items) {

        int totalEffort = items.stream()
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        int lateEffort = items.stream()
                .filter(i ->
                        i.getDeadline().isBefore(Instant.now()) &&
                                i.getStatus() != WorkItemStatus.DONE
                )
                .mapToInt(WorkItemDocument::getEffort)
                .sum();

        if (totalEffort == 0) return 0;

        return (double) lateEffort / totalEffort * 100;
    }
}

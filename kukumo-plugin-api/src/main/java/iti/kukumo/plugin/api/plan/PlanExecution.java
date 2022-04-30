package iti.kukumo.plugin.api.plan;

import java.time.Instant;

public record PlanExecution(
    ExecutionMetadata metadata,
    PlanNode testPlan
) {

    public record ExecutionMetadata(
        String executionID,
        String launchedBy,
        Instant launchedAt
    ) { }

}

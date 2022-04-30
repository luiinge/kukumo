package iti.kukumo.core.runner;

import iti.kukumo.core.backend.BackendFactory;
import iti.kukumo.core.concurrency.ExecutorProvider;
import iti.kukumo.plugin.api.plan.PlanExecution;
import java.time.Clock;

public record RunnerContext(
    PlanExecution.ExecutionMetadata metadata,
    BackendFactory backendFactory,
    ExecutorProvider executorProvider,
    Clock clock
) { }

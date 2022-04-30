package iti.kukumo.core.runner;

import iti.kukumo.core.ExecutablePlanNode;
import iti.kukumo.core.backend.*;
import iti.kukumo.plugin.api.plan.*;
import java.util.List;
import java.util.concurrent.*;
import org.opentest4j.*;

public class PlanNodeRunner {

    protected final ExecutablePlanNode node;
    protected final RunnerContext context;
    protected final Executor executor;

    protected Backend backend;
    protected List<PlanNodeRunner> children; // lazy


    public PlanNodeRunner(
        RunnerContext context,
        ExecutablePlanNode node,
        Backend parentBackend
    ) {
        this.node = node;
        this.context = context;
        this.backend = parentBackend;
        this.executor = context.executorProvider().executorForNode(node);
    }


    public PlanNodeRunner(RunnerContext context, ExecutablePlanNode node) {
        this(context, node, null);
    }


    protected void createChildren() {
        if (children == null) {
            children = node.children().stream().map(
                child -> new PlanNodeRunner(context, child, backend)
            ).toList();
        }
    }


    protected void createBackend() {
        if (backend == null && node.nodeType() == NodeType.TEST_CASE) {
            backend = context.backendFactory().createBackend(node);
        }
    }


    public PlanNode run() {
        runNode().join();
        return node.snapshot();
    }


    protected CompletableFuture<Void> runNode() {
        createChildren();
        if (node.nodeType() == NodeType.TEST_CASE) {
            createBackend();
        }
        var preExecution = runAsync(this::preExecution);
        var execution = switch (node.nodeType()) {
            case VIRTUAL_STEP -> runAsync(this::runVirtualStep);
            case STEP ->runAsync(this::runStep);
            default -> runChildren();
        };
        var postExecution = runAsync(this::postExecution);
        return preExecution
            .thenCompose(it -> execution)
            .thenCompose(it -> postExecution);
    }



    protected CompletableFuture<Void> runChildren() {
        return CompletableFuture.allOf(
            children.stream().map(PlanNodeRunner::runNode).toArray(CompletableFuture[]::new)
        );
    }



    protected CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }




    protected void runVirtualStep() {
        var startInstant = context.clock().instant();
        node.markStarted(startInstant);
        node.markFinished(startInstant, Result.PASSED);
    }


    protected void runStep() {
        node.markStarted(context.clock().instant());
        try {
            backend.executeStep(node);
            node.markFinished(context.clock().instant(), Result.PASSED);
        } catch (AssertionFailedError e) {
            node.markFinished(context.clock().instant(), Result.FAILED, e);
        } catch (TestSkippedException e) {
            node.markFinished(context.clock().instant(), Result.SKIPPED);
        } catch (IncompleteExecutionException e) {
            node.markFinished(context.clock().instant(), Result.UNDEFINED);
        } catch (Throwable e) {
            node.markFinished(context.clock().instant(), Result.ERROR, e);
        }
    }



    protected void preExecution() {
        if (node.nodeType() == NodeType.TEST_CASE) {
            backend.setUp();
        }
    }


    protected void postExecution() {
        if (node.nodeType() == NodeType.TEST_CASE) {
            backend.tearDown();
        }
    }


}

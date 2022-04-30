package iti.kukumo.core.backend;

import imconfig.Config;
import iti.kukumo.core.ExecutablePlanNode;
import iti.kukumo.plugin.api.*;
import iti.kukumo.plugin.api.plan.NodeType;
import org.jexten.ExtensionManager;

public class DefaultBackendFactory implements BackendFactory {

    private final ExtensionManager extensionManager;
    private final Config config;
    private final Contributions contributions;


    public DefaultBackendFactory(
        Config config,
        Contributions contributions,
        ExtensionManager extensionManager
    ) {
        this.extensionManager = extensionManager;
        this.config = config;
        this.contributions = contributions;
    }


    @Override
    public Backend createBackend(ExecutablePlanNode testCaseNode) {
        if (testCaseNode.nodeType() != NodeType.TEST_CASE) {
            throw new IllegalArgumentException("PlanNode must be of type TEST_CASE");
        }
        return new DefaultBackend(
            config,
            testCaseNode,
            extensionManager,
            contributions
        );
    }

}

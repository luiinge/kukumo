package iti.kukumo.core.backend;

import iti.kukumo.core.ExecutablePlanNode;

public interface BackendFactory {

    Backend createBackend(ExecutablePlanNode testCaseNode);

}

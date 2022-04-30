package iti.kukumo.core.backend;

import iti.kukumo.core.ExecutablePlanNode;
import org.opentest4j.TestAbortedException;

public interface Backend {

    void executeStep(ExecutablePlanNode node) throws Throwable;

    void setUp() throws TestAbortedException;

    void tearDown() throws TestAbortedException;

}

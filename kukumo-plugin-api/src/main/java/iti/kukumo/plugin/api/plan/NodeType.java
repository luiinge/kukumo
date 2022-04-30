package iti.kukumo.plugin.api.plan;

public enum NodeType {

    /** Regular node that aggregates other nodes. */
    AGGREGATOR,

    /** Root node for an individual test case. */
    TEST_CASE,

    /** Aggregator node within a test case. */
    STEP_AGGREGATOR,

    /** Executable final node within a test case. Cannot have children. */
    STEP,

    /** Non-executable final node within a test case. Cannot have children. */
    VIRTUAL_STEP



}

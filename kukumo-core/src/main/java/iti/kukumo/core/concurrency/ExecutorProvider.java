package iti.kukumo.core.concurrency;

import iti.kukumo.core.ExecutablePlanNode;
import java.util.concurrent.*;

public interface ExecutorProvider {

    Executor executorForNode(ExecutablePlanNode node);

}

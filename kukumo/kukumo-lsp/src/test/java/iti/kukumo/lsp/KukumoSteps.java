/**
 * @author Luis Iñesta Gelabert - luiinge@gmail.com
 */
package iti.kukumo.lsp;


import iti.kukumo.api.annotations.*;
import iti.kukumo.api.datatypes.Assertion;
import iti.kukumo.api.extensions.StepContributor;


@I18nResource("test-kukumo-steps")
public class KukumoSteps implements StepContributor {


    @Step(value = "given.set.of.numbers")
    public void setOfNumbers() {

    }


    @Step(value = "simple.step.with.multiple.asserts", args = { "a:integer-assertion", "b:integer",
                    "c:text-assertion" })
    public void simpleStepWithMultipleAsserts(Assertion<Integer> a, Long b, Assertion<String> c) {
        // nothing
    }

}
package iti.kukumo.plugins.io;

import iti.kukumo.plugin.api.Log;
import iti.kukumo.plugin.api.annotations.*;
import iti.kukumo.plugin.api.contributions.StepContribution;
import org.jexten.*;

@Extension(extensionPointVersion = "2.0", scope = Scope.LOCAL)
@LocalizableWith(IOStepsLocalization.class)
public class IOSteps implements StepContribution {

    final Log log = Log.of("plugins:io");

    Long timeout;


    @Step("io.define.timeout")
    void setTimeout(@Param("value") Long value) {
        this.timeout = value;
    }

}

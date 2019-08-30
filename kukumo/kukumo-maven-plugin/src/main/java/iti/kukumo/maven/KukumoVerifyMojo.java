package iti.kukumo.maven;

import iti.commons.configurer.Configuration;
import iti.commons.configurer.ConfigurationException;
import iti.kukumo.api.Kukumo;
import iti.kukumo.api.KukumoException;
import iti.kukumo.api.plan.PlanNode;
import iti.kukumo.api.plan.Result;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mojo(name = "verify", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class KukumoVerifyMojo extends AbstractMojo implements KukumoConfigurable {

    @Parameter
    public boolean skipTests;

    @Parameter
    public Map<String, String> properties;

    @Parameter
    public List<String> configurationFiles;

    @Parameter(defaultValue = "info")
    public String logLevel;



    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        System.setProperty("org.slf4j.simpleLogger.log.iti.kukumo",logLevel);

        if (skipTests) {
            info("Kukumo tests skipped");
            return;
        }

        Configuration configuration;
        try {
            // replace null properties for empty values
            for (String key : properties.keySet()) {
                properties.putIfAbsent(key, "");
            }

            configuration = readConfiguration(configurationFiles, properties);
            PlanNode plan = Kukumo.createPlanFromConfiguration(configuration);
            if (!plan.hasChildren()) {
                warn("Test Plan is empty!");
            } else {
                Optional<Result> result = Kukumo.executePlan(plan, configuration).computeResult();
                if (result.isPresent()) {
                    if (result.get() == Result.PASSED) {
                    } else {
                        throw new MojoFailureException("Kukumo Test Plan not passed");
                    }
                }
            }
        } catch (KukumoException e) {
            throw new MojoFailureException(e.getMessage());
        } catch (IOException e) {
            throw new MojoExecutionException("Kukumo reporting error: " + e.getMessage(), e);
        } catch (ConfigurationException e) {
            throw new MojoExecutionException("Kukumo configuration error: " + e.getMessage(), e);
        }

    }


}

/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.maven;


import imconfig.Configuration;
import iti.kukumo.core.Kukumo;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Mojo(name = "report", defaultPhase = LifecyclePhase.VERIFY)
public class KukumoReporterMojo extends AbstractMojo implements KukumoConfigurable {

    @Parameter
    public Map<String, String> properties = new LinkedHashMap<>();

    @Parameter
    public List<String> configurationFiles = new LinkedList<>();

    @Parameter
    public boolean testFailureIgnore;

    /**
     * The current build session instance.
     */
    @Parameter(defaultValue = "${session}", required = true, readonly = true)
    private MavenSession session;


    @Override
    public void execute() {
        try {
            Configuration configuration = readConfiguration(configurationFiles, properties);
            info("invoking reports...");
            Kukumo.instance().generateReports(configuration);
        } catch (Exception e) {
            if (!testFailureIgnore)
                session.getResult().addException(new MojoExecutionException("Kukumo configuration error: " + e.getMessage(), e));
        }
    }

}
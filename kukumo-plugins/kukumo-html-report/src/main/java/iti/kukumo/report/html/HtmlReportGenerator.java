/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package iti.kukumo.report.html;

import ch.simschla.minify.css.CssMin;
import ch.simschla.minify.js.JsMin;
import freemarker.template.*;
import iti.commons.jext.Extension;
import iti.kukumo.api.KukumoAPI;
import iti.kukumo.api.extensions.Reporter;
import iti.kukumo.api.plan.PlanNodeSnapshot;
import iti.kukumo.api.plan.Result;
import iti.kukumo.api.util.KukumoLogger;
import iti.kukumo.report.html.factory.DurationTemplateNumberFormatFactory;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.UnaryOperator;

import static iti.kukumo.report.html.HtmlReportGeneratorConfig.*;


/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
@Extension(provider = "iti.kukumo", name = "html-report", version = "1.2")
public class HtmlReportGenerator implements Reporter {

    private static final Logger LOGGER = KukumoLogger.forClass(HtmlReportGenerator.class);

    private final Configuration templateConfiguration;
    private String cssFile;
    private String outputFile;
    private String title;
    private Map<String, Object> parameters;

    public HtmlReportGenerator() {
        templateConfiguration = new Configuration(Configuration.VERSION_2_3_29);
        templateConfiguration.setDefaultEncoding("UTF-8");
        templateConfiguration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        templateConfiguration.setLogTemplateExceptions(true);
        templateConfiguration.setWrapUncheckedExceptions(true);
        templateConfiguration.setFallbackOnNullLoopVariable(false);
        templateConfiguration.setClassLoaderForTemplateLoading(classLoader(), "/");

        templateConfiguration.setCustomNumberFormats(
                Collections.singletonMap("duration", DurationTemplateNumberFormatFactory.INSTANCE));
    }

    private static ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    void setCssFile(String cssFile) {
        this.cssFile = cssFile;
    }

    void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    void setTitle(String title) {
        this.title = title;
    }

    public void setConfiguration(imconfig.Configuration configuration) {
        configuration.get(CSS_FILE, String.class).ifPresent(this::setCssFile);
        configuration.get(OUTPUT_FILE, String.class).ifPresent(this::setOutputFile);
        configuration.get(TITLE, String.class).ifPresent(this::setTitle);
        var reportConfiguration = configuration.inner(PREFIX);
        this.parameters = new HashMap<>(reportConfiguration.asMap());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void report(PlanNodeSnapshot rootNode) {
        try {
            File output = new File(Objects.requireNonNull(
                    this.outputFile,
                    "Output file not configured"
            ));
            parameters.put("globalStyle", readStyles());
            parameters.put("globalScript", readJavascript());
            parameters.put("plan", rootNode);
            parameters.put("title", title);
            parameters.put("version", KukumoAPI.instance().version());
            parameters.put("sum", (TemplateMethodModelEx) args ->
                    ((Map<Result, Long>) ((DefaultMapAdapter) args.get(0)).getWrappedObject()).values().stream()
                            .mapToLong(Number::longValue).sum());

            File parent = output.getCanonicalFile().getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }

            try (var writer = new FileWriter(output, StandardCharsets.UTF_8)) {
                template("report.ftl")
                        .process(parameters, writer);
            }
        } catch (IOException | TemplateException e) {
            LOGGER.error("Error generating HTML report: {}", e.getMessage(), e);
            e.printStackTrace();
        }

    }

    private String readStyles() {
        UnaryOperator<String> readStyle = (resource) -> {
            try (InputStream is = resource(resource)) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                CssMin.builder()
                        .inputStream(is)
                        .outputStream(baos)
                        .build()
                        .minify();

                return baos.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        String localCss = readStyle.apply("lib/normalize.css") + readStyle.apply("lib/global.css");
        if (this.cssFile != null) {
            localCss += readStyle.apply(this.cssFile);
        }
        return localCss;
    }

    private String readJavascript() {
        try (InputStream is = resource("lib/global.js")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            JsMin.builder()
                    .inputStream(is)
                    .outputStream(baos)
                    .build()
                    .minify();

            return baos.toString().replaceAll("[\n\r]", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Template template(String resource) throws IOException {
        return templateConfiguration.getTemplate(resource);
    }

    private InputStream resource(String resource) {
        return classLoader().getResourceAsStream(resource);
    }
}
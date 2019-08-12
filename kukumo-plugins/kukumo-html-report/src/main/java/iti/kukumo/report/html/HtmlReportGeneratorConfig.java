package iti.kukumo.report.html;

import iti.commons.configurer.Configuration;
import iti.commons.jext.Extension;
import iti.kukumo.api.KukumoConfiguration;
import iti.kukumo.api.extensions.Configurator;
import iti.kukumo.util.LocaleLoader;

import java.util.Locale;
import java.util.Optional;

@Extension(
    provider="iti.kukumo",
    name="html-report-config",
    version="1.0",
    extensionPoint="iti.kukumo.api.extensions.Configurator"
)
public class HtmlReportGeneratorConfig implements Configurator<HtmlReportGenerator> {

    public static final String OUTPUT_FILE = "htmlReport.output";
    public static final String CSS_FILE = "htmlReport.css";
    public static final String REPORT_LOCALE = "htmlReport.locale";


    @Override
    public boolean accepts(Object contributor) {
        return HtmlReportGenerator.class.equals(contributor.getClass());
    }


    @Override
    public void configure(HtmlReportGenerator contributor, Configuration configuration) {
        contributor.setCssFile(configuration.get(CSS_FILE,String.class).orElse(null));
        contributor.setOutputFile(configuration.get(OUTPUT_FILE,String.class).orElse("kukumo.html"));
        Optional<String> localeProperty = Optional.ofNullable(
                configuration.get(REPORT_LOCALE,String.class)
                .orElse(configuration.get(KukumoConfiguration.LANGUAGE,String.class).orElse(null))
        );
        contributor.setReportLocale(localeProperty.map(LocaleLoader::forLanguage).orElse(Locale.ENGLISH));
    }


}

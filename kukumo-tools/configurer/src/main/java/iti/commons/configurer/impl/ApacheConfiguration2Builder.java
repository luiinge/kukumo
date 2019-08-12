package iti.commons.configurer.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.BaseConfiguration;
import org.apache.commons.configuration2.CompositeConfiguration;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.JSONConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.convert.ConversionHandler;

import iti.commons.configurer.Configuration;
import iti.commons.configurer.ConfigurationBuilder;
import iti.commons.configurer.ConfigurationException;
import iti.commons.configurer.Configurator;
import iti.commons.configurer.Property;

public class ApacheConfiguration2Builder implements ConfigurationBuilder {



    private final ConversionHandler conversionHandler = new ApacheConfiguration2ConversionHandler();


    @Override
    public Configuration compose(Configuration... configurations) {
        if (configurations == null || configurations.length == 0) {
            return empty();
        }
        AbstractConfiguration configuration = configure(new BaseConfiguration());
        for (int i=0; i<configurations.length; i++) {
            configuration.copy(toImpl(configurations[i]));
        }
        return new ApacheConfiguration2(this,configuration);
    }


    @Override
    public Configuration empty() {
        return new ApacheConfiguration2(this,new BaseConfiguration());
    }


    @Override
    public Configuration buildFromAnnotation(Class<?> configuredClass) {
        return
            Optional.ofNullable(configuredClass.getAnnotation(Configurator.class))
            .map(this::buildFromAnnotation)
            .orElseThrow(()->
                new ConfigurationException(configuredClass+" is not annotated with @Configurator")
            );
    }


    @Override
    public Configuration buildFromAnnotation(Configurator annotation) {
        BaseConfiguration configuration = configure(new BaseConfiguration());
        for (Property property : annotation.properties()) {
            String[] value = property.value();
            if (value.length == 1) {
                configuration.addProperty(property.key(), value[0]);
            } else {
                configuration.addProperty(property.key(), value);
            }
        }
        if (annotation.path() != null && !annotation.path().isEmpty()) {
        	Configuration pathConfiguration = buildFromClasspathResourceOrURI(annotation.path());
        	pathConfiguration = pathConfiguration.inner(annotation.pathPrefix());
            configuration.copy(toImpl(pathConfiguration));
        }
        return new ApacheConfiguration2(this,configuration);

    }


    @Override
    public Configuration buildFromEnvironment(boolean includeSystemProperties) {
        final CompositeConfiguration configuration = configure(new CompositeConfiguration());
        if (includeSystemProperties ) {
            configuration.addConfiguration(new SystemConfiguration());
        }
        configuration.addConfiguration(new EnvironmentConfiguration());
        return new ApacheConfiguration2(this,configuration);
    }


    @Override
    public Configuration buildFromEnvironment() {
        return buildFromEnvironment(false);
    }


    @Override
    public Configuration buildFromPath(Path path) {
        return buildFromURI(path.toUri());
    }


    @Override
    public Configuration buildFromClasspathResourceOrURI(String path) {
        if (path.startsWith("classpath:")) {
            return buildFromClasspathResource(path.substring("classpath:".length()));
        } else {
            return buildFromURI(URI.create(path));
        }
    }


    @Override
    public Configuration buildFromProperties(Properties properties) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<Object, Object> property : properties.entrySet()) {
            configuration.addProperty(property.getKey().toString(), property.getValue());
        }
        return new ApacheConfiguration2(this,configuration);
    }


    @Override
    public Configuration buildFromMap(Map<String,?> properties) {
        final BaseConfiguration configuration = configure(new BaseConfiguration());
        for (final Entry<String, ?> property : properties.entrySet()) {
            configuration.addProperty(property.getKey(), property.getValue());
        }
        return new ApacheConfiguration2(this,configuration);
    }


    @Override
    public Configuration buildFromClasspathResource(String resourcePath, ClassLoader classLoader) {
        try {
            BaseConfiguration configuration = configure(new BaseConfiguration());
            List<Configuration> urlConfs = buildFromURLEnum(
                classLoader.getResources(resourcePath),
                resourcePath
            );
            for (Configuration urlConf : urlConfs) {
                configuration.append(toImpl(urlConf));
            }
            return new ApacheConfiguration2(this,configuration);
        } catch (IOException e) {
            throw new ConfigurationException(e);
        }
    }


    @Override
    public Configuration buildFromClasspathResource(String resourcePath) {
        return buildFromClasspathResource(resourcePath,getClass().getClassLoader());
    }


    @Override
    public Configuration buildFromURI(URI uri) {
        try {
            if (uri.getScheme() == null) {
                Path path = Paths.get(uri.getPath());
                return buildFromPath(path);
            }
            return buildFromURL(uri.toURL());
        } catch (final MalformedURLException e) {
            throw new ConfigurationException(e);
        }
    }


    @Override
    public Configuration buildFromURL(URL url) {
        Configuration configuration;
        if (url.getFile().endsWith(".properties")) {
            configuration = buildFromPropertiesFile(url);
        } else if (url.getFile().endsWith(".json")) {
            configuration = buildFromJSON(url);
        } else if (url.getFile().endsWith(".xml")) {
            configuration = buildFromXML(url);
        } else if (url.getFile().endsWith(".yaml")) {
            configuration = buildFromYAML(url);
        } else {
            throw new ConfigurationException("Cannot determine resource type of "+ url);
        }
        return configuration;
    }


    private List<Configuration> buildFromURLEnum(Enumeration<URL> urls, String resourcePath) {
        final List<Configuration> configurations = new ArrayList<>();
        if (!urls.hasMoreElements()) {
            throw new ConfigurationException("Cannot find resource "+resourcePath);
        } else {
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                configurations.add(buildFromURL(url));
            }
        }
        return configurations;
    }




    private Configuration buildFromJSON(URL url) {
        try (InputStream stream = url.openStream()) {
            JSONConfiguration json = configure(new JSONConfiguration());
            json.read(stream);
            return new ApacheConfiguration2(this,json);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromYAML(URL url) {
        try (InputStream stream = url.openStream()) {
            YAMLConfiguration yaml = configure(new YAMLConfiguration());
            yaml.read(stream);
            return new ApacheConfiguration2(this,yaml);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromPropertiesFile(URL url) {
        try (InputStream stream = url.openStream(); Reader reader = new InputStreamReader(stream)) {
            PropertiesConfiguration properties = configure(new PropertiesConfiguration());
            properties.read(reader);
            return new ApacheConfiguration2(this,properties);
        } catch (IOException | org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }


    private Configuration buildFromXML(URL url) {
        try {
            XMLConfiguration xml = configure(new Configurations().xml(url));
            return new ApacheConfiguration2(this,xml);
        } catch (org.apache.commons.configuration2.ex.ConfigurationException e) {
            throw new ConfigurationException(e);
        }
    }



    private AbstractConfiguration toImpl(Configuration configuration) {
        if (configuration instanceof ApacheConfiguration2) {
            org.apache.commons.configuration2.Configuration impl =
                ((ApacheConfiguration2) configuration).conf;
            if (impl instanceof AbstractConfiguration) {
                return (AbstractConfiguration) impl;
            }
        }
        return new MapConfiguration(configuration.asMap());
    }


    private <T extends AbstractConfiguration> T configure (T configuration) {
        configuration.setConversionHandler(conversionHandler );
        return configuration;
    }


}

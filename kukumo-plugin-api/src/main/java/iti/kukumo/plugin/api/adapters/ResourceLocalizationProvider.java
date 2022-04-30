package iti.kukumo.plugin.api.adapters;

import java.io.InputStream;
import java.util.*;


public interface ResourceLocalizationProvider extends LocalizationProvider {

   InputStream openResource(Locale locale);

}

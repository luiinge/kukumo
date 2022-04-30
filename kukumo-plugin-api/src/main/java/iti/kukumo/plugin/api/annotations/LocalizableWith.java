package iti.kukumo.plugin.api.annotations;

import iti.kukumo.plugin.api.adapters.*;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocalizableWith {

    Class<? extends LocalizationProvider> value();

}

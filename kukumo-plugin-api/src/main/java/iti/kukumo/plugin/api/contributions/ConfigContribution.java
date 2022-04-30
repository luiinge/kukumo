package iti.kukumo.plugin.api.contributions;

import imconfig.Config;
import iti.kukumo.plugin.api.Contribution;
import org.jexten.ExtensionPoint;

@ExtensionPoint(version = "2.0")
public interface ConfigContribution extends Contribution {

    Config config();

}

// =================== DO NOT EDIT THIS FILE ====================
// Generated by Modello 1.8.3,
// any modifications will be overwritten.
// ==============================================================

package org.apache.maven.model;

/**
 * Contains the plugins management informations for the project.
 * 
 * @version $Revision$ $Date$
 */
@SuppressWarnings( "all" )
public class PluginConfiguration
    extends PluginContainer
    implements java.io.Serializable, java.lang.Cloneable
{

      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Default plugin information to be made available for
     * reference by projects
     *             derived from this one. This plugin configuration
     * will not be resolved or bound to the
     *             lifecycle unless referenced. Any local
     * configuration for a given plugin will override
     *             the plugin's entire definition here.
     */
    private PluginManagement pluginManagement;


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method clone.
     * 
     * @return PluginConfiguration
     */
    public PluginConfiguration clone()
    {
        try
        {
            PluginConfiguration copy = (PluginConfiguration) super.clone();

            if ( this.pluginManagement != null )
            {
                copy.pluginManagement = (PluginManagement) this.pluginManagement.clone();
            }

            return copy;
        }
        catch ( java.lang.Exception ex )
        {
            throw (java.lang.RuntimeException) new java.lang.UnsupportedOperationException( getClass().getName()
                + " does not support clone()" ).initCause( ex );
        }
    } //-- PluginConfiguration clone()

    /**
     * Get default plugin information to be made available for
     * reference by projects
     *             derived from this one. This plugin configuration
     * will not be resolved or bound to the
     *             lifecycle unless referenced. Any local
     * configuration for a given plugin will override
     *             the plugin's entire definition here.
     * 
     * @return PluginManagement
     */
    public PluginManagement getPluginManagement()
    {
        return this.pluginManagement;
    } //-- PluginManagement getPluginManagement()

    /**
     * Set default plugin information to be made available for
     * reference by projects
     *             derived from this one. This plugin configuration
     * will not be resolved or bound to the
     *             lifecycle unless referenced. Any local
     * configuration for a given plugin will override
     *             the plugin's entire definition here.
     * 
     * @param pluginManagement
     */
    public void setPluginManagement( PluginManagement pluginManagement )
    {
        this.pluginManagement = pluginManagement;
    } //-- void setPluginManagement( PluginManagement )

}

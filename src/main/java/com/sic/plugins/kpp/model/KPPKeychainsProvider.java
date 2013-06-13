
package com.sic.plugins.kpp.model;

import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.XmlFile;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 *
 * An extension point for providing {@link KPPKeychain}
 */

public abstract class KPPKeychainsProvider implements ExtensionPoint {
    
    private final static Logger LOGGER = Logger.getLogger(KPPKeychainsProvider.class.getName());
    private final static String DEFAULT_KEYCHAINS_CONFIG_XML = "com.sic.kpp.KPPKeychainProvider.xml";
    
    private List<KPPKeychain> keychains = new ArrayList<KPPKeychain>();
    
    public String test;
    
    /**
     * Constructor
     */
    public KPPKeychainsProvider() {
        test = "Na du";
    }
    
    /**
     * load keychains.
     */
    public void load() {
        try {
            XmlFile xml = getKeychainsConfigFile();
            if (xml.exists()) {
                xml.unmarshal(this);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read the existing keychains and provisioning profiles from xml", e);
        }
    }

    /**
     * Returns all the registered {@link KPPKeychain} descriptors.
     *
     * @return all the registered {@link KPPKeychain} descriptors.
     */
    public static DescriptorExtensionList<KPPKeychain, Descriptor<KPPKeychain>> allKeychainDescriptors() {
        return Hudson.getInstance().getDescriptorList(KPPKeychain.class);
    }

    /**
     * Get a list with all keychains.
     * 
     * @return all keychains.
     */
    public List<KPPKeychain> getKeychains() {
        load();
        return keychains;
    }

    /**
     * Get the keychains config file.
     * 
     * @return file.
     */
    public XmlFile getKeychainsConfigFile() {
        return new XmlFile(new File(Hudson.getInstance().getRootDir(), getKeychainsConfigXMLName()));
    }

    /**
     * All regsitered instances.
     */
    public static ExtensionList<KPPKeychainsProvider> all() {
        return Jenkins.getInstance().getExtensionList(KPPKeychainsProvider.class);
    }

    /**
     * Get the keychains xml config filename.
     * 
     * @return filename.
     */
    public String getKeychainsConfigXMLName() {
        return DEFAULT_KEYCHAINS_CONFIG_XML;
    }
}

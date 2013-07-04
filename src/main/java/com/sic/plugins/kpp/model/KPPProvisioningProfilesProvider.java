/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sic.plugins.kpp.model;

import hudson.ExtensionPoint;
import hudson.XmlFile;
import hudson.model.Hudson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;

/**
 * An extension point for providing {@link KPPProvisioningProfile}
 * @author michaelbar
 */
public class KPPProvisioningProfilesProvider implements ExtensionPoint {
    
    private final static Logger LOGGER = Logger.getLogger(KPPKeychainsProvider.class.getName());
    
    private final static String DEFAULT_PROVISIONING_PROFILES_CONFIG_XML = String.format("%s%s.xml", KPPProvisioningProfile.class.getPackage().getName(), KPPProvisioningProfile.class.getName());
    private final static String DEFAULT_PROVISIONING_PROFILES_UPLOAD_DIRECTORY_PATH = Hudson.getInstance().getRootDir() + File.separator + "kpp_upload";
    
    private List<KPPProvisioningProfile> provisioningProfiles = new ArrayList<KPPProvisioningProfile>();
    
    /**
     * Constructor
     */
    public KPPProvisioningProfilesProvider() {
        load();
        try {
            save();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not save provisioning profiles provider config file.", e);
        }
    }
    
    private void load() {
        // 1. load provisioning profile(s) information from config xml.
        try {
            XmlFile xml = getProvisioningProfilesConfigFile();
            if (xml.exists()) {
                xml.unmarshal(this);
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "No provisioning profiles provider config file found.", e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read the existing provisioning profiles provider config xml file.", e);
        }
        
        List<KPPProvisioningProfile> profilesFromXml = provisioningProfiles;
    }
    
    private List<KPPProvisioningProfile> loadProvisioningProfilesFromUploadFolder() {
        // TODO: hier gehts weiter.
        return null;
    }
    
    /**
     * Get a list with all provisioning profiles.
     * @return list
     */
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        return provisioningProfiles;
    }
    
    /**
     * Get the provisioning profiles config xml file.
     * @return file
     */
    public XmlFile getProvisioningProfilesConfigFile() {
        return new XmlFile(new File(Hudson.getInstance().getRootDir(), getProvisioningProfilesConfigXMLName()));
    }
    
    /**
     * Get the provisioning profiles config xml filename.
     * @return filename
     */
    public String getProvisioningProfilesConfigXMLName() {
        return DEFAULT_PROVISIONING_PROFILES_CONFIG_XML;
    }
    
    /**
     * Save provisioning profiles provider config xml.
     * @throws IOException 
     */
    public final void save() throws IOException {
        getProvisioningProfilesConfigFile().write(this);
    }
    
    /**
     * Updates provisioning profiles information from xml configuration and provisioning profiles upload folder.
     */
    public void update() {
        getProvisioningProfiles().clear();
        load();
    }
    
}
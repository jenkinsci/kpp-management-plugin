package com.sic.plugins.kpp.model;

import hudson.model.Describable;
import hudson.model.Descriptor;
import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a provisioning profile.
 */
public class KPPProvisioningProfile implements Describable<KPPProvisioningProfile>, Serializable {
    
    private String uuid;
    private final String fileName;
    
    @DataBoundConstructor
    public KPPProvisioningProfile(String fileName) {
        this.fileName = fileName;
    }
    
    /**
     * Get the filename of the provisioning profile.
     * @return filename
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Get the uuid of the provisioning profile.
     * @return uuid
     */
    public String getUUID() {
        return uuid;
    }

    public Descriptor<KPPProvisioningProfile> getDescriptor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

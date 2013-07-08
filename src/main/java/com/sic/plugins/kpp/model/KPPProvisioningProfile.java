package com.sic.plugins.kpp.model;

import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a provisioning profile.
 */
public class KPPProvisioningProfile implements Serializable {
    
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
    
    
}

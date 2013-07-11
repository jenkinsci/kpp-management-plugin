package com.sic.plugins.kpp.model;

import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents a provisioning profile.
 */
public class KPPProvisioningProfile implements Describable<KPPProvisioningProfile>, Serializable {
    
    private final String fileName;
    private transient String uuid;
    
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
    public String getUuid() {
        if (uuid==null || uuid.isEmpty()) {
            uuid = KPPProvisioningProfilesProvider.parseUUIDFromProvisioningProfileFile(fileName);
        }
        return uuid;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KPPProvisioningProfile other = (KPPProvisioningProfile) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
            return false;
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        return hash;
    }
    
    public Descriptor<KPPProvisioningProfile> getDescriptor() {
        Descriptor ds = Hudson.getInstance().getDescriptorOrDie(getClass());
        return ds;
    }
    
    @Extension
    public static final class DescriptorImpl extends Descriptor<KPPProvisioningProfile> {

        @Override
        public String getDisplayName() {
            return "Provisioning Profile";
        }
    }
    
    
}

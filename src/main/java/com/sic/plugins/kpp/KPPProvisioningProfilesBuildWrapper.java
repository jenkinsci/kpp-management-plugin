package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import java.util.List;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Build wrapper for provisioning profiles
 * @author mb
 */
public class KPPProvisioningProfilesBuildWrapper extends BuildWrapper {
    
    private List<KPPProvisioningProfile> provisioningProfiles;
    
    /**
     * Constructor
     */
    @DataBoundConstructor
    public KPPProvisioningProfilesBuildWrapper(List<KPPProvisioningProfile> provisioningProfiles) {
        super();
        this.provisioningProfiles = provisioningProfiles;
    }
    
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        return provisioningProfiles;
    }
    
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }
    
    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {
        
        @Override
        public boolean isApplicable(AbstractProject<?, ?> ap) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.KPPProvisioningProfilesBuildWrapper_DisplayName();
        }
    }
    
}
 
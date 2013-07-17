package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Build wrapper for provisioning profiles
 * @author mb
 */
public class KPPProvisioningProfilesBuildWrapper extends BuildWrapper {
    
    private List<KPPProvisioningProfile> provisioningProfiles;
    private boolean deleteProfilesAfterBuild;
    private boolean overwriteExistingProfiles;
    /**
     * Constructor
     */
    @DataBoundConstructor
    public KPPProvisioningProfilesBuildWrapper(List<KPPProvisioningProfile> provisioningProfiles, boolean deleteProfilesAfterBuild, boolean overwriteExistingProfiles) {
        super();
        this.provisioningProfiles = provisioningProfiles;
        this.deleteProfilesAfterBuild = deleteProfilesAfterBuild;
        this.overwriteExistingProfiles = overwriteExistingProfiles;
    }
    
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        return provisioningProfiles;
    }
    
    public boolean getDeleteProfilesAfterBuild() {
        return deleteProfilesAfterBuild;
    }
    
    public boolean getOverwriteExistingProfiles() {
        return overwriteExistingProfiles;
    }
    
    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        copyProvisioningProfiles(build);
        return new KPPProvisioningProfilesBuildWrapper.EnvironmentImpl(provisioningProfiles);
    }
    
    private void copyProvisioningProfiles(AbstractBuild build) {
        // TODO: implement
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
    
    private class EnvironmentImpl extends Environment {
        private final List<KPPProvisioningProfile> provisioningProfiles;
        
        public EnvironmentImpl(List<KPPProvisioningProfile> provisioningProfiles) {
            super();
            this.provisioningProfiles = provisioningProfiles;
        }
        
        private Map<String, String> getEnvMap() {
            Map<String, String> map = new HashMap<String,String>();
            for(KPPProvisioningProfile profile : provisioningProfiles) {
                String uuid = profile.getUuid();
                if (uuid != null && uuid.length()!=0) {
                    map.put(profile.getProvisioningProfileVariableName(), uuid);
                }
            }
            return map;
        }
        
        @Override
        public void buildEnvVars(Map<String, String> env) {
            env.putAll(getEnvMap());
	}
        
    }
    
}
 
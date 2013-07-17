package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private transient List<FilePath> copiedProfiles;
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
    
    private void copyProvisioningProfiles(AbstractBuild build) throws IOException, InterruptedException {
        
        Hudson hudson = Hudson.getInstance();
        FilePath hudsonRoot = hudson.getRootPath();
        VirtualChannel channel = null;
        String toProvisioningProfilesDirectoryPath = null;
        
        String buildOn = build.getBuiltOnStr();
        if (buildOn==null || buildOn.isEmpty()) {
            // build on master
            FilePath projectWorkspace = build.getWorkspace();
            channel = projectWorkspace.getChannel();
            toProvisioningProfilesDirectoryPath = KPPProvisioningProfilesProvider.getInstance().getProvisioningProfilesPath();
        } else {
            // build on slave
            // TODO implement
            Node node = build.getBuiltOn();
            System.out.println("build on slave");
        }
        
        if (toProvisioningProfilesDirectoryPath==null || toProvisioningProfilesDirectoryPath.isEmpty()) {
            // nothing to copy to provisioning profiles path
            return;
        }
        
        if (copiedProfiles == null) {
            copiedProfiles = new ArrayList<FilePath>();
        } else {
            copiedProfiles.clear();
        }
        
        for (KPPProvisioningProfile pp : provisioningProfiles) {
            FilePath from = new FilePath(hudsonRoot, pp.getProvisioningProfileFilePath());
            String toPPPath = String.format("%s%s%s", toProvisioningProfilesDirectoryPath, File.separator, KPPProvisioningProfilesProvider.getUUIDFileName(pp.getUuid()));
            FilePath to = new FilePath(channel, toPPPath);
            if (overwriteExistingProfiles || !to.exists()) {
                from.copyTo(to);
                copiedProfiles.add(to);
            }
        }
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
        
        @Override
        public boolean tearDown(AbstractBuild build, BuildListener listener)
                throws IOException, InterruptedException {
            if (deleteProfilesAfterBuild) {
                for (FilePath filePath : copiedProfiles) {
                    filePath.delete();
                }
            }
            return true;
        }
        
    }
    
}
 
/*
 * The MIT License
 *
 * Copyright 2013 Michael Bär SIC! Software GmbH.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Build wrapper for provisioning profiles
 * @author Michael Bär
 */
public class KPPProvisioningProfilesBuildWrapper extends SimpleBuildWrapper implements Serializable {
    
    private List<KPPProvisioningProfile> provisioningProfiles;
    private boolean deleteProfilesAfterBuild;
    private boolean overwriteExistingProfiles;
    private transient List<FilePath> copiedProfiles;
    
    /**
     * Constructor
     * @param provisioningProfiles list of provisioning profiles
     * @param deleteProfilesAfterBuild if the provisioning profile can be deleted after the build
     * @param overwriteExistingProfiles if the provisioning profile can be overwritten
     */
    @DataBoundConstructor
    public KPPProvisioningProfilesBuildWrapper(List<KPPProvisioningProfile> provisioningProfiles, boolean deleteProfilesAfterBuild, boolean overwriteExistingProfiles) {
        super();
        this.provisioningProfiles = provisioningProfiles;
        this.deleteProfilesAfterBuild = deleteProfilesAfterBuild;
        this.overwriteExistingProfiles = overwriteExistingProfiles;
    }
    
    /**
     * Get the provisioning profiles.
     * @return list of provisioning profiles
     */
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        return provisioningProfiles;
    }
    
    /**
     * Get if the provisioning profile can be deleted after the build.
     * @return true can be deleted, otherwise false
     */
    public boolean getDeleteProfilesAfterBuild() {
        return deleteProfilesAfterBuild;
    }
    
    /**
     * Get if current existing provisioning profile with the same filename can be overwritten.
     * @return true can be overwritten, otherwise false
     */
    public boolean getOverwriteExistingProfiles() {
        return overwriteExistingProfiles;
    }

    @Override
    public void setUp(Context context, Run<?, ?> run, FilePath filePath, Launcher launcher, TaskListener taskListener, EnvVars envVars) throws IOException, InterruptedException {
        copyProvisioningProfiles(filePath, envVars.get("NODE_NAME"));
        EnvironmentImpl env = new KPPProvisioningProfilesBuildWrapper.EnvironmentImpl(provisioningProfiles);
        env.buildEnvVars(context.getEnv());
        context.setDisposer(new KPPProvisioningProfilesDisposer());
    }

    /**
     * Copy the provisioning profiles configured for this job to the mobile provisioning profile path of the node or master, where the job is executed.
     * @param projectWorkspace current workspace
     * @param nodeStr Name of the execution node
     * @throws IOException
     * @throws InterruptedException 
     */
    private void copyProvisioningProfiles(FilePath projectWorkspace, String nodeStr) throws IOException, InterruptedException {
        
        Hudson hudson = Hudson.getInstance();
        FilePath hudsonRoot = hudson.getRootPath();
        VirtualChannel channel;
        String toProvisioningProfilesDirectoryPath = null;

        String buildOn = nodeStr;
        boolean isMaster = false;
        if (buildOn.equals("master")) {
            // build on master
            channel = projectWorkspace.getChannel();
            toProvisioningProfilesDirectoryPath = KPPProvisioningProfilesProvider.getInstance().getProvisioningProfilesPath();
            isMaster = true;
        } else {
            // build on slave
            Node node = hudson.getNode(nodeStr);;
            channel = node.getChannel();
            KPPNodeProperty nodeProperty = KPPNodeProperty.getCurrentNodeProperties(node);
            if (nodeProperty != null) {
                toProvisioningProfilesDirectoryPath = KPPNodeProperty.getCurrentNodeProperties(node).getProvisioningProfilesPath();
            }
        }
        
        if (toProvisioningProfilesDirectoryPath==null || toProvisioningProfilesDirectoryPath.isEmpty()) {
            // nothing to copy to provisioning profiles path
            String message;
            if (isMaster) {
                message = Messages.KPPProvisioningProfilesBuildWrapper_NoProvisioningProfilesPathForMaster();
            } else {
                message = Messages.KPPProvisioningProfilesBuildWrapper_NoProvisioningProfilesPathForSlave();
            }
            throw new IOException(message);
        }
        
        // remove file seperator char at the end of the path
        if (toProvisioningProfilesDirectoryPath.endsWith(File.separator)) {
            toProvisioningProfilesDirectoryPath = toProvisioningProfilesDirectoryPath.substring(0, toProvisioningProfilesDirectoryPath.length()-1);
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
    
    /**
     * Descriptor of the {@link KPPProvisioningProfilesBuildWrapper}
     */
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

    /**
     * Disposer class for cleaning up copied keychains
     */
    public class KPPProvisioningProfilesDisposer extends Disposer
    {
        @Override
        public void tearDown(Run<?, ?> run, FilePath filePath, Launcher launcher, TaskListener taskListener) throws IOException, InterruptedException {
            if (deleteProfilesAfterBuild) {
                for (FilePath profilePath : copiedProfiles) {
                    profilePath.delete();
                }
            }
        }
    }

    /**
     * Environment implementation that adds additional variables to the build.
     */
    private class EnvironmentImpl extends Environment {
        private final List<KPPProvisioningProfile> provisioningProfiles;
        
        /**
         * Constructor
         * @param provisioningProfiles list of provisioning profiles configured for this job
         */
        public EnvironmentImpl(List<KPPProvisioningProfile> provisioningProfiles) {
            super();
            this.provisioningProfiles = provisioningProfiles;
        }
        
        /**
         * Adds additional variables to the build environment.
         * @return environment with additional variables
         */
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
 
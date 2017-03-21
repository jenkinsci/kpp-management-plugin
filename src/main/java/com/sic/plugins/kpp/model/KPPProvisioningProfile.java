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

package com.sic.plugins.kpp.model;

import com.sic.plugins.kpp.Messages;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Represents a provisioning profile.
 * @author Michael Bär
 */
public class KPPProvisioningProfile implements Describable<KPPProvisioningProfile>, Serializable {
    
    private final static String PROVISIONING_PROFILE_BASE_VARIABLE_NAME = "PROVISIONING_PROFILE";
    
    private final String fileName;
    private final String varPrefix; // variable prefix for build step integration
    private transient String uuid;
    private static final long serialVersionUID = 1;
    
    /**
     * Constructor
     * @param fileName filename of the provisioning profile
     * @param varPrefix variable prefix name
     */
    @DataBoundConstructor
    public KPPProvisioningProfile(String fileName, String varPrefix) {
        this.fileName = fileName;
        this.varPrefix = varPrefix;
    }
    
    /**
     * Get the filename of the provisioning profile.
     * @return filename
     */
    public String getFileName() {
        return fileName;
    }
    
    /**
     * Get the filepath to the provisioning profile stored on the master or standalone jenkins instance.
     * @return filepath
     */
    public String getProvisioningProfileFilePath() {
        String file = KPPProvisioningProfilesProvider.removeUUIDFromFileName(fileName);
        return String.format("%s%s%s", KPPProvisioningProfilesProvider.getInstance().getUploadDirectoryPath(), File.separator, file);
    }
    
    /**
     * Get the variable prefix for build step integration.
     * @return variable prefix
     */
    public String getVarPrefix() {
        return varPrefix;
    }
    
    /**
     * Get the variable name for the provisioning profile.
     * @return variable name.
     */
    public String getProvisioningProfileVariableName() {
        String name;
        String prefix = getVarPrefix();
        if (prefix!=null && !prefix.isEmpty()) {
            name = String.format("%s_%s", prefix, PROVISIONING_PROFILE_BASE_VARIABLE_NAME);
        } else {
            name = PROVISIONING_PROFILE_BASE_VARIABLE_NAME;
        }
        return name;
    }
    
    /**
     * Get the variable name included in ${}.
     * @return variable name
     */
    public String getVariableName() {
        return String.format("${%s}", getProvisioningProfileVariableName());
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
    
    /**
     * Get the filename and uuid in one string.
     * @return filename and uuid
     */
    public String getFileNameUuidDescription() {
        return String.format("%s (%s)", getFileName(), getUuid());
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
    
    /**
     * Get the {@link DescriptorImpl}
     * @return descriptor
     */
    public Descriptor<KPPProvisioningProfile> getDescriptor() {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins != null) {
            return jenkins.getDescriptorOrDie(getClass());
        }

        return null;
    }
    
    /**
     * Descriptor for an {@link KPPProvisioningProfile}.
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<KPPProvisioningProfile> {

        @Override
        public String getDisplayName() {
            return Messages.KPPProvisioningProfile_DisplayName();
        }
        
        /**
         * Action method to fill out the filename items.
         * @param fileName the query parameter of the filename
         * @return list of filename items
         */
        public ListBoxModel doFillFileNameItems(@QueryParameter String fileName) {
            ListBoxModel m = new ListBoxModel();
            List<KPPProvisioningProfile> pps = KPPProvisioningProfilesProvider.getInstance().getProvisioningProfiles();
            for (KPPProvisioningProfile pp : pps) {
                m.add(pp.getFileNameUuidDescription());
            }
            return m;
        }
    }
    
    
}

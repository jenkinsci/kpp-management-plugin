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

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.Hudson;
import hudson.model.Node;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Configure Slave Node Properties
 * @author Michael Bär
 */
public class KPPNodeProperty extends NodeProperty<Node>{
    private String provisioningProfilesPath;
    
    /**
     * Construcrot
     * @param provisioningProfilesPath path to the directory where the provisioning profiles should be saved on the node.
     */
    @DataBoundConstructor
    public KPPNodeProperty(String provisioningProfilesPath) {
        this.provisioningProfilesPath = provisioningProfilesPath;
    }
    
    /**
     * Get path to the directory where the provisioning profiles should be saved.
     * @return path to the directory
     */
    public String getProvisioningProfilesPath() {
        return provisioningProfilesPath;
    }
    
    /**
     * Get the {@link KPPNodeProperty}.
     * @return node property
     */
    public static KPPNodeProperty getCurrentNodeProperties(Node node) {
        KPPNodeProperty property = node.getNodeProperties().get(KPPNodeProperty.class);
        if(property == null) {
            property = Hudson.getInstance().getGlobalNodeProperties().get(KPPNodeProperty.class);
        }
        return property;
    }
    
    /**
     * Descriptor of the {@link KPPNodeProperty}
     */
    @Extension
    public static final class DescriptorImpl extends NodePropertyDescriptor {
        
        /**
         * Constructor
         */
        public DescriptorImpl() {
            super(KPPNodeProperty.class);
        }
        
        @Override
        public String getDisplayName() {
            return Messages.KPPNodeProperty_DisplayName();
        }
        
    }
}

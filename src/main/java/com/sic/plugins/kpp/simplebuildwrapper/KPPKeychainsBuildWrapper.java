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

package com.sic.plugins.kpp.simplebuildwrapper;

import com.sic.plugins.kpp.Messages;
import com.sic.plugins.kpp.model.KPPKeychain;
import com.sic.plugins.kpp.model.KPPKeychainCertificatePair;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.BuildWrapperDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jenkins.tasks.SimpleBuildWrapper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/**
 * Build wrapper for keychains
 * @author mb
 */
public class KPPKeychainsBuildWrapper extends SimpleBuildWrapper {

    private List<KPPKeychainCertificatePair> keychainCertificatePairs = new ArrayList<KPPKeychainCertificatePair>();
    private boolean deleteKeychainsAfterBuild;
    private boolean overwriteExistingKeychains;
    private String keychain;
    private String codeSigningIdentity;
    private transient List<FilePath>copiedKeychains;
    private final static Logger LOG = Logger.getLogger(KPPKeychainsBuildWrapper.class.getName());
    private TaskListener taskListener;


    @DataBoundConstructor
    public KPPKeychainsBuildWrapper(String keychain, String codeSigningIdentity, boolean deleteKeychainsAfterBuild, boolean overwriteExistingKeychains) {
        this.codeSigningIdentity = codeSigningIdentity;
        this.keychain = keychain;
        this.keychainCertificatePairs.add(new KPPKeychainCertificatePair("iOS-Enterprise-2016-2019.keychain", "iPhone Distribution: sovanta AG", ""));
        this.deleteKeychainsAfterBuild = deleteKeychainsAfterBuild;
        this.overwriteExistingKeychains = overwriteExistingKeychains;

    }

    /**
     * Getter needed by Jenkins Snippet Generator. Don't remove.
     * @return keychain string
     */
    public String getKeychain()
    {
        return keychain;
    }

    /**
     * Getter needed by Jenkins Snippet Generator. Don't remove.
     * @return code signing identity string
     */
    public String getCodeSigningIdentity()
    {
        return codeSigningIdentity;
    }

    /**
     * Get if the keychain can be deleted after the build.
     * @return true can be deleted, otherwise false
     */
    public boolean getDeleteKeychainsAfterBuild() {
        return deleteKeychainsAfterBuild;
    }

    /**
     * Get if a current existing keychain with the same filename can be overwritten.
     * @return true can be overwritten, otherwise false
     */
    public boolean getOverwriteExistingKeychains() {
        return overwriteExistingKeychains;
    }
    
    /**
     * Get all keychain certificate pairs configured for this build job.
     * @return list of keychain certificate pairs
     */
    public List<KPPKeychainCertificatePair> getKeychainCertificatePairs() {
        return keychainCertificatePairs;
    }

    @Override
    public void setUp(Context context, Run<?, ?> run, FilePath filePath, Launcher launcher, TaskListener taskListener, EnvVars envVars) throws IOException, InterruptedException {
        this.taskListener = taskListener;
        taskListener.getLogger().println(keychainCertificatePairs.get(0).getKeychain());
        copyKeychainsToWorkspace(filePath);

        Environment env = new EnvironmentImpl(keychainCertificatePairs);
        env.buildEnvVars(context.getEnv());
        context.setDisposer(new KPPKeychainsDisposer());
    }

    /**
     * Disposer class for cleaning up copied keychains
     */
    public class KPPKeychainsDisposer extends Disposer
    {
        @Override
        public void tearDown(Run<?, ?> run, FilePath filePath, Launcher launcher, TaskListener taskListener) throws IOException, InterruptedException {
            if (deleteKeychainsAfterBuild) {
                for (FilePath keychainPath : copiedKeychains) {
                    keychainPath.delete();
                }
            }
        }
    }


    /**
     * Copy the keychains configured for this build job to the workspace of the job.
     * @param projectWorkspace the current build workspace path
     * @throws IOException
     * @throws InterruptedException 
     */
    private void copyKeychainsToWorkspace(FilePath projectWorkspace) throws IOException, InterruptedException {


        Hudson hudson = Hudson.getInstance();
        FilePath hudsonRoot = hudson.getRootPath();

        if (copiedKeychains == null) {
            copiedKeychains = new ArrayList<FilePath>();
        } else {
            copiedKeychains.clear();
        }

        for (KPPKeychainCertificatePair pair : keychainCertificatePairs) {
            FilePath from = new FilePath(hudsonRoot, pair.getKeychainFilePath());
            FilePath to = new FilePath(projectWorkspace, pair.getKeychainFileName());
            if (overwriteExistingKeychains || !to.exists()) {
                from.copyTo(to);
                copiedKeychains.add(to);
            }
        }
    }

    @Extension
    public static final class DescriptorImpl extends BuildWrapperDescriptor {
        public DescriptorImpl()
        {
            super(KPPKeychainsBuildWrapper.class);
        }


        @Override
        public boolean isApplicable(AbstractProject<?, ?> ap) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return Messages.KPPKeychainsBuildWrapper_DisplayName();
        }

    }
    
    /**
     * Environment implementation that adds additional variables to the build.
     * TODO: Does not need extend Environment anymore.
     */
    private class EnvironmentImpl extends Environment {
        
        private final List<KPPKeychainCertificatePair> keychainCertificatePairs;
        
        /**
         * Constructor
         * @param keychainCertificatePairs list of keychain certificate pairs configured for this build job
         */
        public EnvironmentImpl(List<KPPKeychainCertificatePair> keychainCertificatePairs) {
            this.keychainCertificatePairs = keychainCertificatePairs;
        }
        
        /**
         * Adds additional variables to the build environment.
         * @param env current environment
         * @return environment with additional variables
         */
        private Map<String, String> getEnvMap(Map<String, String> env) {
            Map<String, String> map = new HashMap<String,String>();
            for (KPPKeychainCertificatePair pair : keychainCertificatePairs) {
                KPPKeychain keychain = KPPKeychainCertificatePair.getKeychainFromString(pair.getKeychain());
                if (keychain!=null) {
                    String fileName = keychain.getFileName();
                    String password = keychain.getPassword();
                    String codeSigningIdentity = pair.getCodeSigningIdentity();
                    if (fileName!=null && fileName.length()!=0) {
                        String keychainPath = String.format("%s%s%s", env.get("WORKSPACE"), File.separator, fileName);
                        map.put(pair.getKeychainVariableName(), keychainPath);
                    }
                    if (password!=null && password.length()!=0)
                        map.put(pair.getKeychainPasswordVariableName(), keychain.getPassword());
                    if (codeSigningIdentity!=null && codeSigningIdentity.length()!=0)
                        map.put(pair.getCodeSigningIdentityVariableName(), codeSigningIdentity);
                }
            }
            return map;
        }
        
        @Override
        public void buildEnvVars(Map<String, String> env) {
            env.putAll(getEnvMap(env));
	    }
    }
}

package com.sic.plugins.kpp;

import com.sic.plugins.kpp.model.KPPKeychain;
import com.sic.plugins.kpp.model.KPPKeychainCertificatePair;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Hudson;
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
 * Build wrapper for keychains
 * @author mb
 */
public class KPPKeychainsBuildWrapper extends BuildWrapper {

    private List<KPPKeychainCertificatePair> keychainCertificatePairs = new ArrayList<KPPKeychainCertificatePair>();
    private boolean deleteKeychainsAfterBuild;
    private boolean overwriteExistingKeychains;
    private transient List<FilePath>copiedKeychains;
    
    /**
     * Constructor
     * @param keychainCertificatePairs
     * @param deleteKeychainsAfterBuild
     * @param overwriteExistingKeychains 
     */
    @DataBoundConstructor
    public KPPKeychainsBuildWrapper(List<KPPKeychainCertificatePair> keychainCertificatePairs, boolean deleteKeychainsAfterBuild, boolean overwriteExistingKeychains) {
        this.keychainCertificatePairs = keychainCertificatePairs;
        this.deleteKeychainsAfterBuild = deleteKeychainsAfterBuild;
        this.overwriteExistingKeychains = overwriteExistingKeychains;
    }
    
    public boolean getDeleteKeychainsAfterBuild() {
        return deleteKeychainsAfterBuild;
    }
    
    public boolean getOverwriteExistingKeychains() {
        return overwriteExistingKeychains;
    }
    
    public List<KPPKeychainCertificatePair> getKeychainCertificatePairs() {
        return keychainCertificatePairs;
    }
    
    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) throws IOException, InterruptedException {
        copyKeychainsToWorkspace(build);
        return new EnvironmentImpl(keychainCertificatePairs);
    }
    
    private void copyKeychainsToWorkspace(AbstractBuild build) throws IOException, InterruptedException {
        FilePath projectWorkspace = build.getWorkspace();

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
            return Messages.KPPKeychainsBuildWrapper_DisplayName();
        }
    }
    
    private class EnvironmentImpl extends Environment {
        
        private final List<KPPKeychainCertificatePair> keychainCertificatePairs;
        
        public EnvironmentImpl(List<KPPKeychainCertificatePair> keychainCertificatePairs) {
            this.keychainCertificatePairs = keychainCertificatePairs;
        }
        
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
        
        @Override
        public boolean tearDown(AbstractBuild build, BuildListener listener)
                throws IOException, InterruptedException {
            if (deleteKeychainsAfterBuild) {
                for (FilePath filePath : copiedKeychains) {
                    filePath.delete();
                }
            }
            return true;
        }
        
    }
}

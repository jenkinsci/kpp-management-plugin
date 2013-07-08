
package com.sic.plugins.kpp.provider;

import com.sic.plugins.kpp.model.KPPKeychain;
import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import org.apache.commons.lang.StringUtils;

/**
 *
 * An extension point for providing {@link KPPKeychain}
 */
public abstract class KPPBaseKeychainsProvider extends KPPBaseProvider implements ExtensionPoint {
    
    private List<KPPKeychain> keychains = new ArrayList<KPPKeychain>();
    
    /**
     * {@inherited}
     */
    protected void merge() {
        List<KPPKeychain> keychainsFromXml = keychains;
        
        // 2. load keychains from upload folder.
        List<KPPKeychain> keychainsFromFolder = loadKeychainsFromUploadFolder();
        
        //3. merge keychains
        keychains = mergedKeychains(keychainsFromXml, keychainsFromFolder);
    }
    
    private List<KPPKeychain> loadKeychainsFromUploadFolder() {
        checkAndCreateUploadFolder();
        List<KPPKeychain> k = new ArrayList<KPPKeychain>();
        
        File[] keychainFiles = new File(getUploadDirectoryPath()).listFiles(new KPPBaseKeychainsProvider.KeychainFileNameFilter());
        for(File keychainFile : keychainFiles) {
            KPPKeychain keychain = new KPPKeychain(keychainFile.getName());
            if(StringUtils.isBlank(keychain.getFileName())) {
            break;
            }
            k.add(keychain);
        }
        return k;
    }
    
    private List<KPPKeychain> mergedKeychains(List<KPPKeychain>keychainsFromXML, List<KPPKeychain>keychainsFromFolder) {
        List<KPPKeychain> ks = new ArrayList<KPPKeychain>();
        
        List<KPPKeychain> ksFolder = new ArrayList<KPPKeychain>(keychainsFromFolder);
        for (KPPKeychain kXML : keychainsFromXML) {
            for (KPPKeychain kFolder : ksFolder) {
                if (kXML.equals(kFolder)) {
                    ks.add(kXML);
                    ksFolder.remove(kFolder);
                    break;
                }
            }
        }
        
        if(!ksFolder.isEmpty()) {
            ks.addAll(ksFolder);
        }
        
        return ks;
    }
    
    /**
     * Get a list with all keychains.
     * 
     * @return all keychains.
     */
    public List<KPPKeychain> getKeychains() {
        return keychains;
    }

    /**
     * Returns all the registered {@link KPPKeychain} descriptors.
     *
     * @return all the registered {@link KPPKeychain} descriptors.
     */
    public static DescriptorExtensionList<KPPKeychain, Descriptor<KPPKeychain>> allKeychainDescriptors() {
        return Hudson.getInstance().getDescriptorList(KPPKeychain.class);
    }

    /**
     * All regsitered instances.
     */
    public static ExtensionList<KPPBaseKeychainsProvider> all() {
        return Jenkins.getInstance().getExtensionList(KPPBaseKeychainsProvider.class);
    }
    
    /**
     * Call this method to update keychains after save action.
     * This method updates keychain information and removes keychains from upload folder if they are deleted.
     * @param keychainsFromSave 
     */
    public void updateKeychainsFromSave(List<KPPKeychain>keychainsFromSave) {
        List<KPPKeychain> ksCurrent = new ArrayList<KPPKeychain>(getKeychains());
        List<KPPKeychain> ksNew = new ArrayList<KPPKeychain>(keychainsFromSave.size());
        
        for (KPPKeychain kS : keychainsFromSave) {
            for (KPPKeychain kC : ksCurrent) {
                if (kC.equals(kS)) {
                    ksNew.add(kS);
                    ksCurrent.remove(kC);
                    break;
                }
            }
        }
        
        if (!ksCurrent.isEmpty()) {
            // delete keychains from filesystem
            final String ksFolderPath = getUploadDirectoryPath();
            File kFile;
            for (KPPKeychain k : ksCurrent) {
                kFile = new File(ksFolderPath + File.separator +k.getFileName());
                kFile.delete();
            }
        }
        
        keychains = ksNew;
    }
    
    /**
     * Filename filter to get only files with the ".keychain" extension.
     */
    private class KeychainFileNameFilter implements FilenameFilter {

        public boolean accept(File file, String name) {
            boolean ret = false;
            if (file.isDirectory() && name.endsWith(".keychain")) { // keychains are directories
                ret = true;
            }
            return ret;
        }
    }
    
    /**
     * Updates keychains information.
     */
    @Override
    public void update() {
        getKeychains().clear();
        super.update();
    }
    
}

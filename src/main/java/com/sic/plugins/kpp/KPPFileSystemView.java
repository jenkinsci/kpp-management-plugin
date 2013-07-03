/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sic.plugins.kpp;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author michaelbar
 */
public class KPPFileSystemView extends FileSystemView {
    
    private String provisioningProfilesPath;
    
    
    public KPPFileSystemView() {
        super();
        this.provisioningProfilesPath = String.format("%s%s%s", this.getHomeDirectory().getAbsolutePath(), File.separator, "Library/MobileDevice/Provisioning Profiles");
    }
            
    @Override
    public File createNewFolder(File file) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getProvisioningProfilesPath() {
        return provisioningProfilesPath;
    }
    
}

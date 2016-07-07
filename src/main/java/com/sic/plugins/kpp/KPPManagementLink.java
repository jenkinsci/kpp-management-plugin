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

import com.sic.plugins.kpp.provider.KPPKeychainsProvider;
import com.sic.plugins.kpp.model.KPPKeychain;
import com.sic.plugins.kpp.model.KPPProvisioningProfile;
import com.sic.plugins.kpp.provider.KPPProvisioningProfilesProvider;
import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import static hudson.model.ManagementLink.all;
import hudson.model.Saveable;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Manages the main plugin configuration page for uploading of provisioning profiles and keychains.
 * 
 * @author Michael Bär
 */
@Extension
public class KPPManagementLink extends ManagementLink implements StaplerProxy, Saveable {
    
    private String errorMessage = null;
    
    /**
     * Gets the singletion instance.
     * @return the singletion instance.
     */
    public static KPPManagementLink getInstance() {
        return all().get(KPPManagementLink.class);
    }
    
    /**
     * Get error message
     * @return error message
     */
    public String getErrorMessage () {
        String message = errorMessage;
        errorMessage = null;
        return message;
    }

    /**
     * Get all keychains
     * 
     * @return all keychains.
     */
    @SuppressWarnings("unused") // used by stapler
    public List<KPPKeychain> getKeychains() {
        return KPPKeychainsProvider.getInstance().getKeychains();
    }
    
    /**
     * Get all provisioning profiles.
     * @return  all provisioning profiles
     */
    public List<KPPProvisioningProfile> getProvisioningProfiles() {
        List<KPPProvisioningProfile> list = KPPProvisioningProfilesProvider.getInstance().getProvisioningProfiles();
        return list;
    }
    
    /**
     * Get the path to provisioning profiles on master or standalone jenkins instance.
     * @return path
     */
    public String getProvisioningProfilesPath() {
        return KPPProvisioningProfilesProvider.getInstance().getProvisioningProfilesPath();
    }
    
    /**
     * Action method if upload button is clicked.
     * 
     * @param req Request
     * @param rsp Response
     * @throws ServletException
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    public void doUploadFile(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException,
            NoSuchAlgorithmException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        FileItem file = req.getFileItem("file");
        if (file == null || file.getSize() == 0) {
            this.errorMessage = Messages.KPPManagementLink_Upload_Error_NoFile();
        } else {
            KPPKeychainsProvider kProvider = KPPKeychainsProvider.getInstance();
            KPPProvisioningProfilesProvider ppProvider = KPPProvisioningProfilesProvider.getInstance();
            if (kProvider.isKeychainFile(file)) {
                kProvider.upload(file);
                kProvider.update();
            } else if (ppProvider.isMobileProvisionProfileFile(file)) {
                ppProvider.upload(file);
                ppProvider.update();
            } else {
                this.errorMessage = String.format(Messages.KPPManagementLink_Upload_Error_WrongFileType(), file.getName());
            }
        }
        
        rsp.sendRedirect2("../"+getUrlName()+"/"); //we stay on page
    }
    
    /**
     * Action method if save button is clicked.
     * 
     * @param req Request
     * @param rsp Response
     * @throws ServletException
     * @throws IOException
     * @throws NoSuchAlgorithmException 
     */
    public void doSave(StaplerRequest req, StaplerResponse rsp) throws
            ServletException,
            IOException,
            NoSuchAlgorithmException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        
        JSONObject data = req.getSubmittedForm();
        List<KPPKeychain> keychains = req.bindJSONToList(KPPKeychain.class, data.get("keychain"));
        KPPKeychainsProvider.getInstance().updateKeychainsFromSave(keychains);
        List<KPPProvisioningProfile> pps = req.bindJSONToList(KPPProvisioningProfile.class, data.get("profile"));
        KPPProvisioningProfilesProvider.getInstance().updateProvisioningProfilesFromSave(pps);
        KPPProvisioningProfilesProvider.getInstance().setProvisioningProfilesPath(data.getString("provisioningProfilesPath"));
        save();
        rsp.sendRedirect2("../manage"); //we go back on management page
    }
    
    @Override
    public String getIconFileName() {
        return "/plugin/kpp-management-plugin-pipeline/icon_kpp.png";
    }

    @Override
    public String getDisplayName() {
        return Messages.KPPManagementLink_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "kppmanagmentpipeline";
    }

    @Override
    public Object getTarget() {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        KPPKeychainsProvider.getInstance().update();
        KPPProvisioningProfilesProvider.getInstance().update();
        return this;
    }

    @Override
    public void save() throws IOException {
        Hudson.getInstance().checkPermission(Hudson.ADMINISTER);
        KPPKeychainsProvider.getInstance().save();
        KPPProvisioningProfilesProvider.getInstance().save();
    }
}

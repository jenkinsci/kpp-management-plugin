/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sic.software.kpp;

import hudson.model.Hudson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 *
 * @author michaelbar
 */
public class KPPManagementResult {

    public void doSaveConfig(StaplerRequest req, StaplerResponse rsp) throws ServletException, IOException {

        FileItem file = req.getFileItem("myFile.file");

        if (file == null) {
            throw new ServletException("no file upload");
        }

        byte[] data = file.get();

        File toUploadFile = new File(Hudson.getInstance().getRootDir(), "myFileHamed.xml");
        OutputStream os = new FileOutputStream(toUploadFile);
        try {
            os.write(data);
        } finally {
            os.close();
        }

        rsp.setContentType("text/html");
        rsp.getWriter().println("Uploaded File! Size: " + data.length + ".");
        // sleep 3 Seconds
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(KPPManagementResult.class.getName()).log(Level.SEVERE, null, ex);
        }
        rsp.sendRedirect2("../manageZpp/");
    }
}

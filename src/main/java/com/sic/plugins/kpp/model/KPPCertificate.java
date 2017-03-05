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

import java.io.Serializable;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Represents an certificate inside a keychain.
 * 
 * @author Michael Bär
 */
public class KPPCertificate implements Serializable{
    
    private String codeSigningIdentityName;
    
    /**
     * Constructor
     * @param codeSigningIdentityName name of the code signing identity
     */
    @DataBoundConstructor
    public KPPCertificate(String codeSigningIdentityName) {
        this.codeSigningIdentityName = codeSigningIdentityName;
    }
    
    /**
     * Get the code signing identity name.
     * @return codeSigningIdentityName 
     */
    public String getCodeSigningIdentityName() {
        return codeSigningIdentityName;
    }
    
    /**
     * Set the code singing identity name.
     * @param codeSigningIdentityName name of the code signing identity
     */
    public void setCodeSigningIdentityName(String codeSigningIdentityName) {
        this.codeSigningIdentityName = codeSigningIdentityName;
    }
    
}

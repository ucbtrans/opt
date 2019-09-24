/**
 * Copyright (c) 2019, Regents of the University of California
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *   Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 **/
package opt.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author Alex Kurzhanskiy
 */
public class ModifiedDoubleStringConverter extends DoubleStringConverter {
    public ModifiedDoubleStringConverter() {super();}
    
    public ModifiedDoubleStringConverter(String num_format) {
        super(); 
        myDF = new DecimalFormat(num_format);
    }
    
    public ModifiedDoubleStringConverter(double val) {
        super(); 
        dflt_val = val;
    }
    
    public ModifiedDoubleStringConverter(String num_format, double val) {
        super(); 
        myDF = new DecimalFormat(num_format);
        dflt_val = val;
    }
    
    private DecimalFormat myDF = new DecimalFormat("#.###");
    private double dflt_val = 1.0;

    @Override 
    public String toString(Double value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }
        return myDF.format(value);
    }
    

    @Override
    public Double fromString(String value) {
        try {
            // If the specified value is null or zero-length, return null
            if (value == null) {
                return null;
            }

            value = value.trim();

            if (value.length() < 1) {
                return null;
            }

            // Perform the requested parsing
            return myDF.parse(value).doubleValue();
        } catch (ParseException ex) {
            //throw new RuntimeException(ex);
            return new Double(dflt_val);
        }
    }
    
    
    public void setDefaultValue(double val) {
        dflt_val = val;
    }
    
}

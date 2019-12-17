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

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;


/**
 * Text formatting routines.
 * 
 * @author Alex Kurzhanskiy
 */
public class TextFormatting {
    
    public static TextFormatter<String> createTimeTextFormatter(String defaultValue) {
        UnaryOperator<TextFormatter.Change> filter = (TextFormatter.Change change) -> {
            if (!change.isContentChange()) {
                /* nothing is added or deleted but change must be returned
                * as it contains selection info and caret position
                */
                return change;
            }
            
            int maxlength = 5;
            if (change.getControlText().indexOf(':') == -1) {
                maxlength = 4;
            }
            
            if (change.getControlNewText().length() > maxlength || change.getText().matches("\\D+")) {
                // invalid input. Cancel the change
                return null;
            }
            return change;
        };
        
        
        StringConverter<String> converter = new StringConverter<String>() {
            // updates displayed text from commited value
            @Override
            public String toString(String committedText) {
                if (committedText == null) {
                    // don't change displayed text
                    return String.format("%s:%s", defaultValue.substring(0, 2), defaultValue.substring(2, 4));
                }

                if (committedText.length() == 4 && !committedText.matches("\\D+")) {
                    int hh = Integer.valueOf(committedText.substring(0, 2));
                    int mm = Integer.valueOf(committedText.substring(2, 4));
                    if (mm > 59) {
                        mm -= 60;
                        if (hh < 99)
                            hh++;
                    }
                    String h = "";
                    String m = "";
                    if (hh < 10)
                        h = "0";
                    if (mm < 10)
                        m = "0";
                    return String.format("%s%d:%s%d", h, hh, m, mm);
                } else {
                    /* Commited text can be either null or 5 digits.
                     * Nothing else is allowed by fromString() method unless changed directly
                     */
                    throw new IllegalStateException(
                            "Unexpected or incomplete time value: " + committedText);
                }
            }

            // commits displayed text to value
            @Override
            public String fromString(String displayedText) {
                // remove formatting characters
                Pattern p = Pattern.compile("[\\p{Punct}\\p{Blank}]", Pattern.UNICODE_CHARACTER_CLASS);
                Matcher m = p.matcher(displayedText);
                displayedText = m.replaceAll("");

                if (displayedText.length() != 4) {
                    // user is not done typing the number. Don't commit
                    return null;
                }
                return displayedText;
            }
        };
        
        return new TextFormatter<>(converter, "0000", filter);
    }
    
}

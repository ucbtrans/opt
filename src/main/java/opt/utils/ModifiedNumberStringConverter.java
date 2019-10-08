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
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import javafx.util.StringConverter;
//import javafx.util.converter.NumberStringConverter;

/**
 *
 * @author Alex Kurzhanskiy
 */
public class ModifiedNumberStringConverter extends StringConverter<Number> {

    // Private properties

    private Locale locale;
    private String pattern;
    private NumberFormat numberFormat;
    private int dflt_val = 0;

    // Constructors
    public ModifiedNumberStringConverter() {
        this(Locale.getDefault());
    }

    public ModifiedNumberStringConverter(Locale locale) {
        this(locale, null);
    }

    public ModifiedNumberStringConverter(String pattern) {
        this(Locale.getDefault(), pattern);
    }

    public ModifiedNumberStringConverter(Locale locale, String pattern) {
        this(locale, pattern, null);
    }

    public ModifiedNumberStringConverter(NumberFormat numberFormat) {
        this(null, null, numberFormat);
    }

    ModifiedNumberStringConverter(Locale locale, String pattern, NumberFormat numberFormat) {
        this.locale = locale;
        this.pattern = pattern;
        this.numberFormat = numberFormat;
    }

    // Converter Methods

    /** {@inheritDoc} */
    @Override 
    public Number fromString(String value) {
        try {
            // If the specified value is null or zero-length, return null
            if (value == null) {
                return null;
            }

            value = value.trim();

            if (value.length() < 1) {
                return null;
            }

            // Create and configure the parser to be used
            NumberFormat parser = getNumberFormat();

            // Perform the requested parsing
            return parser.parse(value);
        } catch (ParseException ex) {
            return dflt_val;
            //throw new RuntimeException(ex);
        }
    }

    /** {@inheritDoc} */
    @Override 
    public String toString(Number value) {
        // If the specified value is null, return a zero-length String
        if (value == null) {
            return "";
        }

        // Create and configure the formatter to be used
        NumberFormat formatter = getNumberFormat();

        // Perform the requested formatting
        return formatter.format(value);
    }

    /**
     * @return a <code>NumberFormat</code> instance to use for formatting
     * and parsing in this {@link StringConverter}.
     */
    protected NumberFormat getNumberFormat() {
        Locale _locale = locale == null ? Locale.getDefault() : locale;

        if (numberFormat != null) {
            return numberFormat;
        } else if (pattern != null) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(_locale);
            return new DecimalFormat(pattern, symbols);
        } else {
            return NumberFormat.getNumberInstance(_locale);
        }
    }
}

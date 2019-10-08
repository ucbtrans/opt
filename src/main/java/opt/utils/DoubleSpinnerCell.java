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

import javafx.beans.value.WritableValue;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableCell;

/**
 *
 * @author Alex Kurzhanskiy
 */
public class DoubleSpinnerCell<T> extends TableCell<T, Number> {
    private final Spinner<Double> spinner = new Spinner(0, Double.MAX_VALUE, 0, 0.01);
    private boolean ignoreUpdate; // flag preventing updates triggered from ui/initialisation

    {
        spinner.valueProperty().addListener((o, oldValue, newValue) -> {
            if (!ignoreUpdate) {
                ignoreUpdate = true;
                WritableValue<Number> property = (WritableValue<Number>) getTableColumn().getCellObservableValue((T) getTableRow().getItem());
                property.setValue(newValue);
                ignoreUpdate = false;
            }
        });
    }


    @Override
    protected void updateItem(Number item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            ignoreUpdate = true;
            spinner.getValueFactory().setValue(item.doubleValue());
            setGraphic(spinner);
            ignoreUpdate = false;
        }
    }
}

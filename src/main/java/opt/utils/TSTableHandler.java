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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;


/**
 * Event handling class for time series tables.
 * 
 * @author Alex Kurzhanskiy
 */
public class TSTableHandler {
    private DoubleStringConverter dsc = new DoubleStringConverter();
    private TableView<ObservableList<Object>> myTable = null;
    private int dt = 5;
    
    KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
    KeyCodeCombination pasteKeyCodeCompination = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY);
    
    private TablePosition<ObservableList<Object>, ?> focusedCell = null;
    
    
    
    public TableView<ObservableList<Object>> getTable() {return myTable;}
    public int getDt() {return dt;}
    
    public void setTable(TableView<ObservableList<Object>> tab) {
        if (tab != null)
            myTable = tab;
    }
    
    public void setDt(int dt) {
        if (dt < 1)
            return;
        this.dt = dt;
    }
    
    
    
    public boolean setOnKeyPressed(KeyEvent event) {
        boolean res = true;
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        if (copyKeyCodeCompination.match(event)) {
            return copyToClipboard();
        }
        
        if (pasteKeyCodeCompination.match(event)) {
            return pasteFromClipboard();
        }
        
        
        return res;
    }
    
    
    
    public boolean timeColumnUpdate() {
        int numRows = myTable.getItems().size();
        
        for (int i = 0; i < numRows; i++) {
            String ts = opt.utils.Misc.minutes2timeString(i*dt);
            myTable.getItems().get(i).set(0, ts);
        }
        
        myTable.refresh();
        
        return true;
    }
    
    
    
    /**
     * Copy table data to clipboard..
     * @return <true> if table content changed, <false> otherwise.
     */
    private boolean copyToClipboard() {
        StringBuilder clipboardString = new StringBuilder();
        ObservableList<TablePosition> positionList = myTable.getSelectionModel().getSelectedCells();
        int prevRow = -1;

	for (TablePosition position : positionList) {
            int row = position.getRow();
            int col = position.getColumn();
            Object cell = (Object) myTable.getColumns().get(col).getCellData(row);

            // null-check: provide empty string for nulls
            if (cell == null) {
		cell = "";
            }

            // determine whether we advance in a row (tab) or a column
            // (newline).
            if (prevRow == row) {		
		clipboardString.append('\t');		
            } else if (prevRow != -1) {			
		clipboardString.append('\n');
            }

            // create string from cell
            String text = cell.toString();

            // add new item to clipboard
            clipboardString.append(text);

            // remember previous
            prevRow = row;
	}

	// create clipboard content
	final ClipboardContent clipboardContent = new ClipboardContent();
	clipboardContent.putString(clipboardString.toString());

        // set clipboard content
        Clipboard.getSystemClipboard().setContent(clipboardContent);
        
        return false;
    }
    
    
    
    /**
     * Paste numeric data into the table.
     * @return <true> if table content changed, <false> otherwise.
     */
    private boolean pasteFromClipboard() {
        boolean res = true;
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        if (j0 < 1)
            return false;
        
        ObservableList<ObservableList<Object>> myItems = myTable.getItems();
        ObservableList<ObservableList<Object>> updatedItems = FXCollections.observableArrayList();
        
        for (int i = 0; i < i0; i++) {
            updatedItems.add(myItems.get(i));
        }
        
        Clipboard clipboard = Clipboard.getSystemClipboard();
        String tabTxt = (String)clipboard.getContent(DataFormat.PLAIN_TEXT);
        String textLines[] = tabTxt.split("\\r\\n|\\n|\\r");
        
        int numLines = textLines.length;
        int ii = i0;
        ObservableList<Object> lastRow = null;
        while ((ii < i0+numLines) && (ii < numRows)) {
            ObservableList<Object> row = FXCollections.observableArrayList();
            for (int j = 0; j < j0; j++) {
                row.add(myItems.get(ii).get(j));
            }
            String textCols[] = textLines[ii-i0].split("\\s+");
            int numSubs = textCols.length;
            int jj = j0;
            int offset = 0;
            if (textCols[0].indexOf(':') >= 0)
                offset++;
            
            while ((jj < numCols) && (jj-j0+offset < numSubs)) {
                try {
                    Double val = dsc.fromString(textCols[jj-j0+offset]);
                    row.add(val);
                } catch(Exception e) {
                    row.add(myItems.get(ii).get(jj));
                }
                jj++;
            }
            while (jj < numCols) {
                row.add(myItems.get(ii).get(jj));
                jj++;
            }
            updatedItems.add(row);
            lastRow = row;
            ii++;
        }
        
        while (ii < i0+numLines) {
             ObservableList<Object> row = FXCollections.observableArrayList();
             row.add(opt.utils.Misc.minutes2timeString(ii*dt));
             for (int j = 1; j < j0; j++) {
                row.add(myItems.get(ii).get(j));
            }
            String textCols[] = textLines[ii-i0].split(" ");
            int numSubs = textCols.length;
            int jj = j0;
            int offset = 0;
            if (textCols[0].indexOf(':') >= 0)
                offset++;
            
            while ((jj < numCols) && (jj-j0+offset < numSubs)) {
                try {
                    Double val = dsc.fromString(textCols[jj-j0+offset]);
                    row.add(val);
                } catch(Exception e) {
                    row.add(lastRow.get(jj));
                }
                jj++;
            }
            while (jj < numCols) {
                row.add(lastRow.get(jj));
                jj++;
            }
            updatedItems.add(row);
            lastRow = row;
            ii++;
        }
        
        for (int i = ii; i < numRows; i++) {
            updatedItems.add(myItems.get(i));
        }
        
        myTable.getItems().clear();
        myTable.getItems().addAll(updatedItems);
        myTable.refresh();
        numRows = myTable.getItems().size();
        for (int j = 1; j < numCols; j++) {
            TableColumn<ObservableList<Object>, Number> col = (TableColumn<ObservableList<Object>, Number>)myTable.getColumns().get(j);
            for (int i = 0; i < numRows; i++) {
                CellEditEvent<ObservableList<Object>, Number> event;
                event = new CellEditEvent<ObservableList<Object>, Number>(myTable, 
                        new TablePosition<ObservableList<Object>, Number>(myTable, i, col),
                        TableColumn.editCommitEvent(), (Number)((ObservableList<Object>)updatedItems.get(i)).get(j));
                Event.fireEvent(col, event);
            }
        }
        
        return res;
    }
    
}

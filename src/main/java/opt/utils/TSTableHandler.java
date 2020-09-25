/**
 * Copyright (c) 2020, Regents of the University of California
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
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
import javafx.scene.input.MouseEvent;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.NumberStringConverter;


/**
 * Event handling class for time series tables.
 * 
 * @author Alex Kurzhanskiy
 */
public class TSTableHandler {
    private DoubleStringConverter dsc = new ModifiedDoubleStringConverter();
    private TableView<ObservableList<Object>> myTable = null;
    private ObservableList<Object> defaultRow = null;
    TablePosition prevFocusedCell = null;
    TablePosition focusedCell = null;
    private int dt = 5;
    
    int minSelectedRow = 0;
    int maxSelectedRow = 0;
    int minSelectedColumn = 0;
    int maxSelectedColumn = 0;
    
    
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
    
    public void setDefaultRow(ObservableList<Object> myRow) {
        if (myRow != null)
            defaultRow = myRow;
    }
    
    public void resetFocus() {
        prevFocusedCell = null;
        focusedCell = null;
    }
    
    
    
    public void onMouseClicked(MouseEvent event) {
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        if (event.isShiftDown())
            return;
        
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        
        minSelectedRow = maxSelectedRow = i0;
        minSelectedColumn = maxSelectedColumn = j0;
        if (j0 == 0)
            maxSelectedColumn = myTable.getColumns().size() - 1;
        selectBox();
        myTable.getFocusModel().focus(i0, myTable.getColumns().get(j0));
        if ((j0 > 0) && (event.getClickCount() == 2))
            myTable.edit(i0, myTable.getColumns().get(j0));
        //event.consume();
        prevFocusedCell = null;
    }
    
    
    public void onMouseClicked2() {
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        
        minSelectedRow = maxSelectedRow = i0;
        minSelectedColumn = maxSelectedColumn = j0;
        if (j0 == 0)
            maxSelectedColumn = myTable.getColumns().size() - 1;
        selectBox();
        prevFocusedCell = null;
    }
    
    
    public void setEditOn() {
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        
        myTable.edit(i0, myTable.getColumns().get(j0));
    }
    
    
    
    public boolean onKeyPressed(KeyEvent event) {
        boolean res = false;
        if (prevFocusedCell == null)
            prevFocusedCell = focusedCell;
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        if (prevFocusedCell == null)
            prevFocusedCell = focusedCell;
        
        int row = focusedCell.getRow();
        int col = focusedCell.getColumn();
        
        if ((event.getCode() == KeyCode.C) && event.isControlDown()) {
            return copyToClipboard();
        }
        
        if ((event.getCode() == KeyCode.V) && event.isControlDown()) {
            return pasteFromClipboard();
        }
        
        
        if ((event.getCode() == KeyCode.D) && event.isControlDown()) {
            return duplicateRow();
        }
        
        
        if (event.isShiftDown()) {
            computeSelectedBox();
            
            if (event.getCode() == KeyCode.LEFT) {
                selectLeft();
            } else if ((event.getCode() == KeyCode.RIGHT) || (event.getCode() == KeyCode.TAB)) {
                selectRight();
            } else if (event.getCode() == KeyCode.UP) {
                selectUp();
            } else if (event.getCode() == KeyCode.DOWN) {
                selectDown();
            }
            
            event.consume();           
            prevFocusedCell = focusedCell;
            
            return false;
        }
        
        
        if (event.getCode() == KeyCode.LEFT) {
            res = moveBack();
            event.consume();
            //return false;
        }
        
        if ((event.getCode() == KeyCode.RIGHT) || (event.getCode() == KeyCode.TAB)) {
            res = moveForward();
            event.consume();
            //return false;
        }
        
        if (event.getCode() == KeyCode.UP) {
            res = moveUp();
            event.consume();
            //return false;
        }
        
        if (event.getCode() == KeyCode.DOWN) {
            res = moveDown();
            event.consume();
            //return false;
        }
        
        prevFocusedCell = focusedCell;
        
        return res;
    }
    
    
    public boolean onKeyPressed2(KeyCode kc) {
        boolean res = false;
        if (prevFocusedCell == null)
            prevFocusedCell = focusedCell;
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        if (prevFocusedCell == null)
            prevFocusedCell = focusedCell;
        
        if (kc == KeyCode.LEFT) {
            res = moveBack();
        }
        
        if ((kc == KeyCode.RIGHT) || (kc == KeyCode.TAB)) {
            res = moveForward();
        }
        
        if (kc == KeyCode.UP) {
            res = moveUp();
        }
        
        if (kc == KeyCode.DOWN) {
            res = moveDown();
        }
        
        prevFocusedCell = focusedCell;
        
        return res;
    }
    
    
    
    
    public void onFocusChanged() {
        prevFocusedCell = focusedCell;
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        if (prevFocusedCell == null)
            prevFocusedCell = focusedCell;
        
        int row = focusedCell.getRow();
        int col = focusedCell.getColumn();
        //myTable.getSelectionModel().clearSelection();
        //myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        //myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
    }
    
    
    public void onSelection(ObservableList<Object> newSelection) {
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        int numCols = myTable.getColumns().size();
        
       // TODO
        
    }
    
    
    
    
    
    /***************************************************************************
     * Table data manipulation
     * 
     ***************************************************************************/
    
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
     * Copy table data to clipboard.
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
                    String cs = textCols[jj-j0+offset];
                    cs = cs.replaceAll(",", "");
                    double val = dsc.fromString(cs);
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
            String textCols[] = textLines[ii-i0].split("\\s+");
            int numSubs = textCols.length;
            int jj = j0;
            int offset = 0;
            if (textCols[0].indexOf(':') >= 0)
                offset++;
            
            while ((jj < numCols) && (jj-j0+offset < numSubs)) {
                try {
                    double val = dsc.fromString(textCols[jj-j0+offset]);
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
        myTable.getFocusModel().focus(i0, myTable.getColumns().get(j0));
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
    
    
    /**
     * Duplicate table row.
     * @return <true> if table content changed, <false> otherwise.
     */
    private boolean duplicateRow() {
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        ObservableList<ObservableList<Object>> myItems = myTable.getItems();
        ObservableList<ObservableList<Object>> updatedItems = FXCollections.observableArrayList();
        
        for (int i = 0; i <= i0; i++) {
            updatedItems.add(myItems.get(i));
        }
        
        ObservableList<Object> srcRow = myItems.get(i0);
        ObservableList<Object> row = FXCollections.observableArrayList();
        row.add(opt.utils.Misc.minutes2timeString((i0+1)*dt));
        for (int j = 1; j < numCols; j++) {
            row.add(srcRow.get(j));
        }
        updatedItems.add(row);
        
        for (int i = i0+1; i < numRows; i++) {
            row = FXCollections.observableArrayList();
            row.add(opt.utils.Misc.minutes2timeString(updatedItems.size()*dt));
            
            for (int j = 1; j < numCols; j++) {
                row.add(myItems.get(i).get(j));
            }
            updatedItems.add(row); 
        }
        
        myTable.getItems().clear();
        myTable.getItems().addAll(updatedItems);
        myTable.refresh();
        myTable.getFocusModel().focus(i0+1, myTable.getColumns().get(j0));
        focusedCell = focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        return true;
    }
    
    
    /**
     * Add table row.
     * @return <true> if table content changed, <false> otherwise.
     */
    public boolean addRow() {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        int i0 = numRows - 1;
        int j0 = 1;
        
        ObservableList<ObservableList<Object>> myItems = myTable.getItems();
        ObservableList<ObservableList<Object>> updatedItems = FXCollections.observableArrayList();
        
        for (int i = 0; i <= i0; i++) {
            updatedItems.add(myItems.get(i));
        }
        
        ObservableList<Object> srcRow = defaultRow;
        if (srcRow == null)
            srcRow = myItems.get(i0);
        ObservableList<Object> row = FXCollections.observableArrayList();
        row.add(opt.utils.Misc.minutes2timeString((i0+1)*dt));
        for (int j = 1; j < numCols; j++) {
            row.add(srcRow.get(j));
        }
        updatedItems.add(row);
        
        for (int i = i0+1; i < numRows; i++) {
            row = FXCollections.observableArrayList();
            row.add(opt.utils.Misc.minutes2timeString(updatedItems.size()*dt));
            
            for (int j = 1; j < numCols; j++) {
                row.add(myItems.get(i).get(j));
            }
            updatedItems.add(row); 
        }
        
        myTable.getItems().clear();
        myTable.getItems().addAll(updatedItems);
        myTable.refresh();
        myTable.getFocusModel().focus(i0+1, myTable.getColumns().get(j0));
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        prevFocusedCell = null;
        myTable.getSelectionModel().clearSelection();
        myTable.getSelectionModel().select(focusedCell.getRow(), myTable.getColumns().get(j0));
        
        return true;
    }
    
    
    /**
     * Delete table rows whose cells are selected.
     * @return number of deleted rows.
     */
    public int deleteRows() {
        ObservableList<TablePosition> positionList = myTable.getSelectionModel().getSelectedCells();
        Set<Integer> marked_rows = new HashSet<Integer>();

	for (TablePosition position : positionList) {
            marked_rows.add(position.getRow());
	}
        
        ObservableList<ObservableList<Object>> myItems = myTable.getItems();
        int numRows = myItems.size();
        int numCols = myTable.getColumns().size();
        ObservableList<ObservableList<Object>> updatedItems = FXCollections.observableArrayList();
        
        for (int i = 0; i < numRows; i++) {
            if (marked_rows.contains(i))
                continue;
            ObservableList<Object> row = FXCollections.observableArrayList();
            row.add(opt.utils.Misc.minutes2timeString(updatedItems.size()*dt));
            for (int j = 1; j < numCols; j++) {
                row.add(myItems.get(i).get(j));
            }
            updatedItems.add(row);
        }
        
        while (updatedItems.size() < 1) {
            ObservableList<Object> row = FXCollections.observableArrayList();
            row.add(opt.utils.Misc.minutes2timeString(updatedItems.size()*dt));
            for (int j = 1; j < numCols; j++) {
                row.add(new Double(0));
            }
            updatedItems.add(row);
        }
        
        int res = myItems.size() - updatedItems.size();

        myTable.getItems().clear();
        myTable.getItems().addAll(updatedItems);
        myTable.refresh();
        
        return res;
    }
    
    
    
    
    
    /***************************************************************************
     * Navigation with cell selection
     ***************************************************************************/
    
    private void selectLeft() {
        int col = prevFocusedCell.getColumn() - 1;
        
        if (col < 0)
            return;
        
        if (col <= minSelectedColumn) {
            minSelectedColumn = col;
        } else {
            maxSelectedColumn = col;
        }
        
        selectBox();
        myTable.getFocusModel().focus(focusedCell.getRow(), myTable.getColumns().get(col));
    }
    
    private void selectRight() {
        int col = prevFocusedCell.getColumn() + 1;
        
        if (col >= myTable.getColumns().size())
            return;
        
        if (col >= maxSelectedColumn) {
            maxSelectedColumn = col;
        } else {
            minSelectedColumn = col;
        }
        
        selectBox();
        myTable.getFocusModel().focus(focusedCell.getRow(), myTable.getColumns().get(col));
    }
    
    private void selectUp() {
        int row = prevFocusedCell.getRow() - 1;
        
        if (row < 0)
            return;
        
        if (row <= minSelectedRow) {
            minSelectedRow = row;
        } else {
            maxSelectedRow = row;
        }
        
        selectBox();
        myTable.getFocusModel().focus(row, focusedCell.getTableColumn());
    }
    
    private void selectDown() {
        int row = prevFocusedCell.getRow() + 1;
        
        if (row >= myTable.getItems().size())
            return;
        
        if (row >= maxSelectedRow) {
            maxSelectedRow = row;
        } else {
            minSelectedRow = row;
        }
        
        selectBox();
        myTable.getFocusModel().focus(row, focusedCell.getTableColumn());
    }
    
    
    
    
    
    /***************************************************************************
     * Navigation with a single cell selection
     ***************************************************************************/
    
    private boolean moveBack() {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = prevFocusedCell.getRow();
        int col = prevFocusedCell.getColumn() - 1;
        myTable.getSelectionModel().clearSelection();
        
        if (col < 1) {
            row--;
            col = numCols - 1;
        }
        
        if (row >= 0) {
            myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
            myTable.edit(row, myTable.getColumns().get(col));
        }
        else {
            myTable.getSelectionModel().select(0, myTable.getColumns().get(1));
            myTable.edit(0, myTable.getColumns().get(1));
        }
            
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        return false;
    }
    
    
    private boolean moveForward() {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = prevFocusedCell.getRow();
        int col = prevFocusedCell.getColumn() + 1;
        
        boolean res = false;
        
        if (col >= numCols) {
            row++;
            col = 1;
        }
        
        if (row >= numRows) {
            row = numRows;
            res = true;
            addRow();
        }
        
        myTable.getSelectionModel().clearSelection();
        myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
        if (!res)
            myTable.edit(row, myTable.getColumns().get(col));
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        return res;
    }
    
    
    private boolean moveUp() {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = prevFocusedCell.getRow() - 1;
        int col = prevFocusedCell.getColumn();
        myTable.getSelectionModel().clearSelection();
        
        boolean res = false;
        
        if (row < 1) {
            row = 0;
            res = true;
        }
        
        if (col < numCols) {
            myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
            myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
        }
        
        if (col == 0) {
            minSelectedRow = maxSelectedRow = row;
            minSelectedColumn = 0;
            maxSelectedColumn = myTable.getColumns().size() - 1;
            selectBox();
            myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        } else {
            myTable.edit(row, myTable.getColumns().get(col));
        }
        
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        return res;
    }
    
    
    private boolean moveDown() {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = prevFocusedCell.getRow() + 1;
        int col = prevFocusedCell.getColumn();
        
        boolean res = false;
        
        if (row >= numRows) {
            //row = numRows - 1;
            row = numRows;
            res = true;
            addRow();
        }
        
        myTable.getSelectionModel().clearSelection();
        
        if (col < numCols) {
            myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
            myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
        }
        
        if (col == 0) {
            minSelectedRow = maxSelectedRow = row;
            minSelectedColumn = 0;
            maxSelectedColumn = myTable.getColumns().size() - 1;
            selectBox();
            myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        } else if (!res) {
            myTable.edit(row, myTable.getColumns().get(col));
        }
        
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        
        return res;
    }
        
    
    
    
    
    /***************************************************************************
     * Auxiliary functions
     ***************************************************************************/
    
    private void computeSelectedBox() {
        focusedCell = myTable.focusModelProperty().get().focusedCellProperty().get();
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        ObservableList<TablePosition> positionList = myTable.getSelectionModel().getSelectedCells();
        
        minSelectedRow = maxSelectedRow = i0;
        minSelectedColumn = maxSelectedColumn = j0;
        for (TablePosition pos : positionList) {
            minSelectedRow = Math.min(minSelectedRow, pos.getRow());
            maxSelectedRow = Math.max(maxSelectedRow, pos.getRow());
            minSelectedColumn = Math.min(minSelectedColumn, pos.getColumn());
            maxSelectedColumn = Math.max(maxSelectedColumn, pos.getColumn());
        }
    }
    
    
    private void selectBox() {
        myTable.getSelectionModel().clearSelection();
        myTable.getSelectionModel().selectRange(minSelectedRow, 
                                                myTable.getColumns().get(minSelectedColumn),
                                                maxSelectedRow,
                                                myTable.getColumns().get(maxSelectedColumn));
    }
    
    
    public void setColumnValue(int col, double val) {
        if (focusedCell == null) 
            return;
        int i0 = focusedCell.getRow();
        int j0 = focusedCell.getColumn();
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        if ((col < 1) || (col >= numCols))
            return;
        
        for (int i = 0; i < numRows; i++) {
            myTable.getItems().get(i).set(col, val);
        }
        
        myTable.refresh();
        myTable.getFocusModel().focus(i0, myTable.getColumns().get(j0));
    }
    
    
}

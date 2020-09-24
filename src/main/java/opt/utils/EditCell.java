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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;
import opt.UserSettings;



/**
 *
 * @author Alex Kurzhanskiy
 */
public class EditCell<S, T> extends TextFieldTableCell<S, T> {

    private TextField textField;
    private boolean ignoreFirstCol = false;
    private boolean escapePressed = false;
    private TablePosition<S, ?> tablePos = null;
    
    
    int minSelectedRow = 0;
    int maxSelectedRow = 0;
    int minSelectedColumn = 0;
    int maxSelectedColumn = 0;
    
    
    T myNewValue;
    
    public EditCell(final StringConverter<T> converter) {
	super(converter);
        
        setOnDragDetected(new EventHandler<MouseEvent>() {  
            @Override  
            public void handle(MouseEvent event) {  
                startFullDrag();  
                getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());  
            }  
        });  
        
        setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {  
            @Override  
            public void handle(MouseDragEvent event) {
                computeSelectedBox();
                recomputeSelectedBox(getIndex(), getTableColumn().getTableView().getColumns().indexOf(getTableColumn()));
                //getTableColumn().getTableView().getSelectionModel().select(getIndex(), getTableColumn());  
            }  
                
        });  
    }
    
    public EditCell(final StringConverter<T> converter, boolean ignoreFirstCol) {
	this(converter);
        this.ignoreFirstCol = ignoreFirstCol;
    }

    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> forTableColumn() {
	return forTableColumn(new DefaultStringConverter());
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter) {
	return list -> new EditCell<S, T>(converter);
    }
    
    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(final StringConverter<T> converter, boolean ignoreFirstCol) {
	return list -> new EditCell<S, T>(converter, ignoreFirstCol);
    }

    @Override
    public void startEdit() {
	if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
            return;
	}
	
        super.startEdit();

	if (isEditing()) {
            if (textField == null) {
		textField = getTextField();
            }
            escapePressed = false;
            startEdit(textField);
            final TableView<S> table = getTableView();
            tablePos = table.getEditingCell();
	}
    }

    /** {@inheritDoc} */
    @Override
    public void commitEdit(T newValue) {
        if (!isEditing())
            return;
        
        myNewValue = newValue;
        
        if ((newValue == null) || (newValue.equals("")))
            super.cancelEdit(); 
            
        final TableView<S> table = getTableView();
        if (table != null) {
            // Inform the TableView of the edit being ready to be committed.
            CellEditEvent editEvent = new CellEditEvent(table, tablePos, TableColumn.editCommitEvent(), newValue);
            Event.fireEvent(getTableColumn(), editEvent);
        }
        // we need to setEditing(false):
        super.cancelEdit(); // this fires an invalid EditCancelEvent.
        // update the item within this cell, so that it represents the new value
        updateItem(newValue, false);
        if (table != null) {
            // reset the editing cell on the TableView
            table.edit(-1, null);
        }
    }
    
    

    /** {@inheritDoc} */
    @Override
    public void cancelEdit() {
        if (escapePressed) {
            // this is a cancel event after escape key
            super.cancelEdit();
            setText(getItemText()); // restore the original text in the view
        } else {
            // this is not a cancel event after escape key
            // we interpret it as commit.
            String newText = textField.getText();
            // commit the new text to the model
            this.commitEdit(getConverter().fromString(newText));
        }
        setGraphic(null); // stop editing with TextField
    }

    

    private TextField getTextField() {
        final TextField textField = new TextField(getItemText());

	textField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
		System.out.println("hi");
            }
	});

	// Use onAction here rather than onKeyReleased (with check for Enter),
	textField.setOnAction(event -> {
            if (getConverter() == null) {
		throw new IllegalStateException("StringConverter is null.");
            }
            this.commitEdit(getConverter().fromString(textField.getText()));
            event.consume();
	});

	textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (!newValue) {
                    commitEdit(getConverter().fromString(textField.getText()));
                    }
		}
	});

	textField.setOnKeyPressed(t -> {
            if (t.getCode() == KeyCode.ESCAPE)
		escapePressed = true;
            else
		escapePressed = false;
	});
	textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
		throw new IllegalArgumentException("did not expect esc key releases here.");
            }
	});

	textField.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            TablePosition<S, ?> focusedCell = getTableView().focusModelProperty().get().focusedCellProperty().get();
            if (event.getCode() == KeyCode.ESCAPE) {
		textField.setText(getConverter().toString(getItem()));
		cancelEdit();
                event.consume();
            } else if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.TAB) {
                moveForward(getTableView(), focusedCell);
		//getTableView().getSelectionModel().selectNext();
                //getTableView().getSelectionModel().clearSelection(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
                //UserSettings.linkEditorController.updateTablePositions(KeyCode.RIGHT);
            } else if (event.getCode() == KeyCode.LEFT) {
                moveBack(getTableView(), focusedCell);
		//getTableView().getSelectionModel().selectPrevious();
                //getTableView().getSelectionModel().clearSelection(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
                //UserSettings.linkEditorController.updateTablePositions(KeyCode.LEFT);
            } else if (event.getCode() == KeyCode.UP) {
		getTableView().getSelectionModel().selectPrevious();
                //moveUp(getTableView(), focusedCell);
                getTableView().getSelectionModel().clearSelection();
                getTableView().getSelectionModel().select(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
                //UserSettings.linkEditorController.updateTablePositions(KeyCode.UP);
            } else if (event.getCode() == KeyCode.DOWN) {
		getTableView().getSelectionModel().selectBelowCell();
                //moveDown(getTableView(), focusedCell);
                getTableView().getSelectionModel().clearSelection();
                getTableView().getSelectionModel().select(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
                //UserSettings.linkEditorController.updateTablePositions(KeyCode.DOWN);
            }
	});

	return textField;
    }

    private String getItemText() {
        return getConverter() == null ? getItem() == null ? "" : getItem().toString() : getConverter().toString(getItem());
    }
    
    /** {@inheritDoc} */
    @Override
    public void updateItem(T item, boolean empty) {
	super.updateItem(item, empty);
	updateItem();
    }

    private void updateItem() {
	if (isEmpty()) {
            setText(null);
            setGraphic(null);
	} else {
            if (isEditing()) {
		if (textField != null) {
                    textField.setText(getItemText());
		}
		setText(null);
		setGraphic(textField);
            } else {
		setText(getItemText());
		setGraphic(null);
            }
	}
    }

    private void startEdit(final TextField textField) {
	if (textField != null) {
            textField.setText(getItemText());
	}
	setText(null);
	setGraphic(textField);
	textField.selectAll();
	// requesting focus so that key input can immediately go into the
	// TextField
	textField.requestFocus();
    }
    
    
    private void moveBack(TableView<S> myTable, TablePosition<S, ?> focusedCell) {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = focusedCell.getRow();
        int col = focusedCell.getColumn();
        myTable.getSelectionModel().clearSelection();
        
        if (col < (ignoreFirstCol ? 1 : 0)) {
            if (row > 0) {
                row--;
                col = numCols - 1;
            }
        }
        
        myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
    }
    
    
    private void moveUp(TableView<S> myTable, TablePosition<S, ?> focusedCell) {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = focusedCell.getRow() - 1;
        int col = focusedCell.getColumn();
        
        myTable.getSelectionModel().selectPrevious();
        myTable.getSelectionModel().clearSelection();
        
        if (row < 0) {
            row = 0;
        }
        
        myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
    }
    
    
    private void moveForward(TableView<S> myTable, TablePosition<S, ?> focusedCell) {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = focusedCell.getRow();
        int col = focusedCell.getColumn();
        myTable.getSelectionModel().clearSelection();
        
        if (col >= numCols) {
            if (row < numRows - 1) {
                row++;
                col = ignoreFirstCol ? 1 : 0;
            }
        }
        
        myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
    }
    
    
    private void moveDown(TableView<S> myTable, TablePosition<S, ?> focusedCell) {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = focusedCell.getRow() + 1;
        int col = focusedCell.getColumn();
        myTable.getSelectionModel().clearSelection();
        
        if (row >= numRows) {
            row = numRows - 1;
        }
        
        myTable.getFocusModel().focus(row, myTable.getColumns().get(col));
        myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
    }
    
    
    
    
    
    /***************************************************************************
     * Auxiliary functions
     ***************************************************************************/
    
    private void computeSelectedBox() {
        TableView<S> myTable = getTableColumn().getTableView();
        int i0 = getIndex();
        int j0 = myTable.getColumns().indexOf(getTableColumn());
        ObservableList<TablePosition> positionList = myTable.getSelectionModel().getSelectedCells();
        
        minSelectedRow = myTable.getItems().size();
        maxSelectedRow = 0;
        minSelectedColumn = myTable.getColumns().size();
        maxSelectedColumn = 0;
        for (TablePosition pos : positionList) {
            minSelectedRow = Math.min(minSelectedRow, pos.getRow());
            maxSelectedRow = Math.max(maxSelectedRow, pos.getRow());
            minSelectedColumn = Math.min(minSelectedColumn, pos.getColumn());
            maxSelectedColumn = Math.max(maxSelectedColumn, pos.getColumn());
        }
    }
    
    
    private void recomputeSelectedBox(int i1, int j1) {
        if ((i1 >= minSelectedRow) && (i1 <= maxSelectedRow) &&
            (j1 >= minSelectedColumn) && (j1 <= maxSelectedColumn)) {
            TablePosition focusedCell = getTableColumn().getTableView().focusModelProperty().get().focusedCellProperty().get();
            int i0 = focusedCell.getRow();
            int j0 = focusedCell.getColumn();
            if (i1 > i0) {
                minSelectedRow = i1;
            } else if (i1 < i0) {
                maxSelectedRow = i1;
            }
            if (j1 > j0) {
                minSelectedColumn = j1;
            } else if (j1 < j0) {
                maxSelectedColumn = j1;
            }   
        } else {
            minSelectedRow = Math.min(minSelectedRow, i1);
            maxSelectedRow = Math.max(maxSelectedRow, i1);
            minSelectedColumn = Math.min(minSelectedColumn, j1);
            maxSelectedColumn = Math.max(maxSelectedColumn, j1);
        }
        
        selectBox();
        getTableColumn().getTableView().getFocusModel().focus(i1, getTableColumn().getTableView().getColumns().get(j1));
    }
    
    
    private void selectBox() {
        if ((minSelectedRow > maxSelectedRow) || (minSelectedColumn > maxSelectedColumn))
            return;
        
        TableView<S> myTable = getTableColumn().getTableView();
        myTable.getSelectionModel().clearSelection();
        myTable.getSelectionModel().selectRange(minSelectedRow, 
                                                myTable.getColumns().get(minSelectedColumn),
                                                maxSelectedRow,
                                                myTable.getColumns().get(maxSelectedColumn));
    }
}
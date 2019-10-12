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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;



/**
 *
 * @author Alex Kurzhanskiy
 */
public class EditCell<S, T> extends TextFieldTableCell<S, T> {

    private TextField textField;
    private boolean ignoreFirstCol = false;
    private boolean escapePressed = false;
    private TablePosition<S, ?> tablePos = null;

    public EditCell(final StringConverter<T> converter) {
	super(converter);
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

    /** {@inheritDoc} */
    @Override
    public void updateItem(T item, boolean empty) {
	super.updateItem(item, empty);
	updateItem();
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
            } else if (event.getCode() == KeyCode.LEFT) {
                moveBack(getTableView(), focusedCell);
		//getTableView().getSelectionModel().selectPrevious();
                //getTableView().getSelectionModel().clearSelection(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
            } else if (event.getCode() == KeyCode.UP) {
		getTableView().getSelectionModel().selectAboveCell();
                getTableView().getSelectionModel().clearSelection(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
		getTableView().getSelectionModel().selectBelowCell();
                getTableView().getSelectionModel().clearSelection(focusedCell.getRow(), focusedCell.getTableColumn());
		event.consume();
            }
	});

	return textField;
    }

    private String getItemText() {
        return getConverter() == null ? getItem() == null ? "" : getItem().toString() : getConverter().toString(getItem());
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
        int col = focusedCell.getColumn() - 1;
        myTable.getSelectionModel().clearSelection();
        
        if (col < (ignoreFirstCol ? 1 : 0)) {
            row--;
            col = numCols - 1;
        }
        
        if (row >= 0) {
             myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
        }
    }
    
    
    private void moveForward(TableView<S> myTable, TablePosition<S, ?> focusedCell) {
        int numRows = myTable.getItems().size();
        int numCols = myTable.getColumns().size();
        
        int row = focusedCell.getRow();
        int col = focusedCell.getColumn() + 1;
        myTable.getSelectionModel().clearSelection();
        
        if (col >= numCols) {
            row++;
            col = ignoreFirstCol ? 1 : 0;
        }
        
        if (row < numRows) {
             myTable.getSelectionModel().select(row, myTable.getColumns().get(col));
        }
    }
}
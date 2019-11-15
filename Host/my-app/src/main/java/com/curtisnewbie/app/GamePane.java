package com.curtisnewbie.app;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.event.*;

/**
 * A {@code GridPane} that draws a TicTacToe game board, it has a number of
 * methods for getting information from the gui as well as methods to
 * control/update the gui. For example, it draws the "X" when user clicks on the
 * button, and it freezes button onces the button is clicked. It also provides
 * methods to update game board (when opponent makes a move), such as the
 * {@code oppponentMove()} method.
 */
public class GamePane extends GridPane {

    /** Current player is always CROSS */
    private final int CROSS = 1;

    /** Other player is always CIRCLE */
    private final int CIRCLE = 2;

    /** Has never been selected */
    private final int EMPTY = 0;

    /** Indicate whether the game has finished */
    private boolean finished;

    /**
     * Indicate whether user has moved. This is for current user only not for the
     * opponent
     */
    private boolean moved;

    /**
     * List of buttons represent the nine squares on the tictaktoe game board, and
     * which are used to detect where the user clicks on.
     */
    private Button[][] buttons;

    /** Two dimensional array represents this game board */
    private int[][] gameBoard;

    /**
     * Record what the last step is. First element indicates row and second element
     * indicates column. This method is for current user rather than the opponant.
     */
    private int[] lastStep;

    public GamePane() {
        this.moved = false;
        this.gameBoard = new int[3][3];
        this.lastStep = new int[2];
        this.buttons = new Button[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new Button();
                buttons[i][j].setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            }
        }

        // add the nine buttons to this pane
        addButtonsToPane();

        // setup the event handlers for the buttons, which update the two-dimentional
        // gameBoard when necessary
        setupEventHandlers();

        // make the child nodes grow vertically and horizontally
        ColumnConstraints col = new ColumnConstraints();
        col.setPercentWidth(100 / 3);
        RowConstraints row = new RowConstraints();
        row.setPercentHeight(100 / 3);
        this.getColumnConstraints().addAll(col, col, col);
        this.getRowConstraints().addAll(row, row, row);
    }

    /**
     * Put the nine buttons (which represent the areas to be clicked by the users)
     * to the proper positions on this pane.
     * 
     */
    private void addButtonsToPane() {
        this.add(buttons[0][0], 0, 0);
        this.add(buttons[0][1], 1, 0);
        this.add(buttons[0][2], 2, 0);
        this.add(buttons[1][0], 0, 1);
        this.add(buttons[1][1], 1, 1);
        this.add(buttons[1][2], 2, 1);
        this.add(buttons[2][0], 0, 2);
        this.add(buttons[2][1], 1, 2);
        this.add(buttons[2][2], 2, 2);
    }

    private void setupEventHandlers() {
        buttons[0][0].setOnAction(new ClickHandler(0, 0));
        buttons[0][1].setOnAction(new ClickHandler(0, 1));
        buttons[0][2].setOnAction(new ClickHandler(0, 2));
        buttons[1][0].setOnAction(new ClickHandler(1, 0));
        buttons[1][1].setOnAction(new ClickHandler(1, 1));
        buttons[1][2].setOnAction(new ClickHandler(1, 2));
        buttons[2][0].setOnAction(new ClickHandler(2, 0));
        buttons[2][1].setOnAction(new ClickHandler(2, 1));
        buttons[2][2].setOnAction(new ClickHandler(2, 2));
    }

    /**
     * Refresh the view of this GamePane, it is called internally everytime a button
     * is clicked
     */
    private void refresh() {
        this.getChildren().clear();
        addButtonsToPane();
    }

    /**
     * Check whether this step wins.
     * 
     * @param r row
     * @param c column
     * @return whether this step wins
     */
    public boolean hasWon() {

        // check each column
        for (int i = 0; i < 3; i++) {
            if (gameBoard[0][i] == CROSS && gameBoard[1][i] == CROSS && gameBoard[2][i] == CROSS) {
                return true;
            }
        }

        // check each row
        for (int i = 0; i < 3; i++) {
            if (gameBoard[i][0] == CROSS && gameBoard[i][1] == CROSS && gameBoard[i][2] == CROSS) {
                return true;
            }
        }

        // check diagonal
        if (gameBoard[0][0] == CROSS && gameBoard[1][1] == CROSS && gameBoard[2][2] == CROSS)
            return true;
        if (gameBoard[0][2] == CROSS && gameBoard[1][1] == CROSS && gameBoard[2][0] == CROSS)
            return true;

        return false;
    }

    /**
     * Whether the gameboard still has empty cell to select.
     * 
     * @return {@code true} if there are empty cells, {@code false} if there is no
     *         empty cell.
     */
    public boolean isFull() {
        for (int[] row : gameBoard)
            for (int b : row)
                if (b == EMPTY)
                    return false;

        return true;
    }

    /**
     * This method is used to update the gameboard as opponent click on a cell. This
     * method draws "O" on the gameboard instead of the "X". This method also
     * updates {@code moved} variable, as it indicates user can move now.
     * 
     * @param row row
     * @param col col
     */
    public void opponentMove(int row, int col) {
        // it's current user's turn to move
        this.moved = false;

        gameBoard[row][col] = CIRCLE;
        buttons[row][col].setDisable(true);
        buttons[row][col].setText("0");
        refresh();
        if (hasWon()) {
            this.finished = true;
            freeze();
            var dial = new Alert(AlertType.INFORMATION);
            dial.setContentText("You Lost!");
            dial.showAndWait();
        } else {
            if (isFull()) {
                this.finished = true;
                var dial = new Alert(AlertType.INFORMATION);
                dial.setContentText("Ends, Nobody Wins!");
                dial.showAndWait();
            }
        }
    }

    public void freeze() {
        this.setDisable(true);
    }

    public void unfreeze() {
        this.setDisable(false);
    }

    public boolean isFinished() {
        return this.finished;
    }

    /**
     * Get the last step that user went to
     * 
     * @return {@code Null} if user hasn't moved yet, else {@code int[]} where [0]
     *         is row and [1] is column.
     */
    public int[] getLastStep() {
        if (!hasMoved())
            return null;
        else
            return new int[] { lastStep[0], lastStep[1] };
    }

    /**
     * Check whether user has moved.
     * 
     * @return {@code True} when user has moved, {@code False} when user hasn't
     *         moved yet.
     */
    public boolean hasMoved() {
        return moved;
    }

    /** Handler for the nine buttons ActionEvent */
    public class ClickHandler implements EventHandler<ActionEvent> {

        private int row;
        private int col;

        public ClickHandler(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void handle(ActionEvent event) {
            // current user has moved
            GamePane.this.moved = true;

            gameBoard[row][col] = CROSS;
            buttons[row][col].setDisable(true);
            buttons[row][col].setText("X");
            lastStep[0] = row;
            lastStep[1] = col;
            refresh();

            // check whether current user wins
            if (hasWon()) {
                System.out.println("Win?");
                GamePane.this.finished = true;
                freeze();
                var dial = new Alert(AlertType.INFORMATION);
                dial.setContentText("You Win!");
                dial.showAndWait();
            } else {
                if (isFull()) {
                    GamePane.this.finished = true;
                    var dial = new Alert(AlertType.INFORMATION);
                    dial.setContentText("Ends, Nobody Wins!");
                    dial.showAndWait();
                }
            }
        }
    }
}
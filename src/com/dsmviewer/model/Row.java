package com.dsmviewer.model;

public class Row {
    private int number;
    private Cell[] cells;

    public Row(final int number, final Cell[] cells)
    {
        this.number = number;
        this.cells = cells;
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public Cell[] getCells() {
        return cells;
    }
    public void setCells(Cell[] cells) {
        this.cells = cells;
    }
    public Cell getCellAt(int index) {
        return cells[index];
    }
    public void setCellAt(int index, Cell cell) {
        cells[index] = cell;
    }
}
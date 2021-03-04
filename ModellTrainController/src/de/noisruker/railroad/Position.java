package de.noisruker.railroad;

import java.util.Objects;

public class Position {

    private int x, y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public Position north() {
        return new Position(this.x, this.y - 1);
    }

    public Position south() {
        return new Position(this.x, this.y + 1);
    }

    public Position east() {
        return new Position(this.x + 1, this.y);
    }

    public Position west() {
        return new Position(this.x - 1, this.y);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new Position(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}

package com.linhan.maze.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by linhan on 16/6/20.
 */
public class StepResult {

    String letter;

    List<Point> adjacent;

    boolean end;

    public StepResult() {
        adjacent = new LinkedList<>();
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public List<Point> getAdjacent() {
        return adjacent;
    }

    public void setAdjacent(List<Point> adjacent) {
        this.adjacent = adjacent;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public void addPoint(Point point){
        adjacent.add(point);
    }

    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("letter:").append(letter).append(" point:");
        for(Point point : adjacent)
            builder.append(point.toString());
        builder.append(" isEnd:").append(end);
        return builder.toString();
    }
}

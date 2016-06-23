package com.linhan.maze.model;

/**
 * Created by linhan on 16/6/20.
 */
public class Node {

    Point point;
    Node father;

    public Node(Point point) {
        this.point = point;
    }

    public Node(int x, int y){
        this(new Point(x, y));
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }
}

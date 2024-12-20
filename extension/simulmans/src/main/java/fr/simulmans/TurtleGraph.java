package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class TurtleGraph {

    private Graph<Coords, DefaultWeightedEdge> graph;
    public Graph<Coords, DefaultWeightedEdge> getGraph() { return graph; }
    public void setGraph(Graph<Coords, DefaultWeightedEdge> graph) { this.graph = graph; }

    private Integer turtleSize;
    public Integer getTurtleSize() { return turtleSize; }
    public void setTurtleSize(Integer turtleSize) { this.turtleSize = turtleSize; }
}

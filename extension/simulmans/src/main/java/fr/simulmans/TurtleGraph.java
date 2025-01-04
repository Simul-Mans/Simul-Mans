package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class TurtleGraph {

    private Graph<Coords, DefaultWeightedEdge> graph;
    public Graph<Coords, DefaultWeightedEdge> getGraph() { return graph; }
    public void setGraph(Graph<Coords, DefaultWeightedEdge> graph) { this.graph = graph; }

    private Double turtleSize;
    public Double getTurtleSize() { return turtleSize; }
    public void setTurtleSize(Double turtleSize) { this.turtleSize = turtleSize; }
}

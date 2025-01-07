package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.Turtle;
import org.nlogo.api.AgentException;

public class Humans {

    public static final String BREED = "HUMAN";

    public static final int GRAPH_VARIABLE = 13;

    public static final int SPEED_VARIABLE = 16;

    @SuppressWarnings("unchecked")
    public static Graph<Coords, DefaultWeightedEdge> getGraph(Turtle t) { return (Graph<Coords, DefaultWeightedEdge>) t.getVariable(GRAPH_VARIABLE); }
    public static void setGraph(Graph<Coords, DefaultWeightedEdge> graph, Turtle t) throws AgentException { t.setVariable(GRAPH_VARIABLE, graph); }

    public static Double getSpeed(Turtle t) { return (double) t.getVariable(SPEED_VARIABLE); }
    public static void setSpeed(Double speed, Turtle t) throws AgentException {  t.setVariable(SPEED_VARIABLE, speed); }


}

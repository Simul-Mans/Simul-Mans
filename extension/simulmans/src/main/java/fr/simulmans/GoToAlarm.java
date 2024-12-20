package fr.simulmans;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.stream.Streams;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.*;
import org.nlogo.core.Shape;
import org.nlogo.core.Syntax;
import org.nlogo.shape.Element;
import org.nlogo.shape.VectorShape;

import java.util.Comparator;

public class GoToAlarm implements Command {
    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException {

        if(!(context.getAgent() instanceof Turtle turtle)){
            throw new ExtensionException("Not a Turtle");
        }

        TurtleGraph graph = (TurtleGraph) turtle.getVariable(0);

        // On récupère les alarmes incendies et leurs coordonnées
        AgentSet alarms = context.world().getBreed("buttons");

        AStarShortestPath<Coords, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph.getGraph(), (v1, v2) -> 0);

        Coords turtleCoords = new Coords(((int) turtle.xcor()) / graph.getTurtleSize(), ((int) turtle.ycor()) / graph.getTurtleSize());

        // On calcule le plus court chemin pour chaque alarme
        GraphPath<Coords, DefaultWeightedEdge> path = Streams.of(alarms.agents())
                .parallel()
                .map(agent -> {
                    // On calcule le plus court chemin pour chaque alarme
                    Turtle alarm = (Turtle) agent;

                    Coords alarmCoords = new Coords(((int) alarm.xcor())/ graph.getTurtleSize(), ((int) alarm.ycor()) / graph.getTurtleSize());
                    return aStarAlgorithm.getPath(turtleCoords, alarmCoords);
                })
                .min(Comparator.comparingInt(GraphPath::getLength))
                .orElseThrow();

        

    }

    @Override
    public Syntax getSyntax() {
        return null;
    }
}

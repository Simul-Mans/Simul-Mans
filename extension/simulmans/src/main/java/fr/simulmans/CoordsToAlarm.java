package fr.simulmans;

import org.apache.commons.lang3.stream.Streams;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.*;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CoordsToAlarm implements Reporter {
    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {

        if (!(context.getAgent() instanceof Turtle turtle)) {
            throw new ExtensionException("Not a Turtle");
        }

        Integer speed = ((Double) turtle.getVariable(16)).intValue();

        TurtleGraph graph = (TurtleGraph) turtle.getVariable(13);

        // On récupère les alarmes incendies et leurs coordonnées
        AgentSet alarms = context.world().getBreed("BUTTONS");

        AStarShortestPath<Coords, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph.getGraph(), (v1, v2) -> 0);

        Coords turtleCoords = new Coords((int) (turtle.ycor() / graph.getTurtleSize()), (int) (turtle.xcor() / graph.getTurtleSize()));

        // On calcule le plus court chemin pour chaque alarme
        GraphPath<Coords, DefaultWeightedEdge> path = Streams.of(alarms.agents())
                .parallel()
                .map(agent -> {
                    // On calcule le plus court chemin pour chaque alarme
                    Turtle alarm = (Turtle) agent;

                    Coords alarmCoords = new Coords((alarm.xcor()), (alarm.ycor()));
                    try {
                        return aStarAlgorithm.getPath(turtleCoords, alarmCoords);
                    }
                    catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .min(Comparator.comparingInt(GraphPath::getLength))
                .orElse(null);

        if(path == null){
            return LogoList.fromJava(List.of(turtle.xcor(), turtle.ycor()));
        }

        Coords startVertex = path.getVertexList().get(speed);
        Coords nextPathCoordinates = new Coords(startVertex.x() * graph.getTurtleSize().intValue(), startVertex.y() * graph.getTurtleSize().intValue());

        return LogoList.fromJava(List.of(nextPathCoordinates.x(), nextPathCoordinates.y()));
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(Syntax.ListType());
    }
}

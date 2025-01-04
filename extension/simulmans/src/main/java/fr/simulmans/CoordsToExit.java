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

public class CoordsToExit implements Reporter {
    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {

        if(!(context.getAgent() instanceof Turtle turtle)){
            throw new ExtensionException("Not a Turtle");
        }

        TurtleGraph graph = (TurtleGraph) turtle.getVariable(0);

        // On récupère les sorties et leurs coordonnées
        AgentSet alarms = context.world().getBreed("exit-doors");

        AStarShortestPath<Coords, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph.getGraph(), (v1, v2) -> 0);

        Coords turtleCoords = new Coords((int)( turtle.xcor() / graph.getTurtleSize()), (int)(turtle.ycor() / graph.getTurtleSize()));

        // On calcule le plus court chemin pour chaque sortie
        GraphPath<Coords, DefaultWeightedEdge> path = Streams.of(alarms.agents())
                .parallel()
                .map(agent -> {
                    // On calcule le plus court chemin pour chaque sortie
                    Turtle exit = (Turtle) agent;

                    Coords exitCoords = new Coords((int)(exit.xcor() / graph.getTurtleSize()), (int)( exit.ycor() / graph.getTurtleSize()));
                    return aStarAlgorithm.getPath(turtleCoords, exitCoords);
                })
                .min(Comparator.comparingInt(GraphPath::getLength))
                .orElseThrow();


        Coords startVertex = path.getStartVertex();
        Coords nextPathCoordinates = new Coords(startVertex.x() * graph.getTurtleSize().intValue(), startVertex.y() * graph.getTurtleSize().intValue());

        return LogoList.fromJava(List.of(nextPathCoordinates.x(), nextPathCoordinates.y()));
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(Syntax.ListType());
    }
}

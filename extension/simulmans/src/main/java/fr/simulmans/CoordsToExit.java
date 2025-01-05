package fr.simulmans;

import org.apache.commons.lang3.stream.Streams;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.*;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import scala.Int;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class CoordsToExit implements Reporter {
    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {

        if(!(context.getAgent() instanceof Turtle turtle)){
            throw new ExtensionException("Not a Turtle");
        }

        Integer ticksSinceLastPathfinding = ((Double) turtle.getVariable(14)).intValue();

        if(ticksSinceLastPathfinding % 5 != 0){
            GraphPath<Coords, DefaultWeightedEdge> path = (GraphPath<Coords, DefaultWeightedEdge>) turtle.getVariable(15);

            Integer speed = ((Double) turtle.getVariable(16)).intValue();

            Coords startVertex = path.getVertexList().get(speed * (5 - (ticksSinceLastPathfinding % 5)));

            LogoListBuilder builder = new LogoListBuilder();

            try {
                turtle.setVariable(14, (double) (ticksSinceLastPathfinding + 1));
            } catch (AgentException e) {
                throw new ExtensionException(e);
            }

            builder.add(startVertex.x());
            builder.add(startVertex.y());
        }

        TurtleGraph graph = (TurtleGraph) turtle.getVariable(13);

        // On récupère les sorties et leurs coordonnées
        AgentSet doors = context.world().getBreed("EXIT-DOORS");

        if(doors == null){
            throw new ExtensionException("No exit doors found. Check that the breed exists");
        }

        AStarShortestPath<Coords, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph.getGraph(), (v1, v2) -> 0);

        Coords turtleCoords = new Coords((int)( turtle.xcor() / graph.getTurtleSize()), (int)(turtle.ycor() / graph.getTurtleSize()));

        // On calcule le plus court chemin pour chaque sortie
        GraphPath<Coords, DefaultWeightedEdge> path = Streams.of(doors.agents())
                .parallel()
                .map(agent -> {
                    // On calcule le plus court chemin pour chaque sortie
                    Turtle exit = (Turtle) agent;

                    Coords exitCoords = new Coords((int)(exit.xcor() / graph.getTurtleSize()), (int)( exit.ycor() / graph.getTurtleSize()));

                    try {
                        return aStarAlgorithm.getPath(turtleCoords, exitCoords);
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

        try {
            turtle.setVariable(15, path);
            turtle.setVariable(14, 1D);
        } catch (AgentException e) {
            throw new ExtensionException(e);
        }

        Coords startVertex = path.getVertexList().get(1);
        Coords nextPathCoordinates = new Coords(startVertex.x() * graph.getTurtleSize().intValue(), startVertex.y() * graph.getTurtleSize().intValue());

        return LogoList.fromJava(List.of((double) nextPathCoordinates.x(), (double) nextPathCoordinates.y()));
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(Syntax.ListType());
    }
}

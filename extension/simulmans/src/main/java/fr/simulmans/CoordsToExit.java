package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class CoordsToExit implements Reporter {
    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {

        if(!(context.getAgent() instanceof Turtle turtle)){
            throw new ExtensionException("Not a Turtle");
        }

//        Integer ticksSinceLastPathfinding = ((Double) turtle.getVariable(14)).intValue();

        int speed = ((Double) turtle.getVariable(16)).intValue();

//        if(ticksSinceLastPathfinding % 5 != 0){
//            GraphPath<Coords, DefaultWeightedEdge> path = (GraphPath<Coords, DefaultWeightedEdge>) turtle.getVariable(15);
//
//            Coords startVertex = path.getVertexList().get(speed * ((ticksSinceLastPathfinding % 5)));
//
//            LogoListBuilder builder = new LogoListBuilder();
//
//            try {
//                turtle.setVariable(14, (double) (ticksSinceLastPathfinding + 1));
//            } catch (AgentException e) {
//                throw new ExtensionException(e);
//            }
//
//            builder.add(startVertex.x());
//            builder.add(startVertex.y());
//
//            return builder.toLogoList();
//        }

        Graph<Coords, DefaultWeightedEdge> graph = Humans.getGraph(turtle);

        // On récupère les sorties et leurs coordonnées
        AgentSet doors = context.world().getBreed("EXIT-DOORS");

        if(doors == null){
            throw new ExtensionException("No exit doors found. Check that the breed exists");
        }

        AStarShortestPath<Coords, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph, (v1, v2) -> 0);

        Coords turtleCoords = new Coords((int) turtle.xcor(), (int) turtle.ycor());

        if(!graph.containsVertex(turtleCoords)) {
            throw new ExtensionException("Turtle is not present in graph (%d / %d)".formatted(turtleCoords.x(), turtleCoords.y()));
        }

        Set<GraphPath<Coords, DefaultWeightedEdge>> paths = new HashSet<>();

        for (Agent a : doors.agents()) {
            Turtle exit = (Turtle) a;
            Coords exitCoords = new Coords((int) exit.xcor(), (int) exit.ycor());

            if(!graph.containsVertex(turtleCoords)) {
                throw new ExtensionException("Door is not present in graph (%d / %d)".formatted(exitCoords.x(), exitCoords.y()));
            }

            paths.add(aStarAlgorithm.getPath(turtleCoords, exitCoords));
        }

        GraphPath<Coords, DefaultWeightedEdge> path = paths.stream()
                .min( Comparator.comparingInt(GraphPath::getLength))
                .orElseThrow(() -> new ExtensionException("No path found"));



        Coords startVertex = path.getVertexList().get(1);

        LogoListBuilder builder = new LogoListBuilder();

        builder.add((double) startVertex.x());
        builder.add((double) startVertex.y());

        return builder.toLogoList();
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(Syntax.ListType());
    }
}

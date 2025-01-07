package fr.simulmans;

import org.apache.commons.lang3.stream.Streams;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.*;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import java.util.*;

public class CoordsToAlarm implements Reporter {
    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {

        if (!(context.getAgent() instanceof Turtle turtle)) {
            throw new ExtensionException("Not a Turtle");
        }

        Integer speed = ((Double) turtle.getVariable(16)).intValue();

        Graph<Coords, DefaultWeightedEdge> graph = Humans.getGraph(turtle);

        // On récupère les alarmes incendies et leurs coordonnées
        AgentSet alarms = context.world().getBreed("BUTTONS");

        AStarShortestPath<Coords, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph, (v1, v2) -> 0);

        Coords turtleCoords = new Coords((int) turtle.xcor(), (int) turtle.ycor());

        if(!graph.containsVertex(turtleCoords)) {
            throw new ExtensionException("Turtle is not present in graph (%d / %d)".formatted(turtleCoords.x(), turtleCoords.y()));
        }

        Set<GraphPath<Coords, DefaultWeightedEdge>> paths = new HashSet<>();

        for (Agent a : alarms.agents()) {
            Turtle alarm = (Turtle) a;
            Coords alarmCoords = new Coords((int) alarm.xcor(), (int) alarm.ycor());

            if(!graph.containsVertex(turtleCoords)) {
                throw new ExtensionException("Alarm is not present in graph (%d / %d)".formatted(alarmCoords.x(), alarmCoords.y()));
            }

            paths.add(aStarAlgorithm.getPath(turtleCoords, alarmCoords));
        }

        // On calcule le plus court chemin pour chaque alarme
        GraphPath<Coords, DefaultWeightedEdge> path = paths.stream()
                .min(Comparator.comparingInt(GraphPath::getLength))
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

package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

public class GetNextNodeCoords implements Reporter {
    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {
        if (!(args[0].get() instanceof TurtleGraph)) {
            throw new ExtensionException("Expected a Graph object as input.");
        }

        Graph<String, DefaultWeightedEdge> graph = ((TurtleGraph) args[0].get()).getGraph();

        AStarShortestPath<String, DefaultWeightedEdge> aStarAlgorithm = new AStarShortestPath<>(graph, );

        // Find the shortest path
        GraphPath<String, DefaultWeightedEdge> path = aStarAlgorithm.getPath("A", "D");

    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(new int []{Syntax.WildcardType()}, Syntax.ListType());
    }
}

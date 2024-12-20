package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.Reporter;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

public class GetGraphNumberOfNodes implements Reporter {

    @Override
    public Object report(Argument[] args, Context context) throws ExtensionException {

        if (!(args[0].get() instanceof TurtleGraph)) {
            throw new ExtensionException("Expected a Graph object as input.");
        }

        Graph<Coords, DefaultWeightedEdge> graph = ((TurtleGraph) args[0].get()).getGraph();
        return String.valueOf(graph.vertexSet().size());
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(new int[]{Syntax.WildcardType()}, Syntax.StringType());
    }
}

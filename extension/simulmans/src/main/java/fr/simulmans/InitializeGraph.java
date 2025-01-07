package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.nlogo.core.WorldDimensions;

public class InitializeGraph implements org.nlogo.api.Command {

    private final int[][] directions = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1}   // Right
    };

    public void print(String input, Context context) {
        new Thread(() -> context.workspace().command("""
                print "%s"
                """.formatted(input)))
                .start();
    }

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException {

        if (!(context.getAgent() instanceof Turtle turtle)) {
            throw new ExtensionException("Not a Turtle");
        }

        try {
            Humans.setGraph(createGraph(context), turtle);
        } catch (AgentException e) {
            throw new ExtensionException(e);
        }
    }

    public Graph<Coords, DefaultWeightedEdge> createGraph(Context context) throws AgentException {
        WorldDimensions dimensions = context.world().getDimensions();

        // Create the graph
        Graph<Coords, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        boolean debug = (boolean) context.world().observer().getVariable(context.world().observerOwnsIndexOf("DEBUG-GRAPH")) &&         !(context.getAgent() instanceof Turtle);

        for (int i = dimensions.minPxcor(); i < dimensions.maxPxcor(); i++) {
            for (int j = dimensions.minPycor(); j < dimensions.maxPycor(); j++) {
                if (!isBlackOrGreen(context, i, j)) continue;

                Coords node = new Coords(i, j);
                graph.addVertex(node);

                if(debug){
                    Patch p = context.world().fastGetPatchAt(i, j);

                    p.setVariable(org.nlogo.agent.Patch.VAR_PCOLOR, 138D);
                }

                for (int[] direction : directions) {
                    int newRow = i + direction[0];
                    int newCol = j + direction[1];

                    if (isValidCell(newRow, newCol, dimensions) && isBlackOrGreen(context, newRow, newCol)) {
                        Coords neighbor = new Coords(newRow, newCol);
                        graph.addVertex(neighbor);

                        double weight = 1;

                        DefaultWeightedEdge edge = graph.addEdge(node, neighbor);

                        // Null = les voisins n'existent pas
                        if (edge != null) {
                            // Add edge with weight
                            graph.setEdgeWeight(edge, weight);
                        }
                    }
                }
            }
        }

        return graph;
    }

    private boolean isBlackOrGreen(Context context, int row, int col) throws AgentException {

        Patch p = context.world().getPatchAt( row,  col);

        return isBlackPixel(p) || isGreenPixel(p);
    }


    @Override
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax();
    }

    private boolean isBlackPixel(Patch p) {
        Double color = (Double) p.pcolor();

        return color % 10 == 0 ; // Black pixel
    }

    private boolean isGreenPixel(Patch p) {
        Double color = (Double) p.pcolor();

        return (color >= 51 && color <= 59) || (color >= 61 && color <= 69) ; // Black pixel
    }

    private boolean isValidCell(int row, int col, WorldDimensions dimensions) {
        return row >= dimensions.minPxcor() && col >= dimensions.minPycor() && row <= dimensions.maxPxcor() && col <= dimensions.maxPycor();
    }
}

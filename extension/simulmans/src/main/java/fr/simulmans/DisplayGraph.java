package fr.simulmans;

import com.mxgraph.swing.mxGraphComponent;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.Argument;
import org.nlogo.api.Command;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import javax.swing.*;
import java.awt.*;

public class DisplayGraph implements Command {

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException {

        if (!(args[0].get() instanceof TurtleGraph)) {
            throw new ExtensionException("Expected a Graph object as input.");
        }

        Graph<String, DefaultWeightedEdge> graph = ((TurtleGraph) args[0].get()).getGraph();
        System.out.println(graph.vertexSet().size());

        // Create a JFrame to hold the graph visualization
        JFrame frame = new JFrame("JGraphT with AWT");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        // Use JGraphXAdapter to visualize the graph
        JGraphXAdapter<String, DefaultWeightedEdge> graphAdapter = new JGraphXAdapter<>(graph);

        // Add the graph visualization to the frame
        frame.add(new mxGraphComponent(graphAdapter), BorderLayout.CENTER);

        // Display the frame
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[]{Syntax.WildcardType()});
    }
}

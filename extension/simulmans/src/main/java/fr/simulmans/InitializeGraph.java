package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import java.awt.*;
import java.awt.image.BufferedImage;

public class InitializeGraph implements org.nlogo.api.Command {

    private final int[][] directions = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1}   // Right
    };

    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException {
        BufferedImage playfield = context.getDrawing();

        if(!(context.getAgent() instanceof Turtle turtle)){
            throw new ExtensionException("Not a Turtle");
        }

        TurtleGraph graph = new TurtleGraph();

        graph.setTurtleSize(turtle.size());

        graph.setGraph(createGraph(playfield, (int) turtle.size()));

        try {
            turtle.setVariable(0, graph);
        } catch (AgentException e) {
            throw new RuntimeException(e);
        }
    }

    public Graph<Coords, DefaultWeightedEdge> createGraph(BufferedImage image, int scaleFactor) {
        BufferedImage downscaledImage = downscaleImage(image, scaleFactor);
        int rows = downscaledImage.getHeight();
        int cols = downscaledImage.getWidth();

        // Create the graph
        Graph<Coords, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (!isBlackOrGreen(downscaledImage, i, j)) continue;

                Coords node = new Coords(i, j);
                graph.addVertex(node);

                for (int[] direction : directions) {
                    int newRow = i + direction[0];
                    int newCol = j + direction[1];

                    if (isValidCell(newRow, newCol, rows, cols) && isBlackOrGreen(downscaledImage, newRow, newCol)) {
                        Coords neighbor = new Coords(newRow, newCol);
                        graph.addVertex(neighbor);
                        
                        double weight = 1;

                        DefaultWeightedEdge edge = graph.addEdge(node, neighbor);

                        // Null = les voisins n'existent pas
                        if(edge != null) {
                            // Add edge with weight
                            graph.setEdgeWeight(edge, weight);
                        }
                    }
                }
            }
        }

        return graph;
    }

    private boolean isBlackOrGreen(BufferedImage image, int row, int col) {
        int rgb = image.getRGB(col, row);
        return isBlackPixel(rgb) || isGreenPixel(rgb);
    }


    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(
                new int[]{Syntax.NumberType()},
                Syntax.WildcardType());
    }

    private boolean isBlackPixel(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red == 0 && green == 0 && blue == 0; // Black pixel
    }

    private boolean isGreenPixel(int rgb) {
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        return red == 35 && green == 171 && blue == 47; // Green pixel
    }

    private boolean isValidCell(int row, int col, int rows, int cols) {
        return row >= 0 && col >= 0 && row < rows && col < cols;
    }

    private BufferedImage downscaleImage(BufferedImage image, int scaleFactor) {
        int newWidth = image.getWidth() / scaleFactor;
        int newHeight = image.getHeight() / scaleFactor;
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, newWidth, newHeight, null);
        g.dispose();

        return resized;
    }
}

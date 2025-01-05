package fr.simulmans;

import org.nlogo.api.*;
import org.nlogo.core.Breed;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import scala.collection.JavaConverters;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Entry extends DefaultClassManager {

    @Override
    public void runOnce(ExtensionManager em) throws ExtensionException {

        Logger logger = Logger.getLogger("fr.simulmans");

        super.runOnce(em);

        logger.info("Coucou l'extension");


    }

    @Override
    public void load(PrimitiveManager primManager) throws ExtensionException {
        primManager.addPrimitive("initializeGraph", new InitializeGraph());
        primManager.addPrimitive("getGraphNumberOfNodes", new GetGraphNumberOfNodes());
        primManager.addPrimitive("getCoordsToAlarm", new CoordsToAlarm());
        primManager.addPrimitive("getCoordsToExit", new CoordsToExit());
        primManager.addPrimitive("breeds", new Breeds());
        primManager.addPrimitive("getGraph", new DisplayGraph());
        primManager.addPrimitive("getSpawnableCoords", new SpawnableAreaCoords());
        primManager.addPrimitive("getRandomSpawnableCoords", new SpawnableCoords());
        primManager.addPrimitive("debugGraph", new DebugAgents());
    }

    public static class Breeds implements Reporter {
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(
                    // we take in int[] {modelNumber, varName}
                    // and return a number
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, org.nlogo.api.LogoException {
            LogoListBuilder llb = new LogoListBuilder();
            // add turtle vars as a separate tuple


            for (Breed breed : JavaConverters.asJavaIterable(context.workspace().world().program().breeds().values()))
            {
                llb.add(breed.name());
            }
            return llb.toLogoList();

        }
    }

    public static class DisplayGraph implements Reporter {
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(
                    // we take in int[] {modelNumber, varName}
                    // and return a number
                    Syntax.ListType());
        }

        public Object report(Argument args[], Context context)
                throws ExtensionException, org.nlogo.api.LogoException {
            LogoListBuilder llb = new LogoListBuilder();

            // add turtle vars as a separate tuple

            if(!(context.getAgent() instanceof Turtle turtle)){
                throw new ExtensionException("Not a Turtle");
            }

            TurtleGraph graph = (TurtleGraph) turtle.getVariable(13);

            for(Coords coords : graph.getGraph().vertexSet()) {
                llb.add("\n%s %s".formatted(coords.x(), coords.y()));
            }

            return llb.toLogoList();

        }
    }

    public static class DebugAgents implements Command {
        public Syntax getSyntax() {
            return SyntaxJ.commandSyntax();
        }

        public void perform(Argument args[], Context context)
                throws ExtensionException, org.nlogo.api.LogoException {

            InitializeGraph init = new InitializeGraph();

            org.jgrapht.Graph<Coords, org.jgrapht.graph.DefaultWeightedEdge> graph;

            try {
                graph = init.createGraph(context, 1);
            } catch (AgentException e) {
                throw new ExtensionException(e);
            }

            for(Agent a : context.world().turtles().agents()){
                Turtle turtle = (Turtle) a;

                if(graph.containsVertex(new Coords(turtle.xcor(), turtle.ycor()))){
                    try {
                        turtle.setVariable(org.nlogo.agent.Turtle.VAR_COLOR, 97D);
                    } catch (AgentException e) {
                        throw new ExtensionException(e);
                    }
                }
            }

        }
    }

}

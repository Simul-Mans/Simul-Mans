package fr.simulmans;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

public class RegisterSmoke implements Command {
    @Override
    public void perform(Argument[] args, Context context) throws ExtensionException {

        org.nlogo.agent.World world = (org.nlogo.agent.World) context.world();

        final int smokeMalus = 6;

        // On récupère l'id de l'humain concerné
        long turtleId = ((Double) args[0].get()).longValue();
        // On récupère le patch du contexte
        if (!(context.getAgent() instanceof Patch patch)) {
            throw new ExtensionException("Not a Turtle");
        }

        // On récupère la turtle conernée
        Turtle t = world.getTurtle(turtleId);

        // On récupère le graphe de la turtle
        Graph<Coords, DefaultWeightedEdge> graph = Humans.getGraph(t);

        // On récupère la variable smoke-level
        int variableIndex = world.patchesOwnIndexOf("SMOKE-LEVEL");

        double smokeLevel = (double) patch.getVariable(variableIndex);

        // On récupère les coordonnées du patch
        Coords patchCoords = new Coords(patch.pxcor(), patch.pycor());

        // On met à jour le graphe de ta turtle (On pénalise les chemin voisins au patch concerné)
        for(DefaultWeightedEdge edge : graph.incomingEdgesOf(patchCoords)) {
            graph.setEdgeWeight(edge, smokeLevel * smokeMalus);
        }
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.commandSyntax(new int[] {Syntax.NumberType()});
    }
}

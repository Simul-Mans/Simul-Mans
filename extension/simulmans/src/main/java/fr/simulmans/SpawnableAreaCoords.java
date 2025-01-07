package fr.simulmans;

import org.apache.commons.lang3.stream.Streams;
import org.nlogo.api.*;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import java.util.List;

public class SpawnableAreaCoords implements Reporter {
    @Override
    public List<Coords> report(Argument[] args, Context context) throws ExtensionException {

        return Streams.of(context.world().patches().agents())
                .parallel()
                .map(p -> (Patch) p)
                .filter(p -> ((Double) p.pcolor()) % 10 == 0)
                .map(p -> new Coords(p.pxcor(), p.pycor()))
                .toList();
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(Syntax.WildcardType());
    }
}

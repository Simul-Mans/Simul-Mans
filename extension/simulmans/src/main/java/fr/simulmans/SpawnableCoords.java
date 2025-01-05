package fr.simulmans;

import org.nlogo.api.*;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;

import java.util.List;

public class SpawnableCoords implements Reporter {

    @Override
    public LogoList report(Argument[] args, Context context) throws ExtensionException {

        if (!(args[0].get() instanceof List<?> coords)) {
            throw new ExtensionException("Expected a Set of coords.");
        }

        int randomIndex = context.workspace().mainRNG().nextInt(coords.size());

        Coords randomValue = (Coords) coords.get(randomIndex);

        LogoListBuilder builder = new LogoListBuilder();

        builder.add(randomValue.x());
        builder.add(randomValue.y());

        return builder.toLogoList();
    }

    @Override
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(
                new int[]{Syntax.WildcardType()},
                Syntax.ListType());
    }
}

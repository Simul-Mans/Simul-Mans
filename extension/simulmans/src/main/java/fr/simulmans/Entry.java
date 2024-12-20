package fr.simulmans;

import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.ExtensionManager;
import org.nlogo.api.PrimitiveManager;

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
    }
}

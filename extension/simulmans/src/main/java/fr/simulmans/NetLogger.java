package fr.simulmans;

import org.nlogo.log.LogManager$;
import scala.collection.JavaConverters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetLogger extends Logger {

    private final Method logMethod;

    public NetLogger(String name) {
        super(name, null);

        Method method = null;
        try {
            method = LogManager$.class.getDeclaredMethod("log", String.class, scala.collection.immutable.Map.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        // Make the method accessible
        method.setAccessible(true);

        logMethod = method;
    }

    @Override
    public void log(Level level, String msg) {
        try {
            logMethod.invoke("Simulmans-ext", JavaConverters.mapAsScalaMap(Map.of("message", msg)));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Logger getLogger(String name) {
        return new NetLogger(name);
    }
}

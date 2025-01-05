package fr.simulmans;

import org.nlogo.api.Argument;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;

public interface TypedArgument<T extends Object> extends Argument {

    T get() throws ExtensionException, LogoException;
}

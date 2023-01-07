package net.krlite.pierced.core;

public interface Convertable<C> {
	String convertToString();
	C convertFromString(String value);
}

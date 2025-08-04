package de.instinct.eqlibgdxutils.generic.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import de.instinct.eqlibgdxutils.generic.cache.model.LoadSequence;

public class Cache<E> {
	
	private Map<String, E> loadedElements;
	private LoadSequence<E> loadSequence;
	
	public Cache(LoadSequence<E> loadSequence) {
		this.loadSequence = loadSequence;
		loadedElements = new HashMap<>();
	}
	
	public E get(String tag) {
		E loadedElement = loadedElements.get(tag);
		if (loadedElement != null) return loadedElement;
		return load(tag);
	}

	private E load(String tag) {
		E loadedElement = loadSequence.execute(tag);
		if (loadedElement != null) loadedElements.put(tag, loadedElement);
		return loadedElement;
	}
	
	public Collection<E> getAllLoadedElements() {
		return loadedElements.values();
	}
	
}

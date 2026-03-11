package de.instinct.eqfleet.menu.common.synchronizer;

import de.instinct.eqfleet.menu.common.synchronizer.model.Comparator;
import de.instinct.eqfleet.menu.common.synchronizer.model.Generator;
import de.instinct.eqfleet.menu.common.synchronizer.model.Disposer;
import de.instinct.eqfleet.menu.common.synchronizer.model.Retainer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SynchronizationConfiguration<T extends Object, E extends Object> {
	
	//Required
	private Comparator<T, E> comparator;
	private Generator<T, E> generator;
	
	//Optional
	private Retainer<T, E> retainer;
	private Disposer<E> disposer;

}

package de.instinct.eqfleet.menu.common.synchronizer;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListSynchronizer<T extends Object, E extends Object> {
	
	private SynchronizationConfiguration<T, E> synchronizationConfiguration;
	
	private final List<E> matchedElements = new ArrayList<>();
	
	public void update(List<T> baseList, List<E> targetList) {
		if (baseList == null || baseList.isEmpty()) {
			if (targetList != null && !targetList.isEmpty()) targetList.clear();
			return;
		}
		
		matchedElements.clear();
		
		for (T baseListElement : baseList) {
			E existingSynchronizedElement = null;
			for (E synchronizedElement : targetList) {
		        if (synchronizationConfiguration.getComparator().equals(baseListElement, synchronizedElement)) {
		            existingSynchronizedElement = synchronizedElement;
		            break;
		        }
		    }

	        if (existingSynchronizedElement != null) {
	        	matchedElements.add(existingSynchronizedElement);
	        	if (synchronizationConfiguration.getRetainer() != null) synchronizationConfiguration.getRetainer().update(baseListElement, existingSynchronizedElement);
	        } else {
	        	E newTargetElement = synchronizationConfiguration.getGenerator().generate(baseListElement);
	        	if (newTargetElement != null) matchedElements.add(newTargetElement);
	        }
		}

	    for (E oldSynchronizedListElement : targetList) {
	        if (!matchedElements.contains(oldSynchronizedListElement)) {
	        	if (synchronizationConfiguration.getDisposer() != null) synchronizationConfiguration.getDisposer().execute(oldSynchronizedListElement);
	        }
	    }
	    targetList.clear();
	    targetList.addAll(matchedElements);
	}

}

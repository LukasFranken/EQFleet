package de.instinct.eqlibgdxutils.net;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueue<T> {
	
	private ConcurrentLinkedQueue<T> messageQueue;
	
	public MessageQueue() {
		messageQueue = new ConcurrentLinkedQueue<>();
    }
    
    public void add(T newElement) {
    	messageQueue.offer(newElement);
    }
    
    public T next() {
    	return messageQueue.poll();
    }
    
    public T peek() {
    	return messageQueue.peek();
    }

}

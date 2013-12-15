package com.audax.dev.forte;

import android.os.Handler;

public class RunnableTrain implements Runnable {
	private RunnableFunction method;
	private RunnableTrain next;
	private int interval;
	private Object argument;
	
	
	
	public RunnableTrain() {
		
	}

	public interface RunnableFunction {
		void run(Object argument, RunnableTrain train);
	}
	
	
	private RunnableTrain(RunnableFunction method) {
		this.method = method;
	}
	
	
	public Object getArgument() {
		return argument;
	}

	public void setArgument(Object argument) {
		this.argument = argument;
	}	

	public static RunnableTrain startFrom(RunnableFunction method) {
		RunnableTrain t = new RunnableTrain(method);
		return t;
	}
	
	public RunnableTrain before(RunnableFunction run, int interval) {
		RunnableTrain t = new RunnableTrain(run);
		this.interval = interval;
		this.next = t;
		return t;
	}
	
	public void notifyCompleted() {
		this.callNext();
	}

	@Override
	public final void run() {
		this.method.run(this.getArgument(), this);
	}
	
	private void callNext() {
		if (this.next != null) {
			Handler h = new Handler();
			if (this.interval > 0) {
				h.postDelayed(this.next, this.interval);
			}else {
				h.post(this.next);
			}
			
		}
	}

}

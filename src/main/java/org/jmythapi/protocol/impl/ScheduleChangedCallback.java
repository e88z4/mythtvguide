package org.jmythapi.protocol.impl;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jmythapi.protocol.IBackend;
import org.jmythapi.protocol.events.IMythEventListener;
import org.jmythapi.protocol.events.IScheduleChange;

public class ScheduleChangedCallback implements IMythEventListener<IScheduleChange>, Future<Boolean> {
    final Semaphore reschedulingDone = new Semaphore(0);

    IBackend backend;
    
    boolean cancelled = false;
    
    boolean failed = false;
    
    public ScheduleChangedCallback(IBackend backend) {
    	this.backend = backend;
	}
    
    private void unregisterListener() {
    	this.backend.removeEventListener(IScheduleChange.class,this);
    }
    
    public void setFailed() {
		this.failed = true;
		
        // unregister listener
        this.unregisterListener();
	}
    
    public void fireEvent(IScheduleChange event) {
    	if(event == null) return;
    	
        // signal received event
        reschedulingDone.release();
        
        // unregister listener
        this.unregisterListener();
    }

    public boolean isCancelled() {
        return this.cancelled;
    }    
    
    public boolean cancel(boolean mayInterruptIfRunning) {
    	if(this.cancelled) return false;    	
    	else if(reschedulingDone.availablePermits() > 0) return false;
    	
    	this.unregisterListener();    	
        this.cancelled =  true;
        return cancelled;
    }

    public Boolean get() throws InterruptedException, ExecutionException {
    	if(this.failed) return Boolean.FALSE;
    	else if(this.cancelled) throw new CancellationException();
    	
    	reschedulingDone.acquire();
    	reschedulingDone.release();
    	return Boolean.TRUE;
    }

    public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    	if(this.failed) return Boolean.FALSE;
    	else if(this.cancelled) throw new CancellationException();
    	
        final boolean success = reschedulingDone.tryAcquire(timeout,unit);
        if(!success) throw new TimeoutException(); 
        	
        reschedulingDone.release();
        return success;
    }

    public boolean isDone() {
    	if(this.failed) return true;
    	else if(this.cancelled) return true;
        return reschedulingDone.availablePermits() > 0;
    }

    @Override
    protected void finalize() throws Throwable {
    	this.unregisterListener();
    }
} 
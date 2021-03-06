/*
 * Copyright 2014 Nastel Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nastel.jkool.tnt4j.sink;

import com.nastel.jkool.tnt4j.core.KeyValueStats;
import com.nastel.jkool.tnt4j.core.OpLevel;
import com.nastel.jkool.tnt4j.core.Snapshot;
import com.nastel.jkool.tnt4j.format.EventFormatter;
import com.nastel.jkool.tnt4j.source.Source;
import com.nastel.jkool.tnt4j.tracker.TrackingActivity;
import com.nastel.jkool.tnt4j.tracker.TrackingEvent;

/**
 * <p>Classes that implement this interface provide implementation for 
 * the <code>EventSink</code>, which provides an interface for underlying logging framework
 * storage destination. All event sink implementations should be wrapped with this interface.
 * </p>
 *
 *
 * @see OpLevel
 * @see Source
 * @see TrackingEvent
 * @see TrackingActivity
 *
 * @version $Revision: 7 $
 *
 */
public interface EventSink extends Sink, KeyValueStats {
	static final String KEY_SINK_ERROR_COUNT = "sink-errors";
	static final String KEY_LOGGED_MSGS = "sink-messages";
	static final String KEY_LOGGED_EVENTS = "sink-events";
	static final String KEY_LOGGED_ACTIVITIES = "sink-activities";
	static final String KEY_LOGGED_SNAPSHOTS = "sink-snapshots";
	static final String KEY_SKIPPED_COUNT = "sink-skipped";

	/**
	 * Set current/active <code>Source</code> with the sink
	 *
	 * @param src event source handle
	 * @see Source
	 */
	void setSource(Source src);
	
	/**
	 * Obtains current/active <code>Source</code> handle associated
	 * with the current sink.
	 *
	 * @return current source handle
	 * @see Source
	 */
	Source getSource();

	/**
	 * Obtain name associated with this event sink instance
	 *
	 * @return name associated with the event sink instance
	 */
	String getName();
	
	/**
	 * Obtain event formatter instance associated with this sink
	 *
	 * @return event formatter instance
	 */
	EventFormatter getEventFormatter();
	
	/**
	 * Check of a certain log level is set/enabled
	 * 
	 * @param sev severity level
	 * @see OpLevel
	 */
	public boolean isSet(OpLevel sev);

	/**
	 * Log a given tracking event
	 *
	 * @param event tracking event to log
	 * @see TrackingEvent
	 */
	public void log(TrackingEvent event);

	/**
	 * This method allows writing of <code>TrackingActivity</code> objects
	 * to the underlying destination.
	 * 
	 * @param activity to be sent to the sink
	 * @see TrackingActivity
	 */
	public void log(TrackingActivity activity);

	/**
	 * This method allows writing of <code>Snapshot<Property></code> objects
	 * to the underlying destination.
	 * 
	 * @param snapshot a set of properties
	 * @see Snapshot
	 */
	public void log(Snapshot snapshot);

	/**
	 * Log a given string message with a specified severity
	 *
	 * @param sev message severity to log
	 * @param msg string message to be logged
	 * @param args arguments passed along the message
	 * @see OpLevel
	 */
	public void log(OpLevel sev, String msg, Object...args);
		
	/**
	 * Log a given string message with a specified severity
	 *
	 * @param src log message source info
	 * @param sev message severity to log
	 * @param msg string message to be logged
	 * @param args arguments passed along the message
	 * @see OpLevel
	 */
	public void log(Source src, OpLevel sev, String msg, Object...args);
		
	/**
	 * Register an event sink listener for notifications when errors 
	 * occur when writing to event sink. 
	 * 
	 * @see SinkErrorListener
	 */
	public void addSinkErrorListener(SinkErrorListener listener);
	
	/**
	 * Remove an event sink listener for notifications when errors 
	 * occur when writing to event sink. 
	 * 
	 * @see SinkErrorListener
	 */
	public void removeSinkErrorListener(SinkErrorListener listener);
	
	/**
	 * Register an event sink listener for notifications when logging events
	 * occur when writing to event sink. 
	 * 
	 * @see SinkLogEventListener
	 */
	public void addSinkLogEventListener(SinkLogEventListener listener);
	
	/**
	 * Remove an event sink listener for notifications when logging events 
	 * occur when writing to event sink. 
	 * 
	 * @see SinkLogEventListener
	 */
	public void removeSinkLogEventListener(SinkLogEventListener listener);
	
	/**
	 * Register an event sink filter to selectively filter out certain events.
	 * 
	 * @see SinkEventFilter
	 */
	public void addSinkEventFilter(SinkEventFilter listener);
	
	/**
	 * Remove an event sink filter.
	 * 
	 * @see SinkEventFilter
	 */
	public void removeSinkEventFilter(SinkEventFilter listener);
	
}

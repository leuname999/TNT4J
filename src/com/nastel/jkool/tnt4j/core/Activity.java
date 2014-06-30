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
package com.nastel.jkool.tnt4j.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nastel.jkool.tnt4j.source.Source;
import com.nastel.jkool.tnt4j.utils.Utils;

/**
 * <p>Implements a collection of related event and sub-activities.</p>
 *
 * <p>Represents a collection of linked items <code>LinkedItem</code>
 * that should be considered to be a single related unit. These are generally
 * delimited by BEGIN/END (or BEGIN/EXCEPTION)calls.
 * </p>
 *
 * <p>A <code>Activity</code> is required to have its start time set,
 * and to have it's end time set if its status is not <code>ActivityStatus.BEGIN</code>.</p>
 *
 * @see ActivityStatus
 * @see ActivityListener
 * @see Message
 * @see Operation
 * @see Property
 * @see Trackable
 *
 * @version $Revision: 11 $
 */
public class Activity extends Operation implements Trackable {

	/**
	 * Maximum length of an Activity Signature.
	 * @since Revision 19
	 */
	public static final int MAX_SIGNATURE_LENGTH = 36;

	/**
	 * Maximum length of Activity Exception string.
	 * @since Revision 38
	 */
	public static final int MAX_EXCEPTION_LENGTH = 512;

	private Source appl;
	private String tracking_id;
	private String parentId;
	private ActivityStatus status = ActivityStatus.BEGIN;

	private HashSet<String> linkedItems = new HashSet<String>(89);
	private HashSet<String> correlators = new HashSet<String>(89);
	private ArrayList<PropertySnapshot> snapshots =  new ArrayList<PropertySnapshot>(32);
	private ArrayList<ActivityListener> activityListeners = null;

	/**
	 * Creates a Activity object with the specified tracking id.
	 *
	 * @param id Activity tracking id
	 * @throws NullPointerException if the tracking id is <code>null</code>
	 * @throws IllegalArgumentException if the tracking id is empty or is too long
	 * @see #setTrackingId(String)
	 */
	public Activity(String id) {
		super(Operation.NOOP, OpType.ACTIVITY);
		setTrackingId(id);
		setTID(Thread.currentThread().getId());
		setSource(appl);
		setResource(Utils.getVMName());
	}

	/**
	 * Creates a Activity object with the specified tracking id.
	 *
	 * @param appl application handle associated with this activity
	 * @param id Activity tracking id
	 * @throws NullPointerException if the tracking id is <code>null</code>
	 * @throws IllegalArgumentException if the tracking id is empty or is too long
	 * @see #setTrackingId(String)
	 * @see #setSource(Source)
	 */
	public Activity(String id, Source appl) {
		super(appl.getName(), OpType.ACTIVITY);
		setTrackingId(id);
		setTID(Thread.currentThread().getId());
		setSource(appl);
		setResource(Utils.getVMName());
	}

	/**
	 * Creates a Activity object with the specified tracking id.
	 *
	 * @param id Activity tracking id
	 * @param name assign activity name
	 * @param appl application handle associated with this activity
	 * @throws NullPointerException if the tracking id is <code>null</code>
	 * @throws IllegalArgumentException if the tracking id is empty or is too long
	 * @see #setTrackingId(String)
	 * @see #setSource(Source)
	 */
	public Activity(String id, String name, Source appl) {
		super(name, OpType.ACTIVITY);
		setTrackingId(id);
		setTID(Thread.currentThread().getId());
		setSource(appl);
		setResource(Utils.getVMName());
	}

	/**
	 * Register an activity listener for notifications when activity timing
	 * events occur.
	 *
	 * @see ActivityListener
	 */
	public void addActivityListener(ActivityListener listener) {
		if (activityListeners == null) {
			activityListeners = new ArrayList<ActivityListener>(10);
		}
		activityListeners.add(listener);
	}

	/**
	 * Remove an activity listener for notifications when activity timing
	 * events occur.
	 *
	 * @see ActivityListener
	 */
	public void removeActivityListener(ActivityListener listener) {
		if (activityListeners != null) {
			activityListeners.remove(listener);
		}
	}

	/**
	 * Subclasses should use this helper class to trigger start notifications
	 *
	 * @see ActivityListener
	 */
	protected void notifyStarted() {
		if (activityListeners == null) return;
		for (ActivityListener listener: activityListeners) {
			listener.started(this);
		}
	}

	/**
	 * Subclasses should use this helper class to trigger stop notifications
	 *
	 * @see ActivityListener
	 */
	protected void notifyStopped() {
		if (activityListeners == null) return;
		for (ActivityListener listener: activityListeners) {
			listener.stopped(this);
		}
	}

	@Override
	public void start(long startTime, long startTimeUsec) {
		super.start(startTime, startTimeUsec);
		notifyStarted();
	}

	@Override
	public void start(UsecTimestamp startTime) {
		super.start(startTime);
		notifyStarted();
	}

	@Override
	public void stop(long stopTime, long stopTimeUsec) {
		super.stop(stopTime, stopTimeUsec);
		notifyStopped();
	}

	@Override
	public void stop(UsecTimestamp stopTime) {
		super.stop(stopTime);
		notifyStopped();
	}

	/**
	 * Set current/active <code>Source</code> with the current activity
	 *
	 * @see Source
	 */
	public void setSource(Source source) {
		appl = source;
	}

	@Override
	public Source getSource() {
		return appl;
	}

	/**
	 * Gets the Activity tracking id, which is the unique identifier for the Activity.
	 *
	 * @return Activity tracking id
	 */
	public String getTrackingId() {
		return tracking_id;
	}

	/**
	 * Sets the Activity tracking id, which is the unique identifier for the Activity.
	 * Could be any string that will uniquely identify this Activity.
	 *
	 * @param id Activity tracking id
	 * @throws NullPointerException if tracking id is <code>null</code>
	 * @throws IllegalArgumentException if tracking id is empty or too long
	 * @see #MAX_SIGNATURE_LENGTH
	 */
	public void setTrackingId(String id) {
		if (id == null)
			throw new NullPointerException("tracking id must be a non-empty string");
		if (id.length() == 0)
			throw new IllegalArgumentException("tracking id must be a non-empty string");
		if (id.length() > MAX_SIGNATURE_LENGTH)
			throw new IllegalArgumentException("tracking id length must be <= " + MAX_SIGNATURE_LENGTH);
		this.tracking_id = id;
	}


	/**
	 * Gets the Activity status.
	 *
	 * @return Activity status
	 */
	public ActivityStatus getStatus() {
		return status;
	}

	/**
	 * Sets the Activity status.
	 *
	 * @param status Activity status
	 * @throws NullPointerException if status is <code>null</code>y
	 */
	public void setStatus(ActivityStatus status) {
		if (status == null)
			throw new NullPointerException("status must be a non-null");
		this.status = status;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentId(Trackable parentObject) {
		this.parentId = parentObject.getTrackingId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParentId() {
		return parentId;
	}

	/**
	 * Adds the specified linked item to the list of items referenced in this Activity.
	 * This method does NOT check for duplicates.
	 *
	 * @param item linked item referenced in Activity
	 * @throws NullPointerException if item is <code>null</code>
	 * @see #containsId(String)
	 * @see #containsCid(String)
	 */
	public void add(Trackable item) {
		if (item == null)
			throw new NullPointerException("msg must be non-null");

		linkedItems.add(item.getTrackingId());
		if (item.getCorrelator() != null) {
			correlators.add(item.getCorrelator());
		}
		item.setParentId(this);
	}

	/**
	 * Checks whether the specified tracking id has been added to the list of
	 * items referenced in this Activity.
	 *
	 * @param id linked item to test for
	 * @return <code>true</code> if the Activity contains specified item,
	 *         <code>false</code> otherwise
	 */
	public boolean containsId(String id) {
		if (linkedItems == null || id == null)
			return false;

		return linkedItems.contains(id);
	}

	/**
	 * Checks whether the specified correlator has been added to the list of
	 * correlators referenced in this Activity.
	 *
	 * @param cid correlator id
	 * @return <code>true</code> if the Activity contains specified correlator,
	 *         <code>false</code> otherwise
	 */
	public boolean containsCid(String cid) {
		if (correlators == null || cid == null)
			return false;

		return correlators.contains(cid);
	}

	/**
	 * Gets the list of tracking ids referenced in this Activity.
	 *
	 * @return list of tracking ids
	 */
	public Set<String> getIds() {
		return linkedItems;
	}

	/**
	 * Gets the list of tracking correlators referenced in this Activity.
	 *
	 * @return list of tracking correlators
	 */
	public Set<String> getCids() {
		return correlators;
	}

	/**
	 * Gets the number of linked items referenced in this Activity.
	 *
	 * @return number of linked items
	 */
	public int getIdCount() {
		return linkedItems != null ? linkedItems.size() : 0;
	}


	/**
	 * Gets the number of correlator items referenced in this Activity.
	 *
	 * @return number of correlator items
	 */
	public int getCidCount() {
		return correlators != null ? correlators.size() : 0;
	}


	/**
	 * Adds the specified snapshot to the list of snapshots for this Activity.
	 * This method does NOT check for duplicates.
	 *
	 * @param snapshot Activity snapshot
	 * @throws NullPointerException if snapshot is <code>null</code>
	 * @see #contains(PropertySnapshot)
	 */
	public void add(PropertySnapshot snapshot) {
		if (snapshot == null)
			throw new NullPointerException("snapshot must be non-null");
		snapshots.add(snapshot);
	}

	/**
	 * Checks whether the specified snapshot has been added to the list of
	 * snapshots for this Activity.
	 *
	 * @param snapshot snapshot to test for
	 * @return <code>true</code> if the Activity contains specified snapshot,
	 *         <code>false</code> otherwise
	 */
	public boolean contains(PropertySnapshot snapshot) {
		if (snapshot == null || snapshots == null)
			return false;

		return snapshots.contains(snapshot);
	}

	/**
	 * Removes the specified snapshot from the list of snapshots for this Activity.
	 *
	 * @param snapshot snapshot to remove
	 */
	public void remove(PropertySnapshot snapshot) {
		if (snapshots != null && snapshot != null) {
			snapshots.remove(snapshot);
		}
	}

	/**
	 * Gets the list of snapshots for this Activity.
	 *
	 * @return list of Activity snapshots
	 */
	public List<PropertySnapshot> getSnapshots() {
		return snapshots;
	}

	/**
	 * Gets the number of snapshots for this Activity.
	 *
	 * @return number of snapshots
	 */
	public int getSnapshotCount() {
		return snapshots != null ? snapshots.size() : 0;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 31 + ((tracking_id == null) ? 0 : tracking_id.hashCode());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Activity))
			return false;

		final Activity other = (Activity) obj;

		if (tracking_id == null) {
			if (other.tracking_id != null)
				return false;
		}
		else if (!tracking_id.equals(other.tracking_id)) {
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final OpType sType = getType();
		final ActivityStatus Status = getStatus();
		final UsecTimestamp sTime = getStartTime();
		final UsecTimestamp eTime = getEndTime();
		StringBuilder str = new StringBuilder();

		str.append(getClass().getSimpleName()).append("{")
			.append("Name:").append(getName()).append(",")
			.append("ParentId:").append(parentId != null? parentId: "root").append(",")
			.append("TrackId:").append(getTrackingId()).append(",")
			.append("Status:").append(Status == null ? "null" : Status.toString()).append(",")
			.append("Type:").append(sType == null ? "null" : sType.toString()).append(",")
			.append("PID:").append(getPID()).append(",")
			.append("TID:").append(getTID()).append(",")
		    .append("ElapsedUsec:").append(getElapsedTime()).append(",")
		    .append("FQName:").append(getSource().getFQName()).append(",")
			.append("IdCount=").append(getIdCount()).append(",")
			.append("CidCount=").append(getCidCount()).append(",")
			.append("SnapCount=").append(getSnapshotCount()).append(",")
			.append("StartTime:[").append(sTime == null ? "null" : sTime.toString()).append("],")
			.append("EndTime:[").append(eTime == null ? "null" : eTime.toString()).append("]}");

		return str.toString();
	}
}

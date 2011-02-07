package org.gradle.eclipse.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gradle.foundation.TaskView;

/**
 * @author François Rey
 *
 */
public abstract class AbstractGradleTaskModelElement {
	protected List<TaskView> tasks;
	public static final Object[] EMPTY_ARRAY= new Object[0];
	public AbstractGradleTaskModelElement(List<TaskView> tasks) {
		super();
		setTasks(tasks);
	}
    /**
     * Sets the tasks contained by this element.
     * 
     * @param tasks the array of tasks
     */ 
	protected void setTasks(List<TaskView> tasks) {
		this.tasks = new ArrayList<TaskView>(tasks);
		Collections.sort(this.tasks);
	}
    /**
     * Returns the tasks contained by this element.
     * 
     * @return the array of tasks contained by this element
     */ 
	public List<TaskView> getTasks() {
		return tasks;
	}
    /**
     * Returns the contained elements to display in a viewer. 
     * These elements can be presented as rows in a table, items in a list, etc.
     * 
     * @return the array of elements to display in a viewer
     */ 
	public abstract Object[] getElements();
    /**
     * Returns whether this element contains any task or group.
     * @return <code>true</code> if this element contains any task or group,
     *  and <code>false</code> otherwise
     */
    public abstract boolean hasChildren();
    /**
     * Returns whether this element contains a given task.
     * @return <code>true</code> if this element contains the given task,
     *  and <code>false</code> otherwise
     */
    public boolean containsTask(Object task) {
        return tasks.contains(task);
    }
    /**
     * Clears all contained elements.
     */
	public void clear() {
		tasks.clear();
	}
}

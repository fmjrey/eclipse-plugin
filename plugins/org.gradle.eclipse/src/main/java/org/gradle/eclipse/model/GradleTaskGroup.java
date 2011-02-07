package org.gradle.eclipse.model;

import java.util.ArrayList;

import org.gradle.foundation.TaskView;

/**
 * @author François Rey
 *
 */
public class GradleTaskGroup extends AbstractGradleTaskModelElement{
	private String name;

	public GradleTaskGroup(TaskView task) {
		super(new ArrayList<TaskView>());
		String[] splittedName = task.getName().split(":");
		this.name = splittedName[splittedName.length - 1];
		addTask(task);
	}
	public String getName() {
		return name;
	}
	public void addTask(TaskView task) {
		tasks.add(task);
	}
    /**
     * Returns the elements to display in a viewer. 
     * These elements can be presented as rows in a table, items in a list, etc.
     * 
     * @return the array of elements to display in a viewer
     */ 
	public Object[] getElements() {
		if (tasks==null || tasks.size() == 0) return EMPTY_ARRAY;
		return tasks.toArray();
	}
    /**
     * Returns whether this group contains any task.
     * @return <code>true</code> if this group contains any task,
     *  and <code>false</code> otherwise
     */
    @Override
    public boolean hasChildren() {
    	return tasks!=null && tasks.size() > 0;
    }
}

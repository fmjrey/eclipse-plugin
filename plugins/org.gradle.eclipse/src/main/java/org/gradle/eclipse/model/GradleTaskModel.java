package org.gradle.eclipse.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.gradle.foundation.ProjectView;
import org.gradle.foundation.TaskView;

/**
 * Model classes for tasks of a single project.
 * Subproject tasks are also included and spread within {@link GradleTaskGroup}
 * instances, each containing subproject tasks with the name.
 * @see GradleTaskGroup
 * @see org.gradle.eclipse.launchConfigurations.GradleTasksTab
 * @author François Rey
 */
public class GradleTaskModel extends AbstractGradleTaskModelElement {
	ProjectView rootProject = null;
	List<TaskView> defaultTasks = new ArrayList<TaskView>();
	Map<String,GradleTaskGroup> groups = new TreeMap<String,GradleTaskGroup>();
	public GradleTaskModel() {
		super(new ArrayList<TaskView>());
		this.rootProject = null;
	}
	public GradleTaskModel(ProjectView rootProject) {
		super(rootProject.getTasks());
		setRootProject(rootProject);
	}
	public String getName() {
		return rootProject.getName();
	}
	public List<TaskView> getDefaultTasks() {
		return defaultTasks;
	}
	public void setDefaultTasks(List<TaskView> defaultTasks) {
		this.defaultTasks = defaultTasks;
	}
	public boolean hasDefaultTasks() {
		return defaultTasks!=null && !defaultTasks.isEmpty();
	}
	public ProjectView getRootProject() {
		return rootProject;
	}
	public void setRootProject(ProjectView rootProject) {
		clear();
		this.rootProject = rootProject;
		setDefaultTasks(rootProject.getDefaultTasks());
		setTasks(rootProject.getTasks());
		addTasksFromSubprojects(rootProject);
	}
	public Collection<GradleTaskGroup> getGroups() {
		return groups.values();
	}
	public void addTask(TaskView task) {
		if (task == null) return;
		String[] splittedName = task.getName().split(":");
		String name = splittedName[splittedName.length - 1];
		GradleTaskGroup group = groups.get(name);
		if (group==null)
			groups.put(name, new GradleTaskGroup(task));
		else
			group.addTask(task);
	}
    /**
     * Returns the elements to display in a viewer. 
     * These elements can be presented as rows in a table, items in a list, etc.
     * 
     * @return the array of elements to display in a viewer
     */ 
	public Object[] getElements() {
		List<TaskView> tasks = getTasks();
		int n = tasks == null ? 0 : tasks.size();
		Collection<GradleTaskGroup> groups = getGroups();
		n += (groups == null ? 0 : groups.size());
		if (n == 0) return EMPTY_ARRAY;
		Object[] elements = new Object[n];
		int i = 0;
		for (Object task: tasks) elements[i++]=task;
		for (Object group: groups) elements[i++]=group;
		return elements;
	}
    /**
     * Returns whether this model contains any task or group.
     * @return <code>true</code> if this model contains any task or group,
     *  and <code>false</code> otherwise
     */
    public boolean hasChildren() {
		List<TaskView> tasks = getTasks();
		int n = tasks == null ? 0 : tasks.size();
		Collection<GradleTaskGroup> groups = getGroups();
		n += (groups == null ? 0 : groups.size());
		return n >0;
    }
	@Override
	public void clear() {
		super.clear();
		this.rootProject = null;
		for (GradleTaskGroup group: groups.values()) 
			group.clear();
		groups.clear();
	}
	/**
	 * Add to the model all tasks from all subprojects of the given project.
	 */
	private void addTasksFromSubprojects(ProjectView parentProject) {
		List<ProjectView> subprojects = parentProject.getSubProjects();
		// Add subproject tasks
		for (ProjectView subproject: subprojects)
			for (TaskView task: subproject.getTasks())
				addTask(task);
		// Add tasks from subprojects of subprojects
		for (ProjectView subproject: subprojects)
			addTasksFromSubprojects(subproject);
	}
	/**
	 * Retrieve the element corresponding to the given String or null.
	 */
	public Object getElement(String text) {
		if (text==null || rootProject == null) return null;
		if (groups.containsKey(text))
			return groups.get(text);
		for (TaskView task: tasks)
			if (text.equals(task.getFullTaskName())) return task;
		for (GradleTaskGroup group: groups.values())
			for (TaskView task: group.getTasks())
				if (text.equals(task.getFullTaskName())) return task;
		return null;
	}
}

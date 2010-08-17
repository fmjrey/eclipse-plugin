package org.gradle.eclipse;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.gradle.foundation.ProjectView;
import org.gradle.foundation.TaskView;

public class GradleAdapterFactory implements IAdapterFactory{

	
	@SuppressWarnings("unchecked")
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if(adapterType == IWorkbenchAdapter.class && adaptableObject instanceof ProjectView){
			return projectViewAdapter;
		}else if(adapterType == IWorkbenchAdapter.class && adaptableObject instanceof TaskView){
			return taskViewAdapter;
		}else
		return null;
	}


	public Class<?>[] getAdapterList() {
		return new Class[] {IWorkbenchAdapter.class};
	}
	
	private Object projectViewAdapter = 
		new WorkbenchAdapter(){
		
		public String getLabel(Object object) {
			return ((ProjectView)object).getFullProjectName();
		};
		
		@Override
		public Object[] getChildren(Object object) {
			return ((ProjectView)object).getTasks().toArray();
		}
	};
	
	private Object taskViewAdapter = 
		new WorkbenchAdapter(){
		
		@Override
		public String getLabel(Object object) {
			return((TaskView)object).getName();
		}
	};
}
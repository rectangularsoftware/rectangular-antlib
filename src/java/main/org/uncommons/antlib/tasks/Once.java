// ============================================================================
//   Copyright 2008-2012 Daniel W. Dyer
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.
// ============================================================================
package org.uncommons.antlib.tasks;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskContainer;
import org.apache.tools.ant.BuildException;
import java.util.List;
import java.util.LinkedList;

/**
 * Task container to ensure that some set of actions is only performed
 * once per build.  On completion of the actions, a property is set that
 * prevents subsequent executions.
 * @author Daniel Dyer
 */
public class Once extends Task implements TaskContainer
{
    private final List<Task> tasks = new LinkedList<Task>();

    private String property;

    /**
     * The name of the property to consult in order to determine whether
     * the nested tasks should be performed.  This is a required attribute.
     */
    public void setProperty(String property)
    {
        this.property = property;
    }


    public void addTask(Task task)
    {
        tasks.add(task);
    }


    @Override
    public void execute() throws BuildException
    {
        if (property == null || property.length() == 0)
        {
            throw new BuildException("Property name must be specified.");
        }
        else
        {
            String propertyValue = getProject().getProperty(property);
            // If no value is specified, the tasks are performed if the property
            // is set to any value.  If a value is specified, the tasks are only
            // performed if the property matches that value.
            if (propertyValue == null)
            {
                for (Task task : tasks)
                {
                    task.perform();
                }
                getProject().setProperty(property, "done");
            }
        }
    }
}

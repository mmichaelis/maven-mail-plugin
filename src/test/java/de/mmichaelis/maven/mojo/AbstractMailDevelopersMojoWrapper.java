/******************************************************************************
 * Copyright 2011 Mark Michaelis                                              *
 *                                                                            *
 * Licensed under the Apache License, Version 2.0 (the "License");            *
 * you may not use this file except in compliance with the License.           *
 * You may obtain a copy of the License at                                    *
 *                                                                            *
 *     http://www.apache.org/licenses/LICENSE-2.0                             *
 *                                                                            *
 * Unless required by applicable law or agreed to in writing, software        *
 * distributed under the License is distributed on an "AS IS" BASIS,          *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 * See the License for the specific language governing permissions and        *
 * limitations under the License.                                             *
 ******************************************************************************/

package de.mmichaelis.maven.mojo;

import org.apache.maven.project.MavenProject;

/**
 * @since 6/3/11 9:32 PM
 */
public class AbstractMailDevelopersMojoWrapper<T extends AbstractMailDevelopersMojo> extends AbstractMailMojoWrapper<T> {

  public AbstractMailDevelopersMojoWrapper(final T mojo) throws IllegalAccessException {
    super(mojo);
    addFields("project");
  }

  public void setProject(final MavenProject project) throws IllegalAccessException {
    fieldMap.get("project").set(mojo, project);
  }

}

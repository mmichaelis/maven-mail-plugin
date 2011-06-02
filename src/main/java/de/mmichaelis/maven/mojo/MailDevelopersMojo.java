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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal mail-developers
 */
public final class MailDevelopersMojo extends AbstractMailDevelopersMojo {
  /**
   * Get the text body for this email.
   *
   * @return the text of the email
   * @throws MojoExecutionException
   *          if an unexpected problem occurs.
   *          Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException
   *          if an expected problem (such as a compilation failure) occurs.
   *          Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  @Override
  protected String getPlainText() throws MojoExecutionException, MojoFailureException {
    return "Lorem Ipsum Dolor Sit Amet";
  }
}

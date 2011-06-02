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

import org.apache.maven.model.Developer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

/**
 * Abstract Mojo which sends mails to the configured developers.
 * @since 5/27/11 10:39 PM
 * @requiresProject true
 */
public abstract class AbstractMailDevelopersMojo extends AbstractMailMojo {
  /**
   * The maven project.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private MavenProject project;

  /**
   * Evaluates the recipients from the list of developers.
   * @return recipients of the email
   * @throws MojoExecutionException if a problem occurs evaluating the mail addresses
   */
  public final InternetAddress[] getRecipients() throws MojoExecutionException {
    final List<Developer> developers = project.getDevelopers();
    final List<InternetAddress> result = new ArrayList<InternetAddress>(developers.size());
    for (final Developer developer : developers) {
      final String developerId = developer.getId();
      final String email = developer.getEmail();
      final String name = developer.getName();
      if (email == null || email.trim().length() == 0) {
        getLog().warn("No email defined for developer " + developerId + ". Skipped.");
      }
      final InternetAddress[] addresses;
      try {
        addresses = InternetAddress.parse(email);
      } catch (AddressException e) {
        throw new MojoExecutionException("Unable to parse email for developer " + developerId + ".", e);
      }
      if (!isEmpty(name)) {
        for (final InternetAddress address : addresses) {
          try {
            address.setPersonal(name, getMimeCharSet());
          } catch (UnsupportedEncodingException e) {
            getLog().warn("Unable to set name for email of developer " + developerId + ".", e);
          }
        }
      }
      result.addAll(Arrays.asList(addresses));
    }
    return result.toArray(new InternetAddress[result.size()]);
  }
}

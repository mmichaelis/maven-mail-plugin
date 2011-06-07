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

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @since 6/7/11 9:53 PM
 * @goal mail
 */
public final class MailMojo extends AbstractMailMojo {
  /**
   * The message to send.
   * @parameter
   * @required
   */
  private Message message;

  /**
   * To whom to send the emails to.
   * @parameter
   * @required
   */
  private List<String> to;

  /**
   * Get the recipients for this email. If the length of the array is <code>null</code> no mail will be sent.
   *
   * @return the list of recipients
   * @throws MojoExecutionException
   *          if an unexpected problem occurs.
   *          Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException
   *          if an expected problem (such as a compilation failure) occurs.
   *          Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  @Override
  protected InternetAddress[] getRecipients() throws MojoExecutionException, MojoFailureException {
    final List<InternetAddress> result = new ArrayList<InternetAddress>(to.size());
    for (final String s : to) {
      try {
        result.addAll(Arrays.asList(InternetAddress.parse(s)));
      } catch (AddressException e) {
        throw new MojoExecutionException("E-Mail address " + s + " is invalid.", e);
      }
    }
    return result.toArray(new InternetAddress[result.size()]);
  }

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
    return message.getText(getLog());
  }
}

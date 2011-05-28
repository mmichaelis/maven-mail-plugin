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

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Goal which touches a timestamp file.
 *
 * @goal mail-developers
 */
public final class MailDevelopersMojo extends AbstractMailDevelopersMojo {
  public void execute() throws MojoExecutionException {
    final Set<InternetAddress> addresses = getDeveloperAddresses();
    if (addresses.isEmpty()) {
      getLog().warn("No developers configured. Skipping to send mail.");
      return;
    }
    final Session session = getSession();
    final MimeMessage message = new MimeMessage(session);
    try {
      message.addRecipients(Message.RecipientType.TO, addresses.toArray(new InternetAddress[addresses.size()]));
    } catch (MessagingException e) {
      getLog().warn("Failed to add recipients.", e);
    }
    try {
      message.setSubject("Subject Test");
      message.setText("Test Text");
    } catch (MessagingException e) {
      throw new MojoExecutionException("Failed to compose mail.", e);
    }
    try {
      Transport.send(message);
    } catch (MessagingException e) {
      throw new MojoExecutionException("Failed to send mail.", e);
    }
  }

}

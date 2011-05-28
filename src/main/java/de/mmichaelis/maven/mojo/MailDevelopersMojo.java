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
import java.util.List;
import java.util.Set;

/**
 * Goal which touches a timestamp file.
 *
 * @goal mail-developers
 */
public final class MailDevelopersMojo extends AbstractMailDevelopersMojo {
  /**
   * Subject of the email to send.
   * @parameter expression="${project.groupId}.${project.artifactId}: Automatic Email"
   */
  private String subject;

  public void execute() throws MojoExecutionException {
    final InternetAddress[] addresses = getDeveloperAddresses();
    if (addresses.length == 0) {
      getLog().warn("No developers configured. Skipping to send mail.");
      return;
    }
    getLog().debug("Developer Emails to send a mail to: " + InternetAddress.toString(addresses));
    final MimeMessage message;
    try {
      message = createMessage();
    } catch (MessagingException e) {
      throw new MojoExecutionException("Could not create initial message.", e);
    }
    try {
      message.addRecipients(Message.RecipientType.TO, addresses);
    } catch (MessagingException e) {
      getLog().warn("Failed to add recipients.", e);
    }
    try {
      message.setSubject("Subject Test");
    } catch (MessagingException e) {
      throw new MojoExecutionException("Failed to compose mail.", e);
    }
    try {
      Transport.send(message);
    } catch (MessagingException e) {
      throw new MojoExecutionException("Failed to send mail.", e);
    }
  }

  @Override
  protected String getText() {
    return "Lorem Ipsum Dolor Sit Amet.";
  }
}

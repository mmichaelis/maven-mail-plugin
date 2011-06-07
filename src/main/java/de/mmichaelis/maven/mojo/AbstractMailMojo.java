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

import de.mmichaelis.maven.mojo.mail.MailBase;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import javax.mail.internet.InternetAddress;
import java.nio.charset.Charset;

import static javax.mail.internet.MimeUtility.mimeCharset;

/**
 * Abstract Mojo for derived mail mojos.
 *
 * @since 5/27/11 11:01 PM
 */
public abstract class AbstractMailMojo extends AbstractMojo {
  /**
   * Can be used to disable sending mails.
   *
   * @parameter default-value="false" expression="${mail.skip}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private boolean skip;

  /**
   * <p>
   * The address to send the email from. Examples:
   * <pre>
   *   john.doe@example.com
   *   John Doe &lt;john.doe@example.com>
   * </pre>
   * or any valid email address.
   * </p>
   *
   * @parameter expression="${mail.from}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String from;

  /**
   * The host to send the mail from.
   *
   * @parameter default-value="localhost" expression="${mail.smtp.host}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String smtphost;

  /**
   * The host to send the mail from.
   *
   * @parameter default-value="25" expression="${mail.smtp.port}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private Integer smtpport;

  /**
   * When automatic mails should expire in days.
   *
   * @parameter default-value="1" expression="${mail.expires}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String expires;

  /**
   * Charset for the emails.
   *
   * @parameter default="${project.build.outputEncoding}" expression="${mail.charset}"
   * @see Charset
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String charset;

  /**
   * Priority for the notification email.
   *
   * @parameter default="low" expression="${mail.priority}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String priority;

  /**
   * If to fail the build if an error occurs while composing or sending the mail.
   *
   * @parameter default="true" expression="${mail.failOnError}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private boolean failOnError;

  /**
   * Topic to add to the mail subject. Might ease filtering mails. Set it to
   * empty string to omit a prepended topic. Topic will be added like this:
   * <pre>
   *   [&lt;Topic>] &lt;Subject>
   * </pre>
   *
   * @parameter default="maven-mail-plugin" expression="${mail.topic}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String topic;

  /**
   * Subject to add to the email. For possibly supported tokens see the appropriate
   * goal description.
   * <pre>
   *   [&lt;Topic>] &lt;Subject>
   * </pre>
   *
   * @parameter default="${project.groupId}.${project.artifactId}: Automatic Email" expression="${mail.subject}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private String subject;

  /**
   * If true the mail won't be send but just logged at INFO level.
   *
   * @parameter default="false" expression="${mail.dryRun}"
   */
  @SuppressWarnings({"UnusedDeclaration"})
  private boolean dryRun;

  /**
   * Execute the Mojo.
   *
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  @Override
  public final void execute() throws MojoExecutionException, MojoFailureException {
    final MailBase base = new MailBase(getLog());
    base.setCharset(charset);
    base.setDryRun(dryRun);
    base.setExpires(expires);
    base.setFailOnError(failOnError);
    base.setFrom(from);
    base.setPlainText(getPlainText());
    base.setPriority(priority);
    base.setRecipients(getRecipients());
    base.setSkip(skip);
    base.setSmtphost(smtphost);
    base.setSmtpport(smtpport);
    base.setSubject(subject);
    base.setTopic(topic);
    base.execute();
  }

  /**
   * Return the charset in MIME-format.
   *
   * @return charset
   */
  protected final String getMimeCharSet() {
    return mimeCharset(charset);
  }

  /**
   * Get the recipients for this email. If the length of the array is <code>null</code> no mail will be sent.
   *
   * @return the list of recipients
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  protected abstract InternetAddress[] getRecipients() throws MojoExecutionException, MojoFailureException;

  /**
   * Get the text body for this email.
   *
   * @return the text of the email
   * @throws MojoExecutionException if an unexpected problem occurs.
   *                                Throwing this exception causes a "BUILD ERROR" message to be displayed.
   * @throws MojoFailureException   if an expected problem (such as a compilation failure) occurs.
   *                                Throwing this exception causes a "BUILD FAILURE" message to be displayed.
   */
  protected abstract String getPlainText() throws MojoExecutionException, MojoFailureException;

}

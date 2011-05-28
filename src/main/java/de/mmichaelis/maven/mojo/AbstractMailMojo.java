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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Abstract Mojo for derived mail mojos.
 * @since 5/27/11 11:01 PM
 */
public abstract class AbstractMailMojo extends AbstractMojo {
  /**
   * The address to send the email from. Examples:
   * <pre>
   *   john.doe@example.com
   *   John Doe &lt;john.doe@example.com>
   * </pre>
   * or any valid email address.
   * @parameter expression="${mail.from}"
   */
  private String from;

  /**
   * The host to send the mail from.
   * @parameter default-value="localhost" expression="${mail.smtp.host}"
   */
  private String smtphost;

  /**
   * The host to send the mail from.
   * @parameter default-value="localhost" expression="${mail.smtp.port}"
   */
  private String smtpport;

  protected Session getSession() {
    final Properties properties = new Properties();
    properties.setProperty("mail.from", from);
    properties.setProperty("mail.smtp.host", smtphost);
    properties.setProperty("mail.smtp.port", smtpport);
    final Session session = Session.getDefaultInstance(properties);
    session.setDebug(getLog().isDebugEnabled());
    return session;
  }
}

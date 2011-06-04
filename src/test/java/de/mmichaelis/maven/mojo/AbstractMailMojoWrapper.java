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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.support.membermodification.MemberMatcher.field;

/**
 * @since 6/3/11 9:32 PM
 */
public class AbstractMailMojoWrapper<T extends AbstractMailMojo> extends AbstractClassWrapper<T> {

  public AbstractMailMojoWrapper(final T mojo) throws IllegalAccessException {
    super(mojo);

    addFields("skip", "from", "smtphost", "smtpport", "expires", "charset", "priority", "failOnError", "topic", "subject", "dryRun");

    /* Defaults */
    setSkip(false);
    setSmtpHost("localhost");
    setSmtpPort(25);
    setExpires("1");
    setCharset("ISO-8859-1");
    setPriority("low");
    setFailOnError(true);
    setTopic("maven-mail-plugin");
    setSubject("de.mmichaelis:maven-mail-plugin: Automatic Email");
    setDryRun(false);
  }

  public T getMojo() {
    return wrapped;
  }

  public void execute() throws MojoExecutionException, MojoFailureException {
    wrapped.execute();
  }

  public void setSkip(final boolean skip) throws IllegalAccessException {
    fieldMap.get("skip").set(wrapped, skip);
  }

  public void setFrom(final String from) throws IllegalAccessException {
    fieldMap.get("from").set(wrapped, from);
  }

  public void setSmtpHost(final String smtphost) throws IllegalAccessException {
    fieldMap.get("smtphost").set(wrapped, smtphost);
  }

  public void setSmtpPort(final Integer smtpport) throws IllegalAccessException {
    fieldMap.get("smtpport").set(wrapped, smtpport);
  }

  public void setExpires(final String expires) throws IllegalAccessException {
    fieldMap.get("expires").set(wrapped, expires);
  }

  public void setCharset(final String charset) throws IllegalAccessException {
    fieldMap.get("charset").set(wrapped, charset);
  }

  public void setPriority(final String priority) throws IllegalAccessException {
    fieldMap.get("priority").set(wrapped, priority);
  }

  public void setFailOnError(final boolean failOnError) throws IllegalAccessException {
    fieldMap.get("failOnError").set(wrapped, failOnError);
  }

  public void setTopic(final String topic) throws IllegalAccessException {
    fieldMap.get("topic").set(wrapped, topic);
  }

  public void setSubject(final String subject) throws IllegalAccessException {
    fieldMap.get("subject").set(wrapped, subject);
  }

  public void setDryRun(final boolean dryRun) throws IllegalAccessException {
    fieldMap.get("dryRun").set(wrapped, dryRun);
  }
}

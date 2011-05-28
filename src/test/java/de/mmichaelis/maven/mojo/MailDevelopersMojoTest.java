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

import com.dumbster.smtp.SmtpMessage;
import org.apache.maven.model.Developer;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.field;

/**
 * Tests {@link MailDevelopersMojo}.
 *
 * @since 5/27/11 11:26 PM
 */
public class MailDevelopersMojoTest extends AbstractMailMojoTestBase {

  private MailDevelopersMojo mojo;
  private List<Developer> developers;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();

    developers = new ArrayList<Developer>(3);
    for (int i = 0; i < 3; i++) {
      final Developer developer = new Developer();
      developer.setId("id" + i);
      developer.setEmail("dev"+i+"@example.org");
      developer.setName("Deve Loper " + i);
      developers.add(developer);
    }

    mojo = new MailDevelopersMojo();
    final MavenProject project = mock(MavenProject.class);
    final Field projectField = field(MailDevelopersMojo.class, "project");
    final Field fromField = field(MailDevelopersMojo.class, "from");
    final Field hostField = field(MailDevelopersMojo.class, "smtphost");
    final Field portField = field(MailDevelopersMojo.class, "smtpport");
    projectField.set(mojo, project);
    fromField.set(mojo, "from@example.org");
    hostField.set(mojo, "localhost");
    portField.set(mojo, String.valueOf(smtpPort));

    when(project.getDevelopers()).thenReturn(developers);
  }

  /**
   * Simple test to send an email.
   *
   * @throws Exception in case of an error
   */
  @Test
  public void testSimple() throws Exception {
    mojo.execute();
    assertTrue(smtpServer.getReceivedEmailSize() == 1);
    final SmtpMessage next = (SmtpMessage) smtpServer.getReceivedEmail().next();
    System.out.println(next);
    final String[] tos = next.getHeaderValues("To");
    for (int i = 0; i < tos.length; i++) {
      String to = tos[i];
      System.out.println(to);
    }
  }
}

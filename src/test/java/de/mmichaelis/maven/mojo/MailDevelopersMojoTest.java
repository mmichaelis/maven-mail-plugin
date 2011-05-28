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

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.apache.maven.model.Developer;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests {@link MailDevelopersMojo}.
 *
 * @since 5/27/11 11:26 PM
 */
public class MailDevelopersMojoTest {
  public static int findFreePort()
          throws IOException {
    ServerSocket server =
            new ServerSocket(0);
    int port = server.getLocalPort();
    server.close();
    return port;
  }

  /**
   * Simple test to send an email.
   *
   * @throws Exception in case of an error
   */
  @Test
  public void testSimple() throws Exception {
    final int freePort = findFreePort();
    SimpleSmtpServer server = SimpleSmtpServer.start(freePort);
    try {
      final Developer developer = new Developer();
      developer.setId("id1");
      developer.setEmail("mark.michaelis@coremedia.com");
      developer.setName("Mark Michaelis");
      final MailDevelopersMojo mojo = new MailDevelopersMojo();
      final MavenProject project = mock(MavenProject.class);
      final Field projectField = PowerMockito.field(MailDevelopersMojo.class, "project");
      final Field fromField = PowerMockito.field(MailDevelopersMojo.class, "from");
      final Field hostField = PowerMockito.field(MailDevelopersMojo.class, "smtphost");
      final Field portField = PowerMockito.field(MailDevelopersMojo.class, "smtpport");
      projectField.set(mojo, project);
      fromField.set(mojo, "from@example.org");
      hostField.set(mojo, "localhost");
      portField.set(mojo, String.valueOf(freePort));
      when(project.getDevelopers()).thenReturn(Collections.singletonList(developer));
      mojo.execute();
      assertTrue(server.getReceivedEmailSize() == 1);
      Iterator emailIter = server.getReceivedEmail();
      SmtpMessage email = (SmtpMessage) emailIter.next();
      assertEquals(email.getHeaderValue("Subject"), "Test");
      assertTrue(email.getBody().equals("Test Body"));
    } finally {
      server.stop();
    }
  }
}

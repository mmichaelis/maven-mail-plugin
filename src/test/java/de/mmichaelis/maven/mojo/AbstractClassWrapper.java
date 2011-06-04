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

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.powermock.api.support.membermodification.MemberMatcher.field;

/**
 * Wrapper for accessing classes through the mock framework.
 * @since 6/4/11 11:57 PM
 */
public class AbstractClassWrapper<T> {
  protected final T wrapped;
  protected final Map<String, Field> fieldMap = new HashMap<String, Field>();

  public AbstractClassWrapper(final T wrapped) {
    this.wrapped = wrapped;
  }

  public T getWrapped() {
    return wrapped;
  }


  protected final void addFields(final String... fields) {
    for (final String field : fields) {
      addField(field);
    }
  }

  protected final void addField(final String fieldName) {
    final Field classField = field(wrapped.getClass(), fieldName);
    fieldMap.put(fieldName, classField);
  }
}

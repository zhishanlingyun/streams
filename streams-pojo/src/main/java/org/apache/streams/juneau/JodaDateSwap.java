/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.streams.juneau;

import org.apache.streams.data.util.RFC3339Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.juneau.BeanSession;
import org.apache.juneau.ClassMeta;
import org.apache.juneau.parser.ParseException;
import org.apache.juneau.transform.StringSwap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Transforms {@link org.joda.time.DateTime} to {@link String Strings}.
 */
public class JodaDateSwap extends StringSwap<DateTime> {

  DateTimeFormatter dateFormatter;

  /**
   * Constructor.
   */
  public JodaDateSwap() {
    dateFormatter = ISODateTimeFormat.dateTime();
  }

  @Override /* PojoSwap */
  public String swap(BeanSession session, DateTime o) {
    DateTimeFormatter dateFormatter = this.dateFormatter;
    if( StringUtils.isNotBlank(session.getProperty("format", String.class, RFC3339Utils.UTC_STANDARD_FMT.toString()))) {
      dateFormatter = DateTimeFormat.forPattern(session.getProperty("format", String.class, RFC3339Utils.UTC_STANDARD_FMT.toString()));
    }
    return dateFormatter.print(o);
  }

  @Override /* PojoSwap */
  public DateTime unswap(BeanSession session, String f, ClassMeta<?> hint) throws ParseException {
    DateTimeFormatter dateFormatter = this.dateFormatter;
    if( StringUtils.isNotBlank(session.getProperty("format", String.class, RFC3339Utils.UTC_STANDARD_FMT.toString()))) {
      dateFormatter = DateTimeFormat.forPattern(session.getProperty("format", String.class, RFC3339Utils.UTC_STANDARD_FMT.toString()));
    }
    return dateFormatter.parseDateTime(f);
  }

}

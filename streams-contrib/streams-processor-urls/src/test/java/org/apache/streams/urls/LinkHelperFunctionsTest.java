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

package org.apache.streams.urls;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LinkHelperFunctionsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkHelperFunctionsTest.class);

    @Test
    public void testIsURL() {
        assertTrue(LinkResolverHelperFunctions.isURL("http://goo.gl/wSrHDA"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://ow.ly/u4Kte"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://x.co/3yapt"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://bit.ly/1cX5Rh4"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://t.co/oP8JYB0MYW"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://goo.gl/wSrHDA"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://t.co/fBoCby3l1t"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://paper.li/GuyKawasaki"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://www.google.com"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://goo.gl/wSrHDA"));
        assertTrue(LinkResolverHelperFunctions.isURL("http://www.cnn.com"));
    }

    @Test
    public void testContainsURL() {
        assertTrue(LinkResolverHelperFunctions.containsURLs("here is the URL: http://goo.gl/wSrHDA"));
        assertTrue(LinkResolverHelperFunctions.containsURLs("a lovely day for URLing it up http://ow.ly/u4Kte"));
        assertTrue(LinkResolverHelperFunctions.containsURLs("http://x.co/3yapt is really cool"));
        assertTrue(LinkResolverHelperFunctions.containsURLs("http://bit.ly/1cX5Rh4 me likes"));
        assertTrue(LinkResolverHelperFunctions.containsURLs("http://t.co/oP8JYB0MYW wtf mate?"));
        assertTrue(LinkResolverHelperFunctions.containsURLs("Every morning is a good morning in URL world: http://goo.gl/wSrHDA"));

        assertFalse(LinkResolverHelperFunctions.containsURLs("Every day I awake, only to find, I have no URLS"));
        assertFalse(LinkResolverHelperFunctions.containsURLs("Http:// to be or not to be"));
        assertFalse(LinkResolverHelperFunctions.containsURLs("Can I get an http://X up on the board pat?"));
        assertFalse(LinkResolverHelperFunctions.containsURLs("You must remember Joey, no matter how much you ftp://stink you must never, EVER, take a shower in my dressing room!"));
    }


    @Test
    public void testSimple() {

        LinkResolverHelperFunctions.purgeAllDomainWaitTimes();
        String domain1 = "smashew.com";

        // safe to run...
        assertEquals("smashew.com: No need to wait", 0, LinkResolverHelperFunctions.waitTimeForDomain(domain1));
        // get required sleep
        long smashewSleepTime1 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        // sleep
        LOGGER.debug("Sleeping: " + new Date().getTime() + "-" + smashewSleepTime1);
        safeSleep(smashewSleepTime1);
        LOGGER.debug("Slept For: " + new Date().getTime() + "-" + smashewSleepTime1);
        // safe to run again
        assertEquals("smashew.com: No need to wait", 0, LinkResolverHelperFunctions.waitTimeForDomain(domain1));
    }

    private static void safeSleep(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch(Exception e) {
            // noOp
        }
    }

    @Test
    public void testSingle() {

        LinkResolverHelperFunctions.purgeAllDomainWaitTimes();

        String domain1 = "smashew.com";
        String domain2 = "google.com";

        long smashewSleepTime1 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        long smashewSleepTime2 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        long smashewSleepTime3 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        long smashewSleepTime4 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);

        LOGGER.debug("smashew.com: " + smashewSleepTime1 + "," + smashewSleepTime2 + "," + smashewSleepTime3 + "," + smashewSleepTime4);

        assertEquals("smashew.com: No need to wait", 0, smashewSleepTime1);
        assertTrue("smashew.com: Wait for at least min x 1", smashewSleepTime2 >= (LinkResolverHelperFunctions.RECENT_DOMAINS_BACKOFF - LinkResolverHelperFunctions.DEFAULT_STAGGER));
        assertTrue("smashew.com: Wait for at least min x 2", smashewSleepTime3 >= (LinkResolverHelperFunctions.RECENT_DOMAINS_BACKOFF * 2) - (LinkResolverHelperFunctions.DEFAULT_STAGGER * 2));
        assertTrue("smashew.com: Wait for at least min x 3", smashewSleepTime4 >= (LinkResolverHelperFunctions.RECENT_DOMAINS_BACKOFF * 3) - (LinkResolverHelperFunctions.DEFAULT_STAGGER * 3));

        long timeBeforeSleep = new Date().getTime();
        LOGGER.debug("Sleeping for: " + smashewSleepTime4 + " ms");

        safeSleep(smashewSleepTime4);
        LOGGER.debug("Actually slept for: " + (new Date().getTime() - timeBeforeSleep) + " ms");

        long postSleepDomain1 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        LOGGER.debug("smashew.com: Post Sleep domain1: " + postSleepDomain1);
        assertEquals("Smashew.com: No need to wait after sleep", 0, postSleepDomain1);

    }

    @Test
    public void testMulti() {

        LinkResolverHelperFunctions.purgeAllDomainWaitTimes();
        String domain1 = "smashew.com";
        String domain2 = "google.com";

        long smashewSleepTime1 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        long smashewSleepTime2 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        long smashewSleepTime3 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);

        long googleSleepTime1 = LinkResolverHelperFunctions.waitTimeForDomain(domain2);
        long googleSleepTime2 = LinkResolverHelperFunctions.waitTimeForDomain(domain2);

        LOGGER.debug("smashew.com: " + smashewSleepTime1 + "," + smashewSleepTime2 + "," + smashewSleepTime3);
        LOGGER.debug("google.com: " + googleSleepTime1 + "," + googleSleepTime2);

        assertEquals("smashew.com: No need to wait", 0, smashewSleepTime1);
        assertTrue("smashew.com: Wait for at least min x 1", smashewSleepTime2 >= (LinkResolverHelperFunctions.RECENT_DOMAINS_BACKOFF - LinkResolverHelperFunctions.DEFAULT_STAGGER));
        assertTrue("smashew.com: Wait for at least min x 2", smashewSleepTime3 >= (LinkResolverHelperFunctions.RECENT_DOMAINS_BACKOFF * 2) - (LinkResolverHelperFunctions.DEFAULT_STAGGER * 2));

        assertEquals("google.com: No need to wait", 0, googleSleepTime1);
        assertTrue("google.com: No need to wait", googleSleepTime2 >= LinkResolverHelperFunctions.RECENT_DOMAINS_BACKOFF - LinkResolverHelperFunctions.DEFAULT_STAGGER);

        try {
            LOGGER.debug("WAITING FOR: " + smashewSleepTime3);
            Thread.sleep(smashewSleepTime3);
        }
        catch(Exception e) {
            // noOp
        }

        long postSleepDomain1 = LinkResolverHelperFunctions.waitTimeForDomain(domain1);
        long postSleepDomain2 = LinkResolverHelperFunctions.waitTimeForDomain(domain2);

        LOGGER.debug("smashew.com: Post Sleep domain1: " + postSleepDomain1);
        LOGGER.debug("google.com:  Post Sleep domain2: " + postSleepDomain2);

        assertEquals("Smashew.com: No need to wait after sleep", 0, postSleepDomain1);
        assertEquals("google.com: No need to wait after sleep", 0, postSleepDomain2);

    }

}

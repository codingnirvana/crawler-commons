/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package crawlercommons.sitemaps;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import crawlercommons.test.TestUtils;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;

public class SiteMapParserTest extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    public void testSitemapIndex() throws UnknownFormatException, IOException {
        SiteMapParser parser = new SiteMapParser();
        String contentType = "text/xml";

        String scontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<sitemapindex xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" + "<sitemap>"
                        + "  <loc>http://www.example.com/sitemap1.xml.gz</loc>" + " <lastmod>2004-10-01T18:23:17+00:00</lastmod>" + " </sitemap>" + "<sitemap>"
                        + "    <loc>http://www.example.com/sitemap2.xml.gz</loc>" + "   <lastmod>2005-01-01</lastmod>" + " </sitemap>" + " </sitemapindex>";

        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemapindex.xml");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(true, asm.isIndex());
        assertEquals(true, asm instanceof SiteMapIndex);
        SiteMapIndex smi = (SiteMapIndex) asm;
        assertEquals(2, smi.getSitemaps().size());
        AbstractSiteMap currentSiteMap = smi.getSitemap(new URL("http://www.example.com/sitemap1.xml.gz"));
        assertNotNull(currentSiteMap);
        assertEquals("http://www.example.com/sitemap1.xml.gz", currentSiteMap.getUrl().toString());
        assertEquals(SiteMap.convertToDate("2004-10-01T18:23:17+00:00"), currentSiteMap.getLastModified());

        currentSiteMap = smi.getSitemap(new URL("http://www.example.com/sitemap2.xml.gz"));
        assertNotNull(currentSiteMap);
        assertEquals("http://www.example.com/sitemap2.xml.gz", currentSiteMap.getUrl().toString());
        assertEquals(SiteMap.convertToDate("2005-01-01"), currentSiteMap.getLastModified());
    }

    public void testSitemapXML() throws UnknownFormatException, IOException {
        SiteMapParser parser = new SiteMapParser();
        String contentType = "text/xml";

        String scontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" + "  <url>" + "<loc>http://www.example.com/</loc>"
                        + "<lastmod>2005-01-01</lastmod>" + "<changefreq>monthly</changefreq>" + "<priority>0.8</priority>" + "</url>" + "<url>"
                        + "<loc>http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii</loc>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                        + "<loc>http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand</loc>" + "<lastmod>2004-12-23</lastmod>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                        + "<loc>http://www.example.com/catalog?item=74&amp;desc=vacation_newfoundland</loc>" + "<lastmod>2004-12-23T18:00:15+00:00</lastmod>" + "<priority>0.3</priority>" + "</url>"
                        + "<url>" + "<loc>http://www.example.com/catalog?item=83&amp;desc=vacation_usa</loc>" + "<lastmod>2004-11-23</lastmod>" + "</url>" + "</urlset>";

        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemap.xml");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(false, asm.isIndex());
        assertEquals(true, asm instanceof SiteMap);
        SiteMap sm = (SiteMap) asm;
        assertEquals(5, sm.getSiteMapUrls().size());
    }

    public void testSitemapXMLWithLeadingWhitespaces() throws IOException, UnknownFormatException {
        SiteMapParser parser = new SiteMapParser();
        String contentType = "text/xml";

        String scontent = "               <?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" + "  <url>" + "<loc>http://www.example.com/</loc>"
                + "<lastmod>2005-01-01</lastmod>" + "<changefreq>monthly</changefreq>" + "<priority>0.8</priority>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii</loc>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand</loc>" + "<lastmod>2004-12-23</lastmod>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=74&amp;desc=vacation_newfoundland</loc>" + "<lastmod>2004-12-23T18:00:15+00:00</lastmod>" + "<priority>0.3</priority>" + "</url>"
                + "<url>" + "<loc>http://www.example.com/catalog?item=83&amp;desc=vacation_usa</loc>" + "<lastmod>2004-11-23</lastmod>" + "</url>" + "</urlset>";

        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemap.xml");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(false, asm.isIndex());
        assertEquals(true, asm instanceof SiteMap);
        SiteMap sm = (SiteMap) asm;
        assertEquals(5, sm.getSiteMapUrls().size());
    }

    public void testSitemapXMLWithLeadingWhitespacesInGzip() throws Exception, UnknownFormatException {
        SiteMapParser parser = new SiteMapParser(true);
        String contentType = "application/gzip";

        byte[] content = TestUtils.readFile("/sitemaps/detail23.xml.gz");
        URL url = new URL("http://www.example.com/detail23.xml.gz");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(false, asm.isIndex());
        assertEquals(true, asm instanceof SiteMap);
        SiteMap sm = (SiteMap) asm;
        assertEquals(20000, sm.getSiteMapUrls().size());
    }


    public void testSitemapTXT() throws UnknownFormatException, IOException {
        SiteMapParser parser = new SiteMapParser();
        String contentType = "text/plain";

        String scontent = "http://www.example.com/catalog?item=1\nhttp://www.example.com/catalog?item=11";

        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemap.txt");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(false, asm.isIndex());
        assertEquals(true, asm instanceof SiteMap);
        SiteMap sm = (SiteMap) asm;
        assertEquals(2, sm.getSiteMapUrls().size());
    }

    public void testSitemapParserBrokenXml() throws IOException {
        // This Sitemap contains badly formatted XML and can't be read
        SiteMapParser parser = new SiteMapParser();
        String contentType = "text/xml";

        String scontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset " + "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"><url>"
                        + "<!-- This file is not a valid XML file --></url><url><loc>" + "http://cs.harding.edu/fmccown/sitemaps/something.html</loc>"
                        + "</url><!-- missing opening url tag --></url></urlset>";
        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemapindex.xml");
        AbstractSiteMap asm = null;
        try {
            asm = parser.parseSiteMap(contentType, content, url);
        } catch (UnknownFormatException e) {
            // If Exception is thrown Test was successful
            return;
        }
        fail("Test failed: UnknownFormatException not thrown");
    }

    public void testSitemapParserWithRelaxedBaseUrlRulesNotSet() throws IOException, UnknownFormatException {
        SiteMapParser parser = new SiteMapParser(false);
        String contentType = "text/xml";

        String scontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" + "  <url>" + "<loc>http://www.example.com/</loc>"
                + "<lastmod>2005-01-01</lastmod>" + "<changefreq>monthly</changefreq>" + "<priority>0.8</priority>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii</loc>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand</loc>" + "<lastmod>2004-12-23</lastmod>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=74&amp;desc=vacation_newfoundland</loc>" + "<lastmod>2004-12-23T18:00:15+00:00</lastmod>" + "<priority>0.3</priority>" + "</url>"
                + "<url>" + "<loc>http://www.example.com/catalog?item=83&amp;desc=vacation_usa</loc>" + "<lastmod>2004-11-23</lastmod>" + "</url>" + "</urlset>";

        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemap/sitemap.xml");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(false, asm.isIndex());
        assertEquals(true, asm instanceof SiteMap);
        SiteMap sm = (SiteMap) asm;
        assertEquals(0, sm.getSiteMapUrls().size());
    }

    public void testSitemapParserWithRelaxedBaseUrlRulesSet() throws IOException, UnknownFormatException {
        SiteMapParser parser = new SiteMapParser(true);
        String contentType = "text/xml";

        String scontent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">" + "  <url>" + "<loc>http://www.example.com/</loc>"
                + "<lastmod>2005-01-01</lastmod>" + "<changefreq>monthly</changefreq>" + "<priority>0.8</priority>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii</loc>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=73&amp;desc=vacation_new_zealand</loc>" + "<lastmod>2004-12-23</lastmod>" + "<changefreq>weekly</changefreq>" + "</url>" + "<url>"
                + "<loc>http://www.example.com/catalog?item=74&amp;desc=vacation_newfoundland</loc>" + "<lastmod>2004-12-23T18:00:15+00:00</lastmod>" + "<priority>0.3</priority>" + "</url>"
                + "<url>" + "<loc>http://www.example.com/catalog?item=83&amp;desc=vacation_usa</loc>" + "<lastmod>2004-11-23</lastmod>" + "</url>" + "</urlset>";

        byte[] content = scontent.getBytes();
        URL url = new URL("http://www.example.com/sitemap/sitemap.xml");
        AbstractSiteMap asm = parser.parseSiteMap(contentType, content, url);
        assertEquals(false, asm.isIndex());
        assertEquals(true, asm instanceof SiteMap);
        SiteMap sm = (SiteMap) asm;
        assertEquals(5, sm.getSiteMapUrls().size());
    }

}

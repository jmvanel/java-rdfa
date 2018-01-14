/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 * [See end of file]
 */
package net.rootdev.javardfa.conformance;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.util.FileManager;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
//import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import net.rootdev.javardfa.ParserFactory;
import net.rootdev.javardfa.Setting;
import net.rootdev.javardfa.StatementSink;
import net.rootdev.javardfa.ParserFactory.Format;
import net.rootdev.javardfa.jena.JenaStatementSink;
import net.rootdev.javardfa.query.QueryUtilities;

/**
 * @author Damian Steer <pldms@mac.com>
 * @author jmv
 */
@RunWith(Parameterized.class)
public abstract class RDFaConformanceGoldenResult {

    final static Logger log = LoggerFactory.getLogger(RDFaConformanceGoldenResult.class);
    final static String testPrefix = "http://rdfa.info/test-suite/test-cases";

    public static Collection<String[]> testFiles(String manifestURI)
            throws URISyntaxException, IOException {

        FileManager fm = FileManager.get();
        Model manifest = fm.loadModel(manifestURI);

        Query manifestExtract = QueryFactory.read("manifest-extract-2017.rq");

        QueryExecution qe = QueryExecutionFactory.create(manifestExtract, manifest);
        ResultSet results = qe.execSelect();
        if (!results.hasNext()) {
            throw new RuntimeException("No results");
        }
        Collection<String[]> tests = new ArrayList<String[]>();
        while (results.hasNext()) {
            QuerySolution soln = results.next();
            String[] params = new String[4];
            params[0] = soln.getResource("test").getURI();
            params[1] = soln.getLiteral("title").getString();
            params[2] = soln.getResource("input").getURI();
            params[3] = soln.getResource("result").getURI();
            tests.add(params);
        }

        return tests;
    }
    private final String test;
    private final String title;
    private final String input;
    private final String result;
		static int failureCount;

    public RDFaConformanceGoldenResult(
    		String test, String title, String input, String result) {
        this.test = test;
        this.title = title;
        this.input = input;
        this.result = result;
    }

    public abstract XMLReader getParser(Model model) throws SAXException;


    @Test
    public void compare() throws XMLStreamException, IOException, ParserConfigurationException, SAXException {
        URL htmlURL = new URL(input);
        URL compareURL = new URL(result);
        
        if (result.endsWith(".rq")) compareQuery(htmlURL, compareURL);
        else compareRDF(htmlURL, compareURL);
    }

    private void compareRDF(URL htmlURL, URL compareURL) throws SAXException, IOException {
        System.out.println("TEST: compareRDF: HTML: " + htmlURL + ", reference TTL: " + compareURL);
        String cf = compareURL.toExternalForm();
        if (cf.matches("file:/[^/][^/].*")) cf = cf.replaceFirst("file:/", "file:///");
        String hf = htmlURL.toExternalForm();
        if (hf.matches("file:/[^/][^/].*")) hf = hf.replaceFirst("file:/", "file:///");

        Model modelGoldenResult = RDFDataMgr.loadModel(compareURL.toExternalForm());
        Model modelFromHTML = ModelFactory.createDefaultModel();
        StatementSink sink = new JenaStatementSink(modelFromHTML);
        XMLReader parser = ParserFactory.createReaderForFormat(sink, Format.XHTML, Setting.OnePointOne);
        // to avoid HTTP 403 from server; alternatively maybe set agent on the URL connection
        System.setProperty("http.agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36     (KHTML, like Gecko) Chrome/28.0.1500.29 Safari/537.36");
        parser.parse(hf);

        boolean result = modelGoldenResult.isIsomorphicWith(modelFromHTML);
        if( !result) {
        	System.err.println("TEST FAILURE " + htmlURL + " => print Graph obtained");
        	modelFromHTML.write(System.err, "TTL");
        	System.err.println("FAILURE " + htmlURL + " => print model Golden Result");
        	modelGoldenResult.write(System.err, "TTL");
        	failureCount ++;
        }
        assertTrue("Files match (" + htmlURL + ")", result);
    }

    private void compareQuery(URL htmlURL, URL compareURL) throws SAXException, IOException {
        Query query = QueryFactory.read(compareURL.toExternalForm());
        
        Map<String, Query> qs = QueryUtilities.makeQueries(ParserFactory.Format.XHTML,
                htmlURL.toExternalForm());

        assertTrue("We have a query", qs.size() != 0);

        Query qFromHTML = qs.get(qs.keySet().toArray()[0]);

        assertEquals("Query matches (" + htmlURL + ")",
                Algebra.compile(query),
                Algebra.compile(qFromHTML));
    }
}

/*
 * (c) Copyright 2009 University of Bristol
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

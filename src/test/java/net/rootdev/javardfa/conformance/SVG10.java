package net.rootdev.javardfa.conformance;

import org.apache.jena.rdf.model.Model;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import net.rootdev.javardfa.ParserFactory;
import net.rootdev.javardfa.ParserFactory.Format;
import net.rootdev.javardfa.jena.JenaStatementSink;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *
 * @author pldms
 */
public class SVG10 extends RDFaConformanceGoldenResult
{
    @Parameters
    public static Collection<String[]> testFiles()
            throws URISyntaxException, IOException {
        return RDFaConformanceGoldenResult.
            testFiles( testPrefix + "/rdfa1.0/svg/manifest.ttl" );
    }

    public SVG10(
      String test, String title, String input, String result) {
        super( test, title, input, result);
    }

    @Override
    public XMLReader getParser(Model model) throws SAXException {
        return ParserFactory.createReaderForFormat(new JenaStatementSink(model), Format.XHTML);
    }
}

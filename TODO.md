## Summary

- 5 problems common to RDFa 1.0 and 1.1
- 6 unique problems in RDFa 1.1 , including a big case

Only problems with XML wrapping are analysed here; XHML(1 or 5), XHML(4 or 5), and SVG wrapping were not analysed; they are likely to be much the same.

## rdfa1.0

http://rdfa.info/test-suite/test-cases/rdfa1.0/xml/
- 0013.xml : the lang @fr should not be there (is it really the spec !?)
- 0113.xml : dc:title should include new line character , even is there is no non-blank character
- 0121.xml : resource="[]" should be implemented like about="[]"
- 0202.xml : xml:base= at XML root should not be used in RDFa (is it really the spec !?)
- 0203.xml : in `<desc about="" xml:base="http://example.com/" ` , xml:base= should not be used in RDFa

## rdfa1.1

http://rdfa.info/test-suite/test-cases/rdfa1.1/xml/
The problems above happen also.
Here are the new problems.

- 0214.xml : base URL is the subject instead of a blank node
- 0259.xml : `<dcat:>` is produced instead of dcat:
- 0264.xml : property="rdfs:seeAlso" without prefix declaration should not be produced
- 0265.xml : property="rdfs:seeAlso" without prefix declaration should not be produced
- 0271.xml : property="rdfs:seeAlso" without prefix declaration should not be produced
- 0295.xml : BIG CASE! foaf:photo1.jpg is output instead of  `<http://rdfa.info/test-suite/test-cases/rdfa1.1/xml/photo1.jpg>`
- 0318.xml : subject is `<http://xmlns.com/foaf/0.1/#me>` instead of `<http://rdfa.info/test-suite/test-cases/rdfa1.1/xml/0318.xml#me>` , in `<div about ="#me">`
- 0319.xml : NOT CLEAR: isomorphic test in Jena imperfect ?
`@prefix pr:    <relative/iri#> .`
really seems the same as golden Result:
`@prefix pr:    <http://rdfa.info/test-suite/test-cases/rdfa1.1/xml/relative/iri#> .`


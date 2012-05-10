package edu.virginia.lib.beverly;

public class Constants {

	final protected static String repo1Url = "http://localhost:8080/fedora";
	final protected static String indexSDef = "indexable:sdef";
	final protected static String indexMethod = "getIndexingMetadata";
	
	final protected static String test1 = "test:1";
	final protected static String test1dissemination = repo1Url + "/objects/"
			+ test1 + "/" + indexSDef + "/" + indexMethod;

	final protected static String test2 = "test:2";
	final protected static String test2dissemination = repo1Url + "/objects/"
			+ test1 + "/" + indexSDef + "/" + indexMethod;
}

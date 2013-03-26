ENML4j
======

Simple utility classes to handle ENML (Evernote Markup Language) in Java.

Overview
--------
ENML4j provide a convenient way to:

  * Convert an Evernote `Note` ENML content to an HTML file referencing `Resource`s as configurable URL
  * Convert an Evernote `Note` ENML content to an HTML file with inline `Resource`s as Data URI
  * Update an Evernote `Note` ENML content after updating it's `Resource`s 

ENML4j is design to be simple, customizable and to produce valid XHTML.
ENML4j uses stAX to parse ENML and write XHTML.

Getting started
-----
Converting a Notes ENML content to XHTML is as simple as:

```java
    ENMLProcessor = new ENMLProcessor();
    FileOutputStream fos = new FileOutputStream(/path/to/file.html");
    ENMLProcessor.noteToInlineHTML(note, fos);
```

The best way to start is to look at [enml4j-sample](https://github.com/vanduynslagerp/enml4j-sample)

### Including the SDK in your project

The easiest way to incorporate the SDK into your Java project is to use Maven. If you're using Maven already, simply add a new dependency to your `pom.xml`:

```xml
    	<dependency>
			<groupId>com.syncthemall</groupId>
			<artifactId>enml4j</artifactId>
			<version>0.1.0</version>
		</dependency>
```

If you'd prefer to build the jar yourself, it's as simple as running

```bash
$ mvn package
```

You'll find `enml4j-0.1.0.jar` in the target directory after the build completes. This single JAR contains everything needed to use the API.

### Dependencies

You'll also need to be sure to include in your classpath a stAX implementation and [evernote-sdk-java](https://github.com/evernote/evernote-sdk-java)

User Guide
-------------
### ENMLProcessor

This is the entry point of ENML4j. This class should be instantiated and kept in reference (as a static for example) for better performances. When
converting a `Note` to HTML the Evernote DTD has to be parsed the first time, then stays in memory. Parsing the DTD the first time is time-consuming.


This class rely on stAX to convert ENML to HTML. ENML4j will uses the default stAX implementation on the platform. Implementation can be easily chosen : [StAX Factory Classes]
(http://docs.oracle.com/javaee/5/tutorial/doc/bnbem.html#bnbeo)

This class is thread-safe as long as the stAX implementation of `XMLInputFactory`, `XMLOutputFactory`, `XMLEventFactory` are thread-safe. Almost all implementation of this classes are thread-safe.

### Customize the conversion

ENML4j rely on `Converter`s classes to convert specifics ENML tags to an HTML equivalent. Default `Converter`s are provided and instantiated by default.

  * `DefaultNoteTagConverter`
  * `DefaultInlineMediaTagConverter`
  * `DefaultTodoTagConverter`
  * `DefaultCryptTagConverter`
  * `DefaultInlineMediaTagConverter`

For specifics needs `BaseConverter` and `MediaConverter` can be implemented and set with
`ENMLProcessor#setConverters(BaseConverter, MediaConverter, BaseConverter, BaseConverter)` and `ENMLProcessor#setInlineConverters(BaseConverter, MediaConverter, BaseConverter, BaseConverter)`.

For more information on ENML see [Understanding the Evernote Markup Language](http://dev.evernote.com/start/core/enml.php)

Development
-----------
Any bug reported properly will be fixed.
Any features request will be taken under consideration.

Potential future features:
  * Convert ENML to PDF with [Flying Saucer](https://github.com/flyingsaucerproject/flyingsaucer)
  * Convert ENML to Plain text
  * Convert valid XHTML to ENML
  * Convert "street HTML" to ENML (very unlikely considering the lack of Java HTML renderer)

License
-------
MIT

*Free Software, Fuck Yeah!*   
package org.asciidoctor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.asciidoctor.OptionsBuilder.options;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class WhenBackendIsRevealJs {

    public static final String DOCUMENT = "= A document\n\n Test";

    private Asciidoctor asciidoctor;


    @Before
    public void initAsciidoctor() {
        this.asciidoctor = Asciidoctor.Factory.create();
    }

    @Test
    public void should_create_simple_slides() throws IOException {
        String filename = "sample";
        File inputFile = new File("build/resources/test/" + filename + ".adoc");
        File outputFile1 = new File(inputFile.getParentFile(), filename + ".html");
        removeFileIfItExists(outputFile1);
        asciidoctor.requireLibrary("asciidoctor-revealjs");
        asciidoctor.convertFile(inputFile,
            options()
                .backend("revealjs")
                .safe(SafeMode.UNSAFE)
                .attributes(
                    AttributesBuilder.attributes()
                        .attribute("revealjsdir", "https://cdn.jsdelivr.net/npm/reveal.js@4.5.0")
                )
                .get()
        );

        Document doc = Jsoup.parse(outputFile1, "UTF-8");

        assertThat(outputFile1.exists(), is(true));

        List<String> stylesheets = doc.head().getElementsByTag("link").stream()
            .filter(element -> "stylesheet".equals(element.attr("rel")))
            .map(element -> element.attr("href"))
            .collect(toList());

        assertThat(stylesheets, hasItem("https://cdn.jsdelivr.net/npm/reveal.js@4.5.0/dist/theme/black.css"));

        Element firstChild = doc.body().children().first();
        assertThat(firstChild.className(), containsString("reveal"));
    }


    private void removeFileIfItExists(File file) throws IOException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("can't delete file");
            }
        }
    }

}

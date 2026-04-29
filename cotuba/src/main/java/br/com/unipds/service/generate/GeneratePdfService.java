package br.com.unipds.service.generate;

import br.com.unipds.service.markedown.MarkeDownService;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.IElement;
import com.itextpdf.layout.properties.AreaBreakType;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.stream.Stream;

public class GeneratePdfService implements GenerateBookService {

    @Override
    public void generate(Path diretorioDosMD, Path arquivoDeSaida) {

        try (var writer = new PdfWriter(Files.newOutputStream(arquivoDeSaida));
             var pdf = new PdfDocument(writer);
             var pdfDocument = new Document(pdf)) {

            //TODO: definir título e autor para o livro
            pdf.getDocumentInfo().setTitle("Livro");
            pdf.getDocumentInfo().setAuthor("Autor");

            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*.md");

            List<Path> arquivosMD = MarkeDownService.render(diretorioDosMD);

                arquivosMD.forEach(arquivoMD -> {
                    Parser parser = Parser.builder().build();
                    Node document = getDocument(arquivoMD, parser);

                    try {
                        HtmlRenderer renderer = HtmlRenderer.builder().build();
                        String html = renderer.render(document);

                        List<IElement> convertToElements = HtmlConverter.convertToElements(html);

                        if (pdf.getNumberOfPages() == 0) {
                            pdf.addNewPage();
                        }
                        PdfOutline rootOutline = pdf.getOutlines(false);
                        if (rootOutline == null) {
                            pdf.initializeOutlines();
                            rootOutline = pdf.getOutlines(false);
                        }

                        // TODO: usar título do capítulo
                        PdfOutline chapterOutline = rootOutline.addOutline("Capítulo");
                        chapterOutline.addDestination(PdfExplicitDestination.createFit(pdf.getLastPage()));

                        for (IElement element : convertToElements) {
                            pdfDocument.add((IBlockElement) element);
                        }
                        // TODO: não adicionar página depois do último capítulo
                        pdfDocument.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                    } catch (Exception ex) {
                        throw new IllegalStateException("Erro ao renderizar para HTML o arquivo " + arquivoMD, ex);
                    }

                });


        } catch (Exception ex) {
            throw new IllegalStateException("Erro ao gerar PDF: " + arquivoDeSaida.toAbsolutePath(), ex);
        }

    }
}

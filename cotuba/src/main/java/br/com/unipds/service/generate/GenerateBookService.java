package br.com.unipds.service.generate;

import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.Heading;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;

import java.nio.file.Files;
import java.nio.file.Path;

public interface GenerateBookService {
        void generate(Path diretorioDosMD, Path arquivoDeSaida);

        default Node getDocument(Path arquivoMD, Parser parser) {
                Node document = null;
                try {
                        document = parser.parseReader(Files.newBufferedReader(arquivoMD));
                        document.accept(new AbstractVisitor() {
                                @Override
                                public void visit(Heading heading) {
                                        if (heading.getLevel() == 1) {
                                                // capítulo
                                                String tituloDoCapitulo = ((Text) heading.getFirstChild()).getLiteral();
                                                // TODO: usar título do capítulo
                                        } else if (heading.getLevel() == 2) {
                                                // seção
                                        } else if (heading.getLevel() == 3) {
                                                // título
                                        }
                                }

                        });
                } catch (Exception ex) {
                        throw new IllegalStateException("Erro ao fazer parse do arquivo " + arquivoMD, ex);
                }
                return document;
        }
}

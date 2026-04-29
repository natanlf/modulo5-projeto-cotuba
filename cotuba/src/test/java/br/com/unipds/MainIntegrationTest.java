package br.com.unipds;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class MainIntegrationTest {

    @TempDir
    Path diretorioDosMd;

    private Path arquivoMd;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    private static final String CONTEUDO_MD = "# Capítulo Teste\n\nEste é um conteúdo de um arquivo Markdown.";
    private static final String NOME_ARQUIVO_MD = "01-introducao.md";

    @BeforeEach
    void setUp() throws Exception {
        System.setErr(new PrintStream(errContent));

        arquivoMd = diretorioDosMd.resolve(NOME_ARQUIVO_MD);
        Files.writeString(arquivoMd, CONTEUDO_MD);
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    private int executarCotuba(String... args) {
        return new Main().executar(args);
    }

    @Test
    @DisplayName("Deve gerar o arquivo PDF corretamente e conter o texto do Markdown.")
    void deveGerarPdfComSucesso() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");

        int exitCode = executarCotuba("-d", diretorioDosMd.toString(), "-f", "pdf", "-o", arquivoSaida.toString());

        assertThat(exitCode).isEqualTo(0);
        assertThat(arquivoSaida).exists().isRegularFile();

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(arquivoSaida.toFile()))) {
            String textoDaPagina = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(1));
            assertThat(textoDaPagina)
                    .contains("Capítulo Teste")
                    .contains("Este é um conteúdo de um arquivo Markdown.");
        }
    }

    @Test
    @DisplayName("Deve gerar o arquivo EPUB corretamente e conter o HTML renderizado")
    void deveGerarEpubComSucesso() throws Exception {
        Path arquivoSaida = diretorioDosMd.resolve("saida.epub");

        int exitCode = executarCotuba("-d", diretorioDosMd.toString(), "-f", "epub", "-o", arquivoSaida.toString());

        assertThat(exitCode).isEqualTo(0);
        assertThat(arquivoSaida).exists().isRegularFile();

        EpubReader epubReader = new EpubReader();
        Book epubLido = epubReader.readEpub(Files.newInputStream(arquivoSaida));

        byte[] dadosDoHtml = epubLido.getSpine().getResource(0).getData();
        String htmlDoCapitulo = new String(dadosDoHtml);

        assertThat(htmlDoCapitulo)
                .contains("<h1>Capítulo Teste</h1>")
                .contains("<p>Este é um conteúdo de um arquivo Markdown.</p>");
    }

    @Test
    @DisplayName("Deve retornar status 1 e exibir erro caso o formato seja inválido")
    void deveFalharEEncerrarQuandoFormatoEhInvalido() {
        Path arquivoSaida = diretorioDosMd.resolve("saida.mobi");

        int exitCode = executarCotuba("-d", diretorioDosMd.toString(), "-f", "mobi", "-o", arquivoSaida.toString());

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Formato do ebook inválido: mobi");
        assertThat(arquivoSaida).doesNotExist();
    }

    @Test
    @DisplayName("Deve retornar status 1 e exibir erro caso diretório não tenha arquivos .md")
    void deveFalharEEncerrarQuandoNaoHaArquivosMd() throws Exception {
        Files.deleteIfExists(arquivoMd);
        Path arquivoSaida = diretorioDosMd.resolve("saida.pdf");

        int exitCode = executarCotuba("-d", diretorioDosMd.toString(), "-f", "pdf", "-o", arquivoSaida.toString(), "-v");

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Não foram encontrados capítulos");
    }

    @Test
    @DisplayName("Deve acionar a ajuda do CLI e encerrar em caso de argumento desconhecido")
    void deveAcionarAjudaEEncerrarAoPassarArgumentoInvalido() {
        int exitCode = executarCotuba("-x");

        assertThat(exitCode).isEqualTo(1);
        assertThat(errContent.toString()).contains("Unrecognized option: -x");
    }
}
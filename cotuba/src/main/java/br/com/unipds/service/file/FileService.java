package br.com.unipds.service.file;

import br.com.unipds.service.ProcessBookService;
import br.com.unipds.service.generate.GenerateEpubService;
import br.com.unipds.service.generate.GeneratePdfService;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

public class FileService {


    public static int processFile(CommandLine cmd) {
        Path diretorioDosMD;
        String formato;
        Path arquivoDeSaida;
        boolean modoVerboso = false;

        try {

            diretorioDosMD = checkDirectory(cmd);

            formato = getFormat(cmd);

            arquivoDeSaida = getArquivoDeSaida(cmd, formato);

            manageDirectory(arquivoDeSaida);

            modoVerboso = cmd.hasOption("verbose");

            generateFile(formato, diretorioDosMD, arquivoDeSaida);

            System.out.println("Arquivo gerado com sucesso: " + arquivoDeSaida);
            return 0;

        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            if (modoVerboso) {
                System.err.println();
                ex.printStackTrace();
            }
            return 1;
        }
    }

    private static void generateFile(String formato, Path diretorioDosMD, Path arquivoDeSaida) {
        if ("pdf".equals(formato)) {
            new ProcessBookService(new GeneratePdfService()).process(diretorioDosMD, arquivoDeSaida);

        } else if ("epub".equals(formato)) {

            new ProcessBookService(new GenerateEpubService()).process(diretorioDosMD, arquivoDeSaida);
        } else {
            throw new IllegalArgumentException("Formato do ebook inválido: " + formato);
        }
    }

    private static void manageDirectory(Path arquivoDeSaida) throws IOException {
        if (Files.isDirectory(arquivoDeSaida)) {
            // deleta arquivos do diretório recursivamente
            Files.walk(arquivoDeSaida).sorted(Comparator.reverseOrder())
                    .map(Path::toFile).forEach(File::delete);
        } else {
            Files.deleteIfExists(arquivoDeSaida);
        }
    }

    private static Path getArquivoDeSaida(CommandLine cmd, String formato) {
        Path arquivoDeSaida;
        String nomeDoArquivoDeSaidaDoEbook = cmd.getOptionValue("output");
        if (nomeDoArquivoDeSaidaDoEbook != null) {
            arquivoDeSaida = Paths.get(nomeDoArquivoDeSaidaDoEbook);
        } else {
            arquivoDeSaida = Paths.get("book." + formato.toLowerCase());
        }
        return arquivoDeSaida;
    }

    private static String getFormat(CommandLine cmd) {
        String formato;
        String nomeDoFormatoDoEbook = cmd.getOptionValue("format");

        if (nomeDoFormatoDoEbook != null) {
            formato = nomeDoFormatoDoEbook.toLowerCase();
        } else {
            formato = "pdf";
        }
        return formato;
    }

    private static Path checkDirectory(CommandLine cmd) {
        Path diretorioDosMD;
        String nomeDoDiretorioDosMD = cmd.getOptionValue("dir");

        if (nomeDoDiretorioDosMD != null) {
            diretorioDosMD = Paths.get(nomeDoDiretorioDosMD);
            if (!Files.isDirectory(diretorioDosMD)) {
                throw new IllegalArgumentException(nomeDoDiretorioDosMD + " não é um diretório.");
            }
        } else {
            Path diretorioAtual = Paths.get("");
            diretorioDosMD = diretorioAtual;
        }
        return diretorioDosMD;
    }

}

package br.com.unipds.service.options;

import org.apache.commons.cli.*;

public class OptionsService {

    public static Options buildOptions(Options options) {

        addDirectory(options);

        addFormat(options);

        addFileName(options);

        enableLogs(options);

        return options;
    }

    private static void enableLogs(Options options) {
        var opcaoModoVerboso = new Option("v", "verbose", false,
                "Habilita modo verboso.");
        options.addOption(opcaoModoVerboso);
    }

    private static void addFileName(Options options) {
        var opcaoDeArquivoDeSaida = new Option("o", "output", true,
                "Arquivo de saída do ebook. Default: book.{formato}.");
        options.addOption(opcaoDeArquivoDeSaida);
    }

    private static void addFormat(Options options) {
        var opcaoDeFormatoDoEbook = new Option("f", "format", true,
                "Formato de saída do ebook. Pode ser: pdf ou epub. Default: pdf");
        options.addOption(opcaoDeFormatoDoEbook);
    }

    private static void addDirectory(Options options) {
        var opcaoDeDiretorioDosMD = new Option("d", "dir", true,
                "Diretório que contém os arquivos md. Default: diretório atual.");
        options.addOption(opcaoDeDiretorioDosMD);
    }

}

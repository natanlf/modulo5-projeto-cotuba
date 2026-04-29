package br.com.unipds.service.commandline;

import org.apache.commons.cli.*;

public class ProcessComandLineService {

    public static CommandLine getCommandLine(String[] args, Options options) {
        CommandLineParser cmdParser = new DefaultParser();
        var ajuda = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            ajuda.printHelp("cotuba", options);
            return null;
        }
        return cmd;
    }

}

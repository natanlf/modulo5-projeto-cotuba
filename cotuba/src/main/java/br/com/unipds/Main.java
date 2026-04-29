package br.com.unipds;

import br.com.unipds.service.OptionsService;
import br.com.unipds.service.ProcessComandLineService;
import br.com.unipds.service.file.FileService;
import org.apache.commons.cli.CommandLine;

import org.apache.commons.cli.Options;

public class Main {

    void main(String[] args) {
        int exitCode = executar(args);
        if (exitCode != 0) {
            System.exit(exitCode);
        }
    }

    int executar(String[] args) {
        var options = OptionsService.buildOptions(new Options());

        CommandLine cmd = ProcessComandLineService.getCommandLine(args, options);
        if (cmd == null) return 1;

        return FileService.processFile(cmd);
    }




}
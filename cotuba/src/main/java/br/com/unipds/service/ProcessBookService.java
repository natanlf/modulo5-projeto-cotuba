package br.com.unipds.service;

import br.com.unipds.service.generate.GenerateBookService;

import java.nio.file.Path;

public class ProcessBookService {

        private final GenerateBookService generateBookService;

        public ProcessBookService(GenerateBookService generateBookService) {
            this.generateBookService = generateBookService;
        }

        public void process(Path diretorioDosMD, Path arquivoDeSaida) {
            generateBookService.generate(diretorioDosMD, arquivoDeSaida);
        }

}

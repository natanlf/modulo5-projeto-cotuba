package br.com.unipds.service.generate;

import java.nio.file.Path;

public interface GenerateBookService {
        void generate(Path diretorioDosMD, Path arquivoDeSaida);
}

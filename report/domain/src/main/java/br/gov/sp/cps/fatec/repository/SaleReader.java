package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.Sale;

import java.util.List;

/** Porta de LEITURA: o relatório só consulta os leilões encerrados, nunca grava nem escuta eventos. */
public interface SaleReader {
    List<Sale> findAllNewestFirst();
}

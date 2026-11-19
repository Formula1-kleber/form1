package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.repository.LotRepository;

import java.util.List;

/** Lotes já encerrados (vendidos ou não), do mais recente para o mais antigo. Alimenta o relatório do admin. */
public class ListClosedLotsUseCase {
    private final LotRepository lots;

    public ListClosedLotsUseCase(LotRepository lots) {
        this.lots = lots;
    }

    public List<Lot> execute() {
        return lots.findClosed();
    }
}

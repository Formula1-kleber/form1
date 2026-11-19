package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.repository.LotRepository;

import java.time.Instant;
import java.util.List;

public class ListOpenLotsUseCase {
    private final LotRepository lots;

    public ListOpenLotsUseCase(LotRepository lots) {
        this.lots = lots;
    }

    public List<Lot> execute() {
        return lots.findOpen(Instant.now());
    }
}

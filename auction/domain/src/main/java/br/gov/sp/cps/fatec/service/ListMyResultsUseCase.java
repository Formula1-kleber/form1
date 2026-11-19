package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.entity.LotResult;
import br.gov.sp.cps.fatec.entity.LotResult.Outcome;
import br.gov.sp.cps.fatec.repository.LotRepository;

import java.util.List;

public class ListMyResultsUseCase {
    private final LotRepository lots;

    public ListMyResultsUseCase(LotRepository lots) {
        this.lots = lots;
    }

    public List<LotResult> execute(String userId) {
        return lots.findClosedWithBidFrom(userId, 20).stream().map(l -> toResult(l, userId)).toList();
    }

    private LotResult toResult(Lot l, String userId) {
        Outcome outcome = l.status() == Lot.Status.UNSOLD ? Outcome.UNSOLD
                : userId.equals(l.leaderId()) ? Outcome.WON : Outcome.LOST;
        long amount = outcome == Outcome.UNSOLD ? l.currentBid() : l.finalPrice();
        return new LotResult(l.id(), l.name(), outcome, l.leaderName() == null ? "" : l.leaderName(), amount, l.endsAt());
    }
}

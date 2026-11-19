package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.exception.BidRejectedException;
import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.entity.LotResult;
import br.gov.sp.cps.fatec.service.ListMyResultsUseCase;
import br.gov.sp.cps.fatec.service.ListOpenLotsUseCase;
import br.gov.sp.cps.fatec.service.PlaceBidUseCase;
import br.gov.sp.cps.fatec.controller.dto.BidRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** API de leilão - lado do participante: ver lotes abertos, dar lances e consultar seus resultados. */
@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    private final ListOpenLotsUseCase listOpen;
    private final PlaceBidUseCase placeBid;
    private final ListMyResultsUseCase myResults;

    public AuctionController(ListOpenLotsUseCase listOpen, PlaceBidUseCase placeBid, ListMyResultsUseCase myResults) {
        this.listOpen = listOpen;
        this.placeBid = placeBid;
        this.myResults = myResults;
    }

    /** Resultado dos leilões encerrados em que o usuário participou. */
    @GetMapping("/results/mine")
    public List<LotResult> myResults(@RequestHeader("X-User-Id") String userId) {
        return myResults.execute(userId);
    }

    @GetMapping
    public List<Lot> listOpen() {
        return listOpen.execute();
    }

    @PostMapping("/{id}/bids")
    public Lot bid(@PathVariable String id,
                   @RequestHeader("X-User-Id") String userId,
                   @RequestHeader("X-User-Name") String userName,
                   @RequestHeader(value = "X-User-Role", defaultValue = "USER") String role,
                   @RequestBody BidRequest request) {
        if ("ADMIN".equals(role)) { // admin só comanda o leilão, não participa
            throw new BidRejectedException(BidRejectedException.Reason.NOT_ALLOWED, "Administradores não podem dar lances.");
        }
        return placeBid.execute(id, userId, userName, request.amount());
    }
}

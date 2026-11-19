package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.exception.BidRejectedException;
import br.gov.sp.cps.fatec.entity.Lot;
import br.gov.sp.cps.fatec.service.CreateLotUseCase;
import br.gov.sp.cps.fatec.service.ListOpenLotsUseCase;
import br.gov.sp.cps.fatec.controller.dto.CreateLotRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API de leilão - lado do administrador: é ela que COMANDA o sistema de leilão (abrir lotes, acompanhar os abertos).
 * O gateway só deixa ADMIN chegar aqui; a checagem repetida abaixo é uma segunda barreira.
 */
@RestController
@RequestMapping("/api/admin/auctions")
public class AuctionAdminController {

    private final CreateLotUseCase createLot;
    private final ListOpenLotsUseCase listOpen;

    public AuctionAdminController(CreateLotUseCase createLot, ListOpenLotsUseCase listOpen) {
        this.createLot = createLot;
        this.listOpen = listOpen;
    }

    @GetMapping
    public List<Lot> listOpen(@RequestHeader(value = "X-User-Role", required = false) String role) {
        requireAdmin(role);
        return listOpen.execute();
    }

    @PostMapping
    public Lot create(@RequestHeader(value = "X-User-Role", required = false) String role,
                      @RequestBody CreateLotRequest request) {
        requireAdmin(role);
        return createLot.execute(request.toCommand());
    }

    private void requireAdmin(String role) {
        if (!"ADMIN".equals(role)) {
            throw new BidRejectedException(BidRejectedException.Reason.NOT_ALLOWED, "Apenas administradores.");
        }
    }
}

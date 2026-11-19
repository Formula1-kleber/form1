package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.service.ListClosedLotsUseCase;
import br.gov.sp.cps.fatec.controller.dto.ClosedAuctionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Leitura interna, usada pelo report-service. O gateway NÃO tem rota para /internal/**,
 * então este endpoint só é alcançável de dentro da rede dos serviços.
 */
@RestController
@RequestMapping("/internal/auctions")
public class InternalAuctionController {

    private final ListClosedLotsUseCase listClosed;

    public InternalAuctionController(ListClosedLotsUseCase listClosed) {
        this.listClosed = listClosed;
    }

    @GetMapping("/closed")
    public List<ClosedAuctionResponse> closed() {
        return listClosed.execute().stream().map(ClosedAuctionResponse::from).toList();
    }
}

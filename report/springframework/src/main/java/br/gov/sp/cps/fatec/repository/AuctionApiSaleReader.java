package br.gov.sp.cps.fatec.repository;

import br.gov.sp.cps.fatec.entity.Sale;
import br.gov.sp.cps.fatec.repository.SaleReader;
import br.gov.sp.cps.fatec.repository.dto.ClosedAuctionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/** Adaptador de saída: lê os leilões encerrados na API de leilão (fonte única da verdade). */
@Component
class AuctionApiSaleReader implements SaleReader {

    private final RestClient http;

    AuctionApiSaleReader(@Value("${app.auction.url}") String auctionUrl) {
        this.http = RestClient.create(auctionUrl);
    }

    @Override
    public List<Sale> findAllNewestFirst() {
        List<ClosedAuctionResponse> closed = http.get()
                .uri("/internal/auctions/closed")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ClosedAuctionResponse>>() {});
        return closed == null ? List.of() : closed.stream().map(ClosedAuctionResponse::toDomain).toList();
    }
}

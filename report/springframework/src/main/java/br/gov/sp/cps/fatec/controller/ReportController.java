package br.gov.sp.cps.fatec.controller;

import br.gov.sp.cps.fatec.entity.SalesReport;
import br.gov.sp.cps.fatec.service.GenerateSalesReportUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** API de relatório: somente leitura, alimenta a tela do admin. Quem comanda o leilão é a API de leilão. */
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final GenerateSalesReportUseCase report;

    public ReportController(GenerateSalesReportUseCase report) {
        this.report = report;
    }

    @GetMapping("/sales")
    public SalesReport sales() {
        return report.execute();
    }
}

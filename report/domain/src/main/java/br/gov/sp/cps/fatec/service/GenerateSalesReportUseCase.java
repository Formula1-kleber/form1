package br.gov.sp.cps.fatec.service;

import br.gov.sp.cps.fatec.entity.Sale;
import br.gov.sp.cps.fatec.entity.SalesReport;
import br.gov.sp.cps.fatec.repository.SaleReader;

import java.util.List;

public class GenerateSalesReportUseCase {
    private final SaleReader sales;

    public GenerateSalesReportUseCase(SaleReader sales) {
        this.sales = sales;
    }

    public SalesReport execute() {
        List<Sale> all = sales.findAllNewestFirst();
        long revenue = all.stream().filter(Sale::sold).mapToLong(Sale::finalPrice).sum();
        long sold = all.stream().filter(Sale::sold).count();
        return new SalesReport(revenue, sold, all.size() - sold, all);
    }
}

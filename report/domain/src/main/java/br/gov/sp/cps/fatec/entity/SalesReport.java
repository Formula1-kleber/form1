package br.gov.sp.cps.fatec.entity;

import java.util.List;

public record SalesReport(long totalRevenue, long soldCount, long unsoldCount, List<Sale> sales) {}

package br.gov.sp.cps.fatec.configuration;

import br.gov.sp.cps.fatec.repository.SaleReader;
import br.gov.sp.cps.fatec.service.GenerateSalesReportUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {
    @Bean
    GenerateSalesReportUseCase generateSalesReportUseCase(SaleReader sales) {
        return new GenerateSalesReportUseCase(sales);
    }
}

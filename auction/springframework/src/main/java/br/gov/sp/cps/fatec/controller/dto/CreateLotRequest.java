package br.gov.sp.cps.fatec.controller.dto;

import br.gov.sp.cps.fatec.service.CreateLotUseCase;

/** Corpo do POST de criação de lote (comando do administrador). */
public record CreateLotRequest(String name, String description, long referenceValue, long startPrice,
                               long durationSeconds) {

    public CreateLotUseCase.Command toCommand() {
        return new CreateLotUseCase.Command(name, description, referenceValue, startPrice, durationSeconds);
    }
}

package br.gov.sp.cps.fatec.exception;

public class BidRejectedException extends RuntimeException {

    public enum Reason {
        NOT_FOUND("NAO_ENCONTRADO"),
        INVALID_DATA("DADOS_INVALIDOS"),
        NOT_ALLOWED("NAO_PERMITIDO"),
        CLOSED("ENCERRADO"),
        ALREADY_LEADING("JA_LIDERA"),
        SAME_BID("LANCE_IGUAL"),
        TOO_LOW("LANCE_BAIXO");

        private final String code;

        Reason(String code) { this.code = code; }

        public String code() { return code; }
    }

    private final Reason reason;

    public BidRejectedException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() { return reason; }
}

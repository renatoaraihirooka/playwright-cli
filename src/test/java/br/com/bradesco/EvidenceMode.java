package br.com.bradesco;

enum EvidenceMode {
    ALL_ACTIONS,
    FINAL_SCREEN,
    FAILURE_ONLY;

    static EvidenceMode from(String value) {
        try {
            return value == null ? ALL_ACTIONS : valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "evidence.mode deve ser ALL_ACTIONS, FINAL_SCREEN ou FAILURE_ONLY",
                    exception
            );
        }
    }
}
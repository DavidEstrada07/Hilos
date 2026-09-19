public enum EstadoPaquete {

    RECIBIDO,
    ALMACENADO,
    CLASIFICANDO,
    CLASIFICADO,
    EMPAQUETANDO,
    EMPAQUETADO,
    EN_EXPEDICION,
    EN_REPARTO,
    NUEVO_INTENTO,
    ENTREGADO,
    DEVUELTO;

    public boolean puedeCambiarA(EstadoPaquete nuevo) {
        switch (this) {
            case RECIBIDO:
                return nuevo == ALMACENADO;
            case ALMACENADO:
                return nuevo == CLASIFICANDO;
            case CLASIFICANDO:
                return nuevo == CLASIFICADO;
            case CLASIFICADO:
                return nuevo == EMPAQUETANDO;
            case EMPAQUETANDO:
                return nuevo == EMPAQUETADO;
            case EMPAQUETADO:
                return nuevo == EN_EXPEDICION;
            case EN_EXPEDICION:
                return nuevo == EN_REPARTO;
            case EN_REPARTO:
                return nuevo == ENTREGADO || nuevo == NUEVO_INTENTO || nuevo == DEVUELTO;
            case NUEVO_INTENTO:
                return nuevo == EN_REPARTO;
            default:
                return false;
        }
    }
}

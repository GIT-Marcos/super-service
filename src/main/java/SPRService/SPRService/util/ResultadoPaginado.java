package SPRService.SPRService.util;

import java.util.List;

public class ResultadoPaginado<T> {

    private final List<T> lista;
    private final Long cantidadResultados;

    public ResultadoPaginado(List<T> lista, Long cantidadResultados) {
        this.lista = lista;
        this.cantidadResultados = cantidadResultados;
    }

    public List<T> getLista() {
        return lista;
    }

    public Long getCantidadResultados() {
        return cantidadResultados;
    }
}

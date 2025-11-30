package factory;

import java.util.Map;

public interface ILaporan {
    String generate();
    Map<String, Object> getData();
    boolean validation();
}
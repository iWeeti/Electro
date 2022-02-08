package ml.putin.Electro;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Constants {

    public static final String PREFIX = "e.";
    public static final long OWNER = 464910064965386283L;
    public static final Map<Long, String> PREFIXES = new HashMap<>();

    public static final String getPrefix(long g) {
        return Constants.PREFIXES.computeIfAbsent(g, (l) -> Constants.PREFIX);
    }
}

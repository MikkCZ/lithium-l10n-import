package cz.mikk.mozilla.sumo.importer;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class PropertyKeyOrderComparator implements Comparator<String>, Serializable {

    private static final long serialVersionUID = -8512039124886881621L;

    private final Map<String, Integer> keysOrder;

    public PropertyKeyOrderComparator(LinkedHashSet<String> orderedKeys) {
        this.keysOrder = new HashMap<>(orderedKeys.size());
        int i = 0;
        for (String key : orderedKeys) {
            keysOrder.put(key, i++);
        }
    }

    @Override
    public int compare(String o1, String o2) {
        int order1 = keysOrder.getOrDefault(o1, Integer.MAX_VALUE);
        int order2 = keysOrder.getOrDefault(o2, Integer.MAX_VALUE);
        return Integer.compare(order1, order2);
    }
}

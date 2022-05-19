package com.summon.finder.component.card;

import java.util.Arrays;
import java.util.List;

class LocationImageModel  {
    static List<Boolean> getBooleanList(int count) {
        Boolean[] array = new Boolean[count];
        Arrays.fill(array, Boolean.FALSE);
        return Arrays.asList(array);
    }
}

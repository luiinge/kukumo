/**
 * @author Luis Iñesta Gelabert - linesta@iti.es | luiinge@gmail.com
 */
package iti.kukumo.core.util;


import java.util.*;


public class StringDistance {


    record Distance(String string, Double distance) { }


    public static List<String> closerStrings(
        String string,
        Collection<String> candidates,
        int limitResults
    ) {
        Comparator<Distance> greaterDistance = Comparator.comparing(Distance::distance);
        var stream = candidates.stream()
            .map(it -> new Distance(it, calculateDistance(string, it)))
            .sorted(greaterDistance.reversed());
        if (limitResults >= 0) {
            stream = stream.limit(limitResults);
        }
        return stream.map(Distance::string).toList();
    }




    private StringDistance() {
        // avoid instantiation
    }


    private static double calculateDistance(String string, String candidate) {
        return new Simil(string).getSimilarityInPercentFor(candidate);
    }

}

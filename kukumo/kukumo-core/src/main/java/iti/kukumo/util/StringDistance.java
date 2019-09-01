package iti.kukumo.util;


import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StringDistance {

    public static List<String> closerStrings(
        String string,
        Collection<String> candidates,
        int limitResults
    ){
        Comparator<Pair<String,Double>> greaterDistance = Comparator.comparing(Pair::value);
        return candidates.stream()
            .map(Pair.computeValue(candidate -> calculateDistance(string,candidate)))
            .sorted(greaterDistance.reversed())
            .limit(limitResults)
            .map(Pair::key)
            .collect(Collectors.toList());
    }


    private StringDistance() {
        // avoid instantiation
    }


    private static double calculateDistance(String string, String candidate) {
        return new Simil(string).getSimilarityInPercentFor(candidate);
    }






}

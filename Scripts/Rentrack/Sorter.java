/*
** Toy Problem #2 from Rentrack:
**
** Write a function to return a sorted copy of a list. The function's caller
** should be able to choose the comparison.  How would you test your sort
** function to convince yourself it works correctly?  Feel free to include
** more than one solution to this problem.
*/

import java.util.*;

public class Sorter<T extends Comparable> {
    Comparator<T> sortFunction;

    public Sorter(Comparator<T> sortFunction) {
        setSortFunction(sortFunction);
    }

    public Sorter() {
        setSortFunction(
            new Comparator<T>() {
                public int compare(T c1, T c2) {
                    return c1.compareTo(c2);
                }
            }
        );
    }

    public Collection<T> sort(Collection<T> list) {
        ArrayList<T> sortedList = new ArrayList<T>(list);
        Collections.sort(sortedList, sortFunction);
        return sortedList;
    }

    public void setSortFunction(Comparator<T> sortFunction) {
        this.sortFunction = sortFunction;
    }
}


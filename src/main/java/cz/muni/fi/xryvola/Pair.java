package cz.muni.fi.xryvola;

/**
 * Created by adam on 8.1.15.
 */
public class Pair {

    private Long first;
    private Long second;

    public Pair(Long first, Long second){
        this.first = first;
        this.second = second;
    }

    public Long getFirst() {
        return first;
    }

    public void setFirst(Long first) {
        this.first = first;
    }

    public Long getSecond() {
        return second;
    }

    public void setSecond(Long second) {
        this.second = second;
    }
}

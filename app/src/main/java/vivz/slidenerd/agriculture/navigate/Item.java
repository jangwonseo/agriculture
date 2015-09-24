package vivz.slidenerd.agriculture.navigate;


import java.io.Serializable;

/**
 * Created by makejin on 2015-09-20.
 */
class Item implements Serializable {
    private String name;
    private String addr;
    private double distance;
    private double lat;
    private double lon;
    //private TMapPoint point;
    public String getName(){return name;}
    public String getAddr(){return addr;}
    public double getDistance(){return distance;}
    public double getLat(){return lat;}
    public double getLon(){return lon;}

    // public TMapPoint getPoint(){return point;}

    public Item(String name, String addr, double distance, double lat, double lon){
        this.name = name;
        this.addr = addr;
        this.distance = distance;
        this.lat = lat;
        this.lon = lon;
    }
}

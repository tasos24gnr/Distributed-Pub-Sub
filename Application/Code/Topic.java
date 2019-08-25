import java.io.Serializable;

public class Topic implements Serializable {

    public String busLine;

    public Topic(String busLine){   //Constractor
        this.busLine = busLine;
    }

    //Getters and Setters

    public String getBusLine() {
        return busLine;
    }

    public  void setBusLine(String busLine) {
        this.busLine = busLine;
    }


}

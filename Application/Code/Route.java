import java.io.Serializable;

public class Route implements Serializable {

    public int RouteCode;
    public int LineCode;
    public String RouteType;
    public String lineName;

    public Route(int RouteCode , int LineCode , String RouteType , String lineName){  // Constractor
        this.RouteCode=RouteCode;
        this.LineCode=LineCode;
        this.RouteType=RouteType;
        this.lineName=lineName;
    }


    public int getRouteCode() {
        return RouteCode;
    }

    public void setRouteCode(int routeCode) {
        RouteCode = routeCode;
    }

    public int getLineCode() {
        return LineCode;
    }

    public void setLineCode(int lineCode) {
        LineCode = lineCode;
    }

    public String getRouteType() {
        return RouteType;
    }

    public void setRouteType(String routeType) {
        RouteType = routeType;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }
}

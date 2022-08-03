package areas;

public class GetArea {

    public static Area getArea(String area) {
        switch (area){
            case "house" -> {
                return new House();
            }
            case "forrest" -> {
                return new Forrest();
            }
            default ->  {
                System.out.println("Area not found");
                return null;
            }
        }


    }


}

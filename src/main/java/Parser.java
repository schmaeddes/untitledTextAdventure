import areas.GetArea;

import java.util.List;

public class Parser {

    public void parse(List<String> parameter) {

            switch (parameter.get(0)) {
                case "go" -> Commands.go(GetArea.getArea(parameter.get(1)));
                case "info" -> Commands.info();
            }

    }
}

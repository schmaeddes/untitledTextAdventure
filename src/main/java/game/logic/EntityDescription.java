package game.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityDescription {
    private final List<String> words;

    public EntityDescription(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public String getMainWord() {
        // TODO enities made up from multiple words. E.g., flat earth conspiracy
        return this.words.get(this.words.size() - 1);
    }

    public List<String> getAttributes() {
        return Collections.unmodifiableList(this.words.subList(0, this.words.size() - 1));
    }

    @Override
    public String toString() {
        return String.join(" ", this.words);
    }
}

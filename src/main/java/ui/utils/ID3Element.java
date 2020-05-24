package ui.utils;

import java.util.LinkedList;

public class ID3Element {
    private LinkedList<String> id3FedElements;

    public ID3Element() {
    }


    public ID3Element(LinkedList<String> id3FedElements) {
        this.id3FedElements = id3FedElements;
    }

    public LinkedList<String> getId3FedElements() {
        return id3FedElements;
    }

    public void setId3FedElements(LinkedList<String> id3FedElements) {
        this.id3FedElements = id3FedElements;
    }
}

package ui.utils;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ID3Element {
    private LinkedList<String> id3FedElements;
    private String elLine;
    private LinkedHashMap<String, LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>>> lineWithAccordingMap = new LinkedHashMap<>();

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

    public String getElLine() {
        return elLine;
    }

    public void setElLine(String elLine) {
        this.elLine = elLine;
    }

    public LinkedHashMap<String, LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>>> getLineWithAccordingMap() {
        return lineWithAccordingMap;
    }

    public void setLineWithAccordingMap(LinkedHashMap<String, LinkedHashMap<String, LinkedList<LinkedHashMap<String, LinkedHashMap<String, LinkedList<Double>>>>>> lineWithAccordingMap) {
        this.lineWithAccordingMap = lineWithAccordingMap;
    }
}

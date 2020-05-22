package ui;

import java.io.FileNotFoundException;

import static ui.lab3.Lab3.*;

public class Solution {

	public static void main(String ... args) {

		setColumnPosition();


		try {
			getVolleyball();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			getID3();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}

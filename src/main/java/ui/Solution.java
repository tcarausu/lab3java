package ui;

import ui.utils.Constant;

import java.io.File;
import java.io.FileNotFoundException;

import static ui.lab3.Lab3.*;

public class Solution {

	public static void main(String ... args) {

//		setColumnPosition();


		try {
			retrieveFileData(new File(Constant.volleyball));
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

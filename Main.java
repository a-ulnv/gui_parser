/**
 * February 8, 2015
 * Eclipse 4.2.1 - JavaSE-1.7
 */

import java.io.*;
import java.awt.*;
import javax.swing.*;


public class Main {
	
	// Global variables:
	// Creating an object to detect only predefined set of tokens
	static Token token;
	// Preparing a variable for the instance of lexical analyzer
	static Lexer lexer;
	
	
	public static void main(String[] args) {
		
		// The name of the input file
		String inputFileName = "input.txt";
		
		try {
			// attempt to create an instance of lexical analyzer and connet to the input file
			lexer = new Lexer(inputFileName);
		} catch (IOException e) {
			System.out.println("File not found: " + inputFileName);
		}
		
		try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e);}
		catch (IOException e) { e.printStackTrace(); }
		
		// If the first token is "Window", - attempt to parse GUI
		if (token == token.WINDOW) {
			try {
				if (parseGUI()){
					System.out.println("GUI parsed.");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		
	}
	
	// attempts to parse GUI with the use sub-methods to parse sub-elements
	static boolean parseGUI() throws IOException {
		
		// Variables for GUI
		String windowName;
		double windowWidth;
		double windowHeight;
		
		// Preparing the frame for the content
		JFrame myFrame = new JFrame();
		
		try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
		if (token == token.STRING) { // check if the token is a lexeme
			windowName = lexer.getLexeme(); // read the name for the Window
			myFrame.setTitle(windowName); // copying the name to the Window Frame
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			if (token == token.LEFT_PAREN) { // check if the token is a "("
				try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
				if (token == token.NUMBER) { // check if the token is a number
					windowHeight = lexer.getValue(); // read the width of the Window
					try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
					if (token == token.COMMA) { // the width and height are separated by the Comma
						try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
						if (token == token.NUMBER) { // check if the token is a number
							windowWidth = lexer.getValue(); // read the height of the Window
							Dimension d = new Dimension((int)windowWidth, (int)windowHeight); // cast the size variables to Integer type, and create Dimension
							myFrame.setMinimumSize(d); // Use the created Dimension d to set the minimum size of the window
							myFrame.setMaximumSize(d); // Use the created Dimension d to set the maximum size of the window
							try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
							if (token == token.RIGHT_PAREN) { // check if the token is a ")"
								try {
									if (parseLayout(myFrame)) { // reading and applying the description of a layout of the window
										if (parseWidgets(myFrame)) { // reading and inserting widgets into the window
											if (token == token.END) { // check if the token is the word "End"
												token = lexer.getNextToken(); // advance to the next token
												if (token == token.PERIOD) { // check if the token is
													// all the elements of the window are now parsed
													myFrame.setVisible(true); // make the window visible
													return true;
												}
											}
										}
									}
								} catch (SyntaxError e) {
									System.out.println(e);
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	// parameter "container" - where the widgets will be added
	static boolean parseWidgets(Container container) throws IOException {
		if (parseWidget(container)) { // attempt to parse one widget
			if (parseWidgets(container)) { // attempt to parse other widgets
				return true;
			}
			return true;
		}
		return false; // the base case
	}

	// parameter "container" - where the widgets will be added
	static boolean parseWidget(Container container) throws IOException {
		try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
		if (token == token.BUTTON) { // case widget "Button"
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			if (token == token.STRING) { // check for string to use as the name of the button
				String buttonName = lexer.getLexeme(); // save the name of the button
				try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
				if (token == token.SEMICOLON) { // check if there is a semicolon to end the widget declaration
					// add a new button to the referenced under "container" pointer object
					container.add(new JButton(buttonName));
					return true;
				}
			}
		}
		else if (token == token.LABEL) { // case widget "Label"
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			if (token == token.STRING) { // check if the string follows
				String labelName = lexer.getLexeme(); // save the name of the label
				try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
				if (token == token.SEMICOLON) { // check if semicolon follows
					container.add(new JLabel(labelName)); // add the label to referenced container
					return true;
				}
			}
		}
		else if (token == token.TEXTFIELD) { // case widget "TextField"
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			if (token == token.NUMBER) { // check if the number follows
				double txtFieldSize = lexer.getValue(); // save number as a TextField Size
				try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
				if (token == token.SEMICOLON) { // check if semicolon follows
					// cast double received from the lexical analyzer into integer type
					container.add(new JTextField((int)txtFieldSize)); // add the TextField to the container
					return true;
				}
			}
		}
		else if (token == token.GROUP) { // case widget "Group" for Radio Buttons
			ButtonGroup group = new ButtonGroup(); // to group the buttons
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			try {
				if (parseRadioButtons(group, container)) {
					if (token == token.END) { // check if word "End" follows
						token = lexer.getNextToken();
						if (token == token.SEMICOLON) { // check if semicolon follows
							return true;
						}
					}
				}
			} catch (SyntaxError e) {
				System.out.println(e);
			}
		}
		else if (token == token.PANEL) { // case widget "Panel"
			JPanel panel = new JPanel(); // initialize a new panel
			try {
				if (parseLayout(panel)) {
					if (parseWidgets(panel)) {
						if (token == token.END) { // check if the word "End" follows
							token = lexer.getNextToken();
							if (token == token.SEMICOLON) { // check if semicolon follows
								container.add(panel); // add panel to the container
								return true;
							}
						}
					}
				}
			} catch (SyntaxError e) {
				System.out.println(e);
			}
		}
		return false;
	}
	
	// attempts to parse one or more radio buttons, and add them to the referenced container
	// with the use of recursive call, and a method for parsing one radio button
	static boolean parseRadioButtons(ButtonGroup group, Container container) throws IOException {
		if (parseRadioButton(group, container)) { // check if can parse one radio button
			if (parseRadioButtons(group, container)) { // check if there are more radio buttons to parse
				return true;
			}
			return true;
		}
		return false; // base case
	}

	// attempts to parse one radio button and add it to the referenced container
	static boolean parseRadioButton(ButtonGroup group, Container container) throws IOException {
		if (token == token.RADIO) { // check if the token reads "Radio"
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			if (token == token.STRING) { // check if the name follows
				String radioButtonName = lexer.getLexeme(); // save the name of the radio button
				try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
				if (token == token.SEMICOLON) { // check if semicolon follows
					try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
					JRadioButton radioButton = new JRadioButton(radioButtonName); // create a new radio button
					group.add(radioButton); // add the button to the current group
					container.add(radioButton); // add the button to the referenced container
					return true;
				}
			}
		}
		return false;
	}

	// attempts to parse layout, and assign it to the referenced container
	static boolean parseLayout(Container container) throws IOException {
		try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
		if (token == token.LAYOUT) { // check if the token reads "Layout"
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			try {
				if (parseLayoutType(container)) { // attempt to detect layout type
					token = lexer.getNextToken();
					if (token == token.COLON) { // check if colon follows
						return true;
					}
				}
			} catch (SyntaxError e) {
				System.out.println(e);
			}
		}
		return false;
	}

	// detecting the type of the layout: FLOW or GRID
	// GRID layout requires additional declarations by the user
	static boolean parseLayoutType(Container container) throws IOException {
		if (token == token.FLOW) { // if the layout is declared as "Flow"
			container.setLayout(new FlowLayout()); // assign the layout to the container
			return true;
		}
		else if (token == token.GRID) { // if the layout is declared as "Grid"
			// Variables to save grid size
			// Type double to receive from lexical analyzer, and later cast to integer
			double gridRowsNum;
			double gridColsNum;
			try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
			if (token == token.LEFT_PAREN) { // check if "(" follows
				try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
				if (token == token.NUMBER) { // check if token is a number
					gridRowsNum = lexer.getValue(); // read and save the value for number of rows
					try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
					if (token == token.COMMA) { // check if "," follows
						try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
						if (token == token.NUMBER) { // check if the token is a number
							gridColsNum = lexer.getValue(); // read and save the number of columns
							try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
							if (token == token.RIGHT_PAREN) { // check if ")" follows
								// cast doubles to integers
								// set a Grid Layout with a new layout object
								container.setLayout(new GridLayout((int)gridRowsNum, (int)gridColsNum));
								return true;
							}
							else if (token == token.COMMA) {
								// Variables for extra details, when user declared four numbers
								// Type double to receive from lexical analyzer, and later cast to integer
								double horGapsNum;
								double verGapsNum;
								try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
								if (token == token.NUMBER) { // check if a number follows
									horGapsNum = lexer.getValue(); // read and save the number of horizontal gaps
									try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
									if (token == token.COMMA) { // check if "," follows
										try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
										if (token == token.NUMBER) { // check if a number follows
											verGapsNum = lexer.getValue(); // read and save the number of vertical gaps
											try { token = lexer.getNextToken(); } catch (SyntaxError e) { System.out.println(e); }
											if (token == token.RIGHT_PAREN) { // check if ")" follows
												// cast doubles to integers
												// set a Grid Layout with a new layout object
												container.setLayout(new GridLayout((int)gridRowsNum, (int)gridColsNum, (int)horGapsNum, (int)verGapsNum));
												return true;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	

}

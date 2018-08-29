# gui_parser
A program that builds GUI from an input file with a pre-defined syntax

## Running locally
Ensure that all projects files are located in the same directory, and an input file is provided. The run the Main class.

## Expected output
The program should parse the keywords like "Button", "Textfield", "Panel" and etc, and arrange them as described in the input file.

The following input file should build a GUI for a common calculator:

    Window "Calculator" (200, 200) Layout Flow:
      Textfield 20;
      Panel Layout Grid(4, 3, 5, 5):
        Button "7";
        Button "8";
        Button "9";
        Button "4";
        Button "5";
        Button "6";
        Button "1";
        Button "2";
        Button "3";
        Label "";
        Button "0";
      End;
    End.

## TODO
- Automatically generate Java syntax code for the described GUI
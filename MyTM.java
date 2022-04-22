import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.Scanner; // Import the Scanner class to read text files

public class MyTM {
    private Scanner input;
    private int stateSize = 0;
    private String alphabet;
    private ArrayList<Character> tape;
    private ArrayList<String> transitions;
    private ArrayList<State> states;
    private State currentState;
    private int currentCharIdx;
    private boolean accepting;

    private int parseStateNum(String data) {
        String[] split = data.split("Number of nonhalting states: ");
        return Integer.parseInt(split[1]);
    }

    private String parseAlphabet(String data) {
        String[] split = data.split("Alphabet: ");
        return split[1];
    }

    private void readFile(String fileName) {
        int lineNum = 0;
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (lineNum == 0) {
                    stateSize = parseStateNum(data) +1;
                } else if (lineNum == 1) {
                    alphabet = parseAlphabet(data);
                } else {
                    transitions.add(data);
                }
                lineNum ++;
            }
            myReader.close();
          } catch (FileNotFoundException e) {
            System.err.println("An error occurred opening the file.");
            e.printStackTrace();
          }
    }

    private void makeStates() {
        for (int i = 0; i < stateSize; ++i) {
            states.add(new State(i));
        }
    }

    private void setCurrentState(int stateNum) {
        for (State aState : states) {
            if (aState.getStateNum() == stateNum) {
                currentState = aState;
            }
        }
    }

    private void populateTransitions() {
        String stateNum;
        String input;
        String outputState;
        String outputSymbol;
        String direction;
        for (String transition : transitions) {
            String[] splitArr = transition.split(" ");
            stateNum = splitArr[0];
            input = splitArr[1];
            outputState = splitArr[3];
            outputSymbol = splitArr[4];
            direction = splitArr[5];

            //find the state that we are currently in
            for(State aState : states) {
                if (aState.StateNum == Integer.parseInt(stateNum)) {
                    aState.setOutPutState(input, outputState);
                    aState.setOutPutSymbol(input, outputSymbol);
                    aState.setDirection(input, direction);
                }
            }
        }
    }

    private void displayConfig() {
        System.out.print(currentState.getStateNum() + ":");
        for (int i = 0; i < tape.size(); ++i) {
            if (i == currentCharIdx) {
                System.out.print("(" + tape.get(i) + ")");
            }
            else {
                System.out.print(tape.get(i));
            }
        }
        System.out.print("\n");
    }

    public class State {
        private HashMap<String, String> outPutState;
        private HashMap<String, String> outPutSymbol;
        private HashMap<String, String> direction;
        private int StateNum;
        //state constructor
        public State(int StateNum) {
            setStateNum(StateNum);
            outPutState = new HashMap<String, String>();
            outPutSymbol = new HashMap<String, String>();
            direction = new HashMap<String, String>();
        }

        public void getDescription() {
            System.out.println("state number: " + this.StateNum);
            System.out.println("state transitions: " + this.outPutState.entrySet().toString());
            System.out.println("symbol changes: " + this.outPutSymbol.entrySet().toString());
            System.out.println("directions: " + this.direction.entrySet().toString());
        }

        public void setOutPutState(String input, String outPutState) {
            this.outPutState.put(input, outPutState);
        }

        public String getOutPutState(String input) {
            return this.outPutState.get(input).toString();
        }

        public void setOutPutSymbol(String input, String outPutSymbol) {
            this.outPutSymbol.put(input, outPutSymbol);
        }

        public String getOutPutSymbol(String input) {
            return this.outPutSymbol.get(input).toString();
        }
        public void setDirection(String input, String direction) {
            this.direction.put(input, direction);
        }

        public String getDirection(String input) {
            return this.direction.get(input).toString();
        }

        public void setStateNum(int StateNum) {
            this.StateNum = StateNum;
        }

        public int getStateNum() {
            return this.StateNum;
        }

        public ArrayList<Character> getInputs() {
            Set<String> keys = outPutState.keySet();
            ArrayList<Character> inputs = new ArrayList<Character>();
            for (String c : keys) {
                inputs.add(c.charAt(0));
            }
            return inputs;
        }
    }


    public static void main(String[] args) {
        if(args.length != 1) {
            System.err.print("Program execution must have exactly 1 parameter: file name");
            System.exit(0);
        }
        MyTM TM = new MyTM();

    while (true) {

        TM.states = new ArrayList<State>();
        TM.transitions = new ArrayList<String>();
        TM.tape = new ArrayList<Character>();
        TM.input = new Scanner(System.in);
        TM.currentCharIdx = 0;
        final String fileName = args[0];
        TM.readFile(fileName);
        TM.makeStates(); //creating all of the state objects and adding them to the states array list
        TM.setCurrentState(0); //declaring the current state to be state 0
        TM.populateTransitions(); // setting all of the transition properties for all states
        
        String tapeString = TM.input.nextLine(); //getting the input string from standard in
        if (tapeString.isEmpty())
            tapeString = "_";

        for (char c : tapeString.toCharArray()) {
            TM.tape.add(c);
        }
        TM.displayConfig(); //show the initial configuration before any changes are made
        while (true) {
            ArrayList<Character> inputs = TM.currentState.getInputs();
            Character inputChar = TM.tape.get(TM.currentCharIdx);
            if (TM.currentState.getStateNum() == TM.stateSize-1) {
                TM.accepting = true;
                break;
            }
            if (!inputs.contains(inputChar)){
                TM.accepting = false;
                break;
            }
            String outputState = TM.currentState.outPutState.get(inputChar.toString());
            String outputSymbol = TM.currentState.outPutSymbol.get(inputChar.toString());
            String direction = TM.currentState.direction.get(inputChar.toString());
            
            TM.tape.set(TM.currentCharIdx, outputSymbol.charAt(0)); // change the symbol at the current position on the tape
            //if tape TM says go left
            if (direction.equals("<")) {
                //if we are at the first character in the tape
                if (TM.currentCharIdx == 0) {
                    TM.tape.add(0,'_');
                }
                else {
                    if (TM.tape.get(TM.currentCharIdx).equals('_'))
                        TM.tape.remove(TM.currentCharIdx);
                    TM.currentCharIdx --;
                }
            }
            //if TM says go right
            else if (direction.equals(">")) {
                //if we are at the last character in the tape
                if (TM.currentCharIdx == TM.tape.size() -1) {
                    TM.tape.add('_');
                    TM.currentCharIdx ++;
                }
                else if (TM.currentCharIdx == 0 && TM.tape.get(TM.currentCharIdx).equals('_')){
                        TM.tape.remove(TM.currentCharIdx);
                }
                else {
                    TM.currentCharIdx ++;
                }
            }
            TM.setCurrentState(Integer.parseInt(outputState)); // moving to the specified state
            TM.displayConfig(); //display the current configuration of TM
        }

        if (TM.accepting) {
            System.out.println("accept");
        }
        else {
            System.out.println("reject");
        }
        }
    }
}
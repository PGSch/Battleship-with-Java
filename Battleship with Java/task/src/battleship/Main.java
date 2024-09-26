package battleship;

import java.sql.SQLOutput;
import java.util.*;

class Board {
    private static final int boardSize = 11;
    public static String[][] boardVisual1 = new String[boardSize][boardSize];
    public static String[][] boardMain1 = new String[boardSize][boardSize];
    public static final String[][] boardCoords = new String[boardSize][boardSize];

    public static String[][] boardVisual2 = new String[boardSize][boardSize];
    public static String[][] boardMain2 = new String[boardSize][boardSize];
    public static final String[][] boardCoords2 = new String[boardSize][boardSize];

    protected Board(){
        initializeBoard1();
        initializeBoard2();
    }

    public static void initializeBoard1() {
        boardVisual1[0][0] = " ";
        boardMain1[0][0] = " ";
        boardCoords[0][0] = " ";

        List<String> capitalAlphabet = misc.capitalAlphabet();
        for (int i = 1; i < boardSize; i++) {
            boardCoords[i][0] = capitalAlphabet.get(i-1);
            boardMain1[i][0] = capitalAlphabet.get(i -1);
            boardVisual1[i][0] = capitalAlphabet.get(i -1);
        }
        for (int i = 1; i < boardSize; i++) {
            boardCoords[0][i] = i + " ";
            boardMain1[0][i] = Integer.toString(i);
            boardVisual1[0][i] = Integer.toString(i);
        }
        for (int i = 1; i < boardSize; i++) {
            for (int j = 1; j < boardSize; j++) {
                boardMain1[i][j] = "~";
                boardVisual1[i][j] = "~";
                boardCoords[i][j] = boardCoords[i][0] + j;
            }
        }
    }

    public static void initializeBoard2() {
        boardCoords[0][0] = " ";
        boardVisual2[0][0] = " ";
        boardMain2[0][0] = " ";

        List<String> capitalAlphabet = misc.capitalAlphabet();
        for (int i = 1; i < boardSize; i++) {
            boardCoords[i][0] = capitalAlphabet.get(i-1);
            boardMain2[i][0] = capitalAlphabet.get(i -1);
            boardVisual2[i][0] = capitalAlphabet.get(i -1);
        }
        for (int i = 1; i < boardSize; i++) {
            boardCoords[0][i] = i + " ";
            boardMain2[0][i] = Integer.toString(i);
            boardVisual2[0][i] = Integer.toString(i);
        }
        for (int i = 1; i < boardSize; i++) {
            for (int j = 1; j < boardSize; j++) {
                boardCoords[i][j] = boardCoords[i][0] + j;
                boardMain2[i][j] = "~";
                boardVisual2[i][j] = "~";
            }
        }
    }

    public static void printBoard1(String layer) {
        if (Objects.equals(layer, "visual")) {
            misc.printArray(boardVisual1);
        } else if (Objects.equals(layer, "main")) {
            misc.printArray(boardMain1);
        } else if (Objects.equals(layer, "coords")) {
            misc.printArray(boardCoords);
        }
    }
    public static void printBoard2(String layer) {
        if (Objects.equals(layer, "visual")) {
            misc.printArray(boardVisual2);
        } else if (Objects.equals(layer, "main")) {
            misc.printArray(boardMain2);
        } else if (Objects.equals(layer, "coords")) {
            misc.printArray(boardCoords);
        }
    }

    public static boolean searchBoardCoords(String input) {
        for (int i = 1; i < boardSize; i++) {
            for (int j = 1; j < boardSize; j++) {
                if (input.equals(boardCoords[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }
   
    public static boolean outOfBounds(int coord) {
        return !(coord < 11);
    }

}

class Ship {
    protected String start;
    protected String end;
    protected String[] placement;
    protected int[][] placementCoord;
    protected int fields;
    protected int maxLength;
    protected String shipName;
    protected String textInit;
    protected int player;

    protected Ship(String shipName, int maxLength, int player) {
        this.player = player;
        this.shipName = shipName;
        this.maxLength = maxLength;
        this.fields = maxLength;
        this.textInit = String.format("%s (%d cells)", shipName, maxLength);
        System.out.println(String.format("Enter the coordinates of the %s:", textInit));
        boolean idx = false;
        while (!idx) {
            Scanner sc = new Scanner(System.in);
            String[] input = sc.nextLine().split(" ");
            if (input.length < 2 && input[0].equals("")){
                idx = true;
            } else {
                start = input[0];
                end = input[1];
                idx = shipPlacement(start, end, this.player);
            }
        }
    }
    protected boolean shipPlacement(String start, String end, int player) {
        if (player == 1){
            if (!(shipOnBoard(start, end))) {
                System.out.println("Error!");
                return false;
            } else if (
                    shipValidHorizontal(start, end) && shipValidHorizontalLength(start, end)
            ) {
                String[] placement = new String[Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1];
                if ((Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1) != maxLength){
                    System.out.println(String.format("Error! Wrong length of the %s! Try again:", textInit));
                }

                String[] splitStart = start.split("");
                String[] splitEnd = end.split("");
                if (misc.realCoords(start)[1] < misc.realCoords(end)[1]){
                    for (int i = 0; i < placement.length; i++) {
                        placement[i] = splitStart[0] + (misc.realCoords(start)[1] + i);
                    }
                } else if (misc.realCoords(start)[1] > misc.realCoords(end)[1]){
                    for (int i = placement.length; i > 0; i--) {
                        placement[i-1] = splitEnd[0] + (misc.realCoords(end)[1] + i - 1);
                    }
                }
                setPlacement(placement);
                setFields(Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1);
                setStart(start);
                setEnd(end);
                placementCoord = new int[this.getLength()][2];
                for (int i = 0; i < placement.length; i++) {
                    placementCoord[i] = misc.realCoords(placement[i]);
                }
                if (!shipClose1(placementCoord, start, end)) {
                    System.out.println("Length: " + (Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1));
                    System.out.print("Parts:");
                    if (misc.realCoords(start)[1] < misc.realCoords(end)[1]){
                        for (int i = 0; i < placement.length; i++) {
                            System.out.print(" " + placement[i]);
                        }
                        System.out.printf("%n");
                    } else if (misc.realCoords(start)[1] > misc.realCoords(end)[1]){
                        for (int i = placement.length; i > 0; i--) {
                            System.out.print(" " + placement[i-1]);
                        }
                    }
                } else {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    return false;
                }
            } else if (
                    shipValidVertical(start, end) && shipValidVerticalLength(start, end)
            ) {
                String[] placement = new String[Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) + 1];
                String[] splitStart = start.split("");
                String[] splitEnd = end.split("");
                if (misc.realCoords(start)[0] < misc.realCoords(end)[0]){
                    for (int i = 0; i < placement.length; i++) {
                        placement[i] = misc.capitalAlphabet().get(misc.realCoords(start)[0] - 1 + i) + misc.realCoords(start)[1];
                    }
                } else if (misc.realCoords(start)[0] > misc.realCoords(end)[0]){
                    for (int i = placement.length; i > 0; i--) {
                        placement[i - 1] = misc.capitalAlphabet().get(misc.realCoords(end)[0] - 2 + i) + misc.realCoords(start)[1];
                    }
                }
                setPlacement(placement);
                setFields(Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) + 1);
                setStart(start);
                setEnd(end);

                placementCoord = new int[placement.length][2];
                for (int i = 0; i < placement.length; i++) {
                    placementCoord[i] = misc.realCoords(placement[i]);
                }
                if (!shipClose1(placementCoord, start, end)) {
                    System.out.println("Length: " + (Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) + 1));
                    System.out.print("Parts:");
                    if (misc.realCoords(start)[0] < misc.realCoords(end)[0]){
                        for (int i = 0; i < placement.length; i++) {
                            System.out.print(" " + placement[i]);
                        }
                    } else if (misc.realCoords(start)[0] > misc.realCoords(end)[0]){
                        for (int i = placement.length; i > 0; i--) {
                            System.out.print(" " + placement[i - 1]);
                        }
                    }
                } else {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    return false;
                }

            } else if (!(shipValidVerticalLength(start, end) || shipValidHorizontalLength(start, end))) {
                System.out.println(String.format("Error! Wrong length of %s! Try again:", textInit));
                return false;
            } else {
                System.out.println("Error! Wrong ship location! Try again:");
                System.out.println("Something in shipPlacement went wrong :(");
                return false;
            }
            for (int i = 0; i < placement.length; i++) {
                Board.boardMain1[placementCoord[i][0]][placementCoord[i][1]] = "O";
            }
            System.out.println();
            Board.printBoard1("main");
            return true;
        } else if (player == 2){
            if (!(shipOnBoard(start, end))) {
                System.out.println("Error!");
                return false;
            } else if (
                    shipValidHorizontal(start, end) && shipValidHorizontalLength(start, end)
            ) {
                String[] placement = new String[Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1];
                if ((Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1) != maxLength){
                    System.out.println(String.format("Error! Wrong length of the %s! Try again:", textInit));
                }

                String[] splitStart = start.split("");
                String[] splitEnd = end.split("");
                if (misc.realCoords(start)[1] < misc.realCoords(end)[1]){
                    for (int i = 0; i < placement.length; i++) {
                        placement[i] = splitStart[0] + (misc.realCoords(start)[1] + i);
                    }
                } else if (misc.realCoords(start)[1] > misc.realCoords(end)[1]){
                    for (int i = placement.length; i > 0; i--) {
                        placement[i-1] = splitEnd[0] + (misc.realCoords(end)[1] + i - 1);
                    }
                }
                setPlacement(placement);
                setFields(Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1);
                setStart(start);
                setEnd(end);
                placementCoord = new int[this.getLength()][2];
                for (int i = 0; i < placement.length; i++) {
                    placementCoord[i] = misc.realCoords(placement[i]);
                }
                if (!shipClose2(placementCoord, start, end)) {
                    System.out.println("Length: " + (Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) + 1));
                    System.out.print("Parts:");
                    if (misc.realCoords(start)[1] < misc.realCoords(end)[1]){
                        for (int i = 0; i < placement.length; i++) {
                            System.out.print(" " + placement[i]);
                        }
                        System.out.printf("%n");
                    } else if (misc.realCoords(start)[1] > misc.realCoords(end)[1]){
                        for (int i = placement.length; i > 0; i--) {
                            System.out.print(" " + placement[i-1]);
                        }
                    }
                } else {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    return false;
                }
            } else if (
                    shipValidVertical(start, end) && shipValidVerticalLength(start, end)
            ) {
                String[] placement = new String[Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) + 1];
                String[] splitStart = start.split("");
                String[] splitEnd = end.split("");
                if (misc.realCoords(start)[0] < misc.realCoords(end)[0]){
                    for (int i = 0; i < placement.length; i++) {
                        placement[i] = misc.capitalAlphabet().get(misc.realCoords(start)[0] - 1 + i) + misc.realCoords(start)[1];
                    }
                } else if (misc.realCoords(start)[0] > misc.realCoords(end)[0]){
                    for (int i = placement.length; i > 0; i--) {
                        placement[i - 1] = misc.capitalAlphabet().get(misc.realCoords(end)[0] - 2 + i) + misc.realCoords(start)[1];
                    }
                }
                setPlacement(placement);
                setFields(Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) + 1);
                setStart(start);
                setEnd(end);

                placementCoord = new int[placement.length][2];
                for (int i = 0; i < placement.length; i++) {
                    placementCoord[i] = misc.realCoords(placement[i]);
                }
                if (!shipClose2(placementCoord, start, end)) {
                    System.out.println("Length: " + (Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) + 1));
                    System.out.print("Parts:");
                    if (misc.realCoords(start)[0] < misc.realCoords(end)[0]){
                        for (int i = 0; i < placement.length; i++) {
                            System.out.print(" " + placement[i]);
                        }
                    } else if (misc.realCoords(start)[0] > misc.realCoords(end)[0]){
                        for (int i = placement.length; i > 0; i--) {
                            System.out.print(" " + placement[i - 1]);
                        }
                    }
                } else {
                    System.out.println("Error! You placed it too close to another one. Try again:");
                    return false;
                }

            } else if (!(shipValidVerticalLength(start, end) || shipValidHorizontalLength(start, end))) {
                System.out.println(String.format("Error! Wrong length of %s! Try again:", textInit));
                return false;
            } else {
                System.out.println("Error! Wrong ship location! Try again:");
                System.out.println("Something in shipPlacement went wrong :(");
                return false;
            }
            for (int i = 0; i < placement.length; i++) {
                Board.boardMain2[placementCoord[i][0]][placementCoord[i][1]] = "O";
            }
            System.out.println();
            Board.printBoard2("main");
            return true;
        }
        return true;
    }

    public void setPlacementCoord(int[][] placementCoord) {
        this.placementCoord = placementCoord;
    }

    public int[][] getPlacementCoord() {
        return placementCoord;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public void setPlacement(String[] placement) {
        this.placement = placement;
    }

    public void setFields(int fields) {
        this.fields = fields;
    }

    public String getStart() {
        return start;
    }

    public String getEnd() {
        return end;
    }

    public String[] getPlacement() {
        return placement;
    }

    public int getLength() {
        return fields;
    }
    
    protected boolean shipClose1(int[][] placementCoord, String start, String end) {
        int blocked = 0;
        if (shipValidHorizontal(start, end)){
            for (int i = 0; i < placementCoord.length; i++) {
                if (!(Board.outOfBounds(placementCoord[i][0]-1) || Board.outOfBounds(placementCoord[i][1]))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[i][0]-1][placementCoord[i][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[i][0]+1) || Board.outOfBounds(placementCoord[i][1]))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[i][0]+1][placementCoord[i][1]], "O") ? 1 : 0;
                }
                blocked += Objects.equals(Board.boardMain1[placementCoord[i][0]][placementCoord[i][1]], "O") ? 1 : 0;
            }
            if (
                    placementCoord[0][1] > placementCoord[placementCoord.length-1][1]
            ) {
                if (!(Board.outOfBounds(placementCoord[0][0]) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0]][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] - 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] + 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0]) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0]][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] - 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
            } else if (
                    placementCoord[0][1] < placementCoord[placementCoord.length-1][1]
            ) {
                if (!(Board.outOfBounds(placementCoord[0][0]) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0]][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] - 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0]) || Board.outOfBounds(placementCoord[placementCoord.length-1][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length-1][0]][placementCoord[placementCoord.length-1][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length-1][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length-1][0] - 1][placementCoord[placementCoord.length-1][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] + 1) || Board.outOfBounds(placementCoord[placementCoord.length-1][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length-1][0] + 1][placementCoord[placementCoord.length-1][1] - 1], "O") ? 1 : 0;
                }
            }
        } else if (shipValidVertical(start, end)) {
            for (int i = 0; i < placementCoord.length; i++) {
                blocked += Objects.equals(Board.boardMain1[placementCoord[i][0]][placementCoord[i][1]], "O") ? 1 : 0;
                if (!(Board.outOfBounds(placementCoord[i][0]) || Board.outOfBounds(placementCoord[i][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[i][0]][placementCoord[i][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[i][0]) || Board.outOfBounds(placementCoord[i][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[i][0]][placementCoord[i][1] + 1], "O") ? 1 : 0;
                }
            }
            if (
                    placementCoord[0][0] > placementCoord[placementCoord.length-1][0]
            ) {
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1]))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] + 1][placementCoord[0][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] + 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length - 1][1]))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length - 1][0] - 1][placementCoord[placementCoord.length - 1][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length - 1][1] - 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length - 1][0] - 1][placementCoord[placementCoord.length - 1][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length - 1][1] + 1))){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length - 1][0] - 1][placementCoord[placementCoord.length - 1][1] + 1], "O") ? 1 : 0;
                }
            } else if (
                    placementCoord[0][0] < placementCoord[placementCoord.length-1][0]
            ) {
                if (!Board.outOfBounds(placementCoord[0][0] - 1)){
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] - 1][placementCoord[0][1]], "O") ? 1 : 0;
                }

                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] + 1))) {
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] - 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] - 1))) {
                    blocked += Objects.equals(Board.boardMain1[placementCoord[0][0] - 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] + 1))) {
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length-1][0] + 1][placementCoord[0][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] + 1) || Board.outOfBounds(placementCoord[0][1] + 1))) {
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length-1][0] + 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))) {
                    blocked += Objects.equals(Board.boardMain1[placementCoord[placementCoord.length - 1][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
            }
        }
        return blocked > 0;
    }
    protected boolean shipClose2(int[][] placementCoord, String start, String end) {
        int blocked = 0;
        if (shipValidHorizontal(start, end)){
            for (int i = 0; i < placementCoord.length; i++) {
                if (!(Board.outOfBounds(placementCoord[i][0]-1) || Board.outOfBounds(placementCoord[i][1]))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[i][0]-1][placementCoord[i][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[i][0]+1) || Board.outOfBounds(placementCoord[i][1]))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[i][0]+1][placementCoord[i][1]], "O") ? 1 : 0;
                }
                blocked += Objects.equals(Board.boardMain2[placementCoord[i][0]][placementCoord[i][1]], "O") ? 1 : 0;
            }
            if (
                    placementCoord[0][1] > placementCoord[placementCoord.length-1][1]
            ) {
                if (!(Board.outOfBounds(placementCoord[0][0]) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0]][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] - 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] + 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0]) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0]][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] - 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
            } else if (
                    placementCoord[0][1] < placementCoord[placementCoord.length-1][1]
            ) {
                if (!(Board.outOfBounds(placementCoord[0][0]) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0]][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] - 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0]) || Board.outOfBounds(placementCoord[placementCoord.length-1][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length-1][0]][placementCoord[placementCoord.length-1][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length-1][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length-1][0] - 1][placementCoord[placementCoord.length-1][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] + 1) || Board.outOfBounds(placementCoord[placementCoord.length-1][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length-1][0] + 1][placementCoord[placementCoord.length-1][1] - 1], "O") ? 1 : 0;
                }
            }
        } else if (shipValidVertical(start, end)) {
            for (int i = 0; i < placementCoord.length; i++) {
                blocked += Objects.equals(Board.boardMain2[placementCoord[i][0]][placementCoord[i][1]], "O") ? 1 : 0;
                if (!(Board.outOfBounds(placementCoord[i][0]) || Board.outOfBounds(placementCoord[i][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[i][0]][placementCoord[i][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[i][0]) || Board.outOfBounds(placementCoord[i][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[i][0]][placementCoord[i][1] + 1], "O") ? 1 : 0;
                }
            }
            if (
                    placementCoord[0][0] > placementCoord[placementCoord.length-1][0]
            ) {
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1]))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] + 1][placementCoord[0][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] + 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length - 1][1]))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length - 1][0] - 1][placementCoord[placementCoord.length - 1][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length - 1][1] - 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length - 1][0] - 1][placementCoord[placementCoord.length - 1][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] - 1) || Board.outOfBounds(placementCoord[placementCoord.length - 1][1] + 1))){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length - 1][0] - 1][placementCoord[placementCoord.length - 1][1] + 1], "O") ? 1 : 0;
                }
            } else if (
                    placementCoord[0][0] < placementCoord[placementCoord.length-1][0]
            ) {
                if (!Board.outOfBounds(placementCoord[0][0] - 1)){
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] - 1][placementCoord[0][1]], "O") ? 1 : 0;
                }

                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] + 1))) {
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] - 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[0][0] - 1) || Board.outOfBounds(placementCoord[0][1] - 1))) {
                    blocked += Objects.equals(Board.boardMain2[placementCoord[0][0] - 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] + 1))) {
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length-1][0] + 1][placementCoord[0][1]], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length-1][0] + 1) || Board.outOfBounds(placementCoord[0][1] + 1))) {
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length-1][0] + 1][placementCoord[0][1] + 1], "O") ? 1 : 0;
                }
                if (!(Board.outOfBounds(placementCoord[placementCoord.length - 1][0] + 1) || Board.outOfBounds(placementCoord[0][1] - 1))) {
                    blocked += Objects.equals(Board.boardMain2[placementCoord[placementCoord.length - 1][0] + 1][placementCoord[0][1] - 1], "O") ? 1 : 0;
                }
            }
        }
        return blocked > 0;
    }
    protected boolean shipOnBoard(String start, String end) {
        return Board.searchBoardCoords(start) && Board.searchBoardCoords(end);
    }
    protected boolean shipValidHorizontalLength (String start, String end) {
        return (Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) < 5)
                && (Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) > 0);
    }
    protected boolean shipValidVerticalLength (String start, String end) {
        return (Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) < 5)
                && (Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) > 0);
    }
        protected boolean shipValidHorizontal (String start, String end) {
        return (misc.realCoords(start)[0] == misc.realCoords(end)[0]);
    }
    protected boolean shipValidVertical (String start, String end) {
        return (misc.realCoords(start)[1] == misc.realCoords(end)[1]);
    }
}

class SpecificShip extends Ship {
    protected SpecificShip(String shipName, int maxLength, int player){
        super(shipName, maxLength, player);
    }

    @Override
    protected boolean shipValidHorizontalLength (String start, String end) {
        return (Math.abs(misc.realCoords(start)[1] - misc.realCoords(end)[1]) == maxLength - 1);
    }

    @Override
    protected boolean shipValidVerticalLength (String start, String end) {
        return (Math.abs(misc.realCoords(start)[0] - misc.realCoords(end)[0]) == maxLength - 1);
    }

    @Override
    protected boolean shipValidHorizontal (String start, String end) {
        return (misc.realCoords(start)[0] == misc.realCoords(end)[0]);
    }

    @Override
    protected boolean shipValidVertical (String start, String end) {
        return (misc.realCoords(start)[1] == misc.realCoords(end)[1]);
    }
}

class Shooting {
    SpecificShip ac1;
    SpecificShip bs1;
    SpecificShip sm1;
    SpecificShip cs1;
    SpecificShip ds1;
    SpecificShip ac2;
    SpecificShip bs2;
    SpecificShip sm2;
    SpecificShip cs2;
    SpecificShip ds2;
    static SpecificShip[] all1;
    static SpecificShip[] all2;
    protected Shooting(
            SpecificShip ac1,
            SpecificShip bs1,
            SpecificShip sm1,
            SpecificShip cs1,
            SpecificShip ds1,
            SpecificShip ac2,
            SpecificShip bs2,
            SpecificShip sm2,
            SpecificShip cs2,
            SpecificShip ds2
    ) {
        this.ac1 = ac1;
        this.bs1 = bs1;
        this.sm1 = sm1;
        this.cs1 = cs1;
        this.ds1 = ds1;
        this.ac2 = ac2;
        this.bs2 = bs2;
        this.sm2 = sm2;
        this.cs2 = cs2;
        this.ds2 = ds2;
        all1 = new SpecificShip[]{
                ac1,
                bs1,
                sm1,
                cs1,
                ds1
        };
        all2 = new SpecificShip[]{
                ac2,
                bs2,
                sm2,
                cs2,
                ds2
        };
    }
    public static void start() {
        System.out.println("Take a shot!");
    }
    public static void shoot() {
        int player = 1;
        boolean end = false;
        boolean win = false;
        while (!win) {
            if (player == 1){
                System.out.println("Player 1, it's your turn:");
                while (!end){
                    Scanner sc = new Scanner(System.in);
                    String coord = sc.nextLine();
                    if (Board.searchBoardCoords(coord)) {
                        if (
                                Objects.equals(Board.boardMain2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]], "O")
                                        || Objects.equals(Board.boardMain2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]], "X")
                        ){
                            Board.boardVisual1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "X";
                            Board.boardMain2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "X";
                            System.out.println("You hit a ship!");
                            sankShip1(coord);
                            gameOver1();
                            if (!(gameOver1())) {
                                misc.endRound();
                                Board.printBoard2("visual");
                                System.out.println("---------------------");
                                Board.printBoard2("main");
                            }
                        } else if (
                                Objects.equals(Board.boardMain2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]], "~")
                        ) {
                            Board.boardVisual1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "M";
                            Board.boardMain2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "M";
                            System.out.println("You missed!");
                            misc.endRound();
                            Board.printBoard2("visual");
                            System.out.println("---------------------");
                            Board.printBoard2("main");
                        } else {
                            System.out.println("Error, shot missed playing field!");
                        }
                        end = true;
                        player = 2;
                    } else {
                        System.out.println("Error, shot missed board!");
                    }
                }
                end = false;
                win = gameOver1();
            } else if (player == 2){
                System.out.println("Player 2, it's your turn:");
                while (!end){
                    Scanner sc = new Scanner(System.in);
                    String coord = sc.nextLine();
                    if (Board.searchBoardCoords(coord)) {
                        if (
                                Objects.equals(Board.boardMain1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]], "O")
                                        || Objects.equals(Board.boardMain1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]], "X")
                        ){
                            Board.boardVisual2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "X";
                            Board.boardMain1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "X";
                            System.out.println("You hit a ship!");
                            sankShip1(coord);
                            gameOver2();
                            if (!(gameOver2())) {
                                misc.endRound();
                                Board.printBoard1("visual");
                                System.out.println("---------------------");
                                Board.printBoard1("main");
                            }
                        } else if (
                                Objects.equals(Board.boardMain1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]], "~")
                        ) {
                            Board.boardMain1[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "M";
                            Board.boardVisual2[misc.realCoords(coord)[0]][misc.realCoords(coord)[1]] = "M";
                            System.out.println("You missed!");
                            misc.endRound();
                            Board.printBoard1("visual");
                            System.out.println("---------------------");
                            Board.printBoard1("main");
                        } else {
                            System.out.println("Error, shot missed playing field!");
                        }
                        end = true;
                        player = 1;
                    } else {
                        System.out.println("Error, shot missed board!");
                    }
                }
                end = false;
                win = gameOver2();
            }
        }
    }
    public static boolean sankShip1(String coord) {
        for (int i = 0; i < all1.length; i++) {
            for (int j = 0; j < all1[i].maxLength; j++) {
                for (int k = 0; k < all1[i].placement.length; k++) {
                    if (!(all1[i].fields == 0)) {
                        if (all1[i].placement[k].equals(coord)) {
                            all1[i].placement[k] = "VV";
                            all1[i].fields -= 1;
                            System.out.println("After:");
                            System.out.println(all1[i].textInit + " --> " + all1[i].fields);
                            System.out.println(Arrays.toString(all1[i].placement));
                            if (all1[i].fields == 0){
                                System.out.println("You sank a ship!");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    public static boolean sankShip2(String coord) {
        for (int i = 0; i < all2.length; i++) {
            for (int j = 0; j < all2[i].maxLength; j++) {
                for (int k = 0; k < all2[i].placement.length; k++) {
                    if (!(all2[i].fields == 0)) {
                        if (all2[i].placement[k].equals(coord)) {
                            all2[i].placement[k] = "VV";
                            all2[i].fields -= 1;
                            System.out.println("After:");
                            System.out.println(all2[i].textInit + " --> " + all2[i].fields);
                            System.out.println(Arrays.toString(all2[i].placement));
                            if (all2[i].fields == 0){
                                System.out.println("You sank a ship!");
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    public static boolean gameOver2() {
        int tmp = 0;
        for (int i = 0; i < Board.boardMain1.length; i++) {
            for (int j = 0; j < Board.boardMain1.length; j++) {
                tmp += Board.boardMain1[i][j].equals("O") ? 1 : 0;
            }
        }
        if (tmp == 0){
        System.out.println("You sank the last ship. You won. Congratulations!");
        }
        return tmp == 0;
    }
    public static boolean gameOver1() {
        int tmp = 0;
        for (int i = 0; i < Board.boardMain2.length; i++) {
            for (int j = 0; j < Board.boardMain2.length; j++) {
                tmp += Board.boardMain2[i][j].equals("O") ? 1 : 0;
            }
        }
        if (tmp == 0){
            System.out.println("You sank the last ship. You won. Congratulations!");
        }
        return tmp == 0;
    }
}

class misc {
    public static void printArray(boolean[][] array){
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.printf("%n");
        }
    }
    public static void printArray(char[][] array){
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.printf("%n");
        }
    }
    public static void printArray(String[][] array){
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.printf("%n");
        }
    }

    public static List<String> capitalAlphabet() {
        List<String> capitalAlphabet = new ArrayList<>();

        for (char ch = 'A'; ch <= 'Z'; ch++) {
            capitalAlphabet.add("" + ch);
        }
        return capitalAlphabet;
    }

    public static int[] realCoords(String coords){
        List<String> capitalAlphabet = capitalAlphabet();
        String[] split = coords.split("");
        if (split.length == 3) {
            return new int[]{
                    capitalAlphabet.indexOf(split[0]) + 1,
                    Integer.parseInt(split[1]+split[2])
            };
        } else if (split.length == 2) {
            return new int[]{
                    capitalAlphabet.indexOf(split[0]) + 1,
                    Integer.parseInt(split[1])
            };
        } else { return new int[]{0,0};}
    }
    public static void endRound() {
        System.out.println("Press Enter and pass the move to another player");
        boolean enter = false;
        while (!enter){
            Scanner sc = new Scanner(System.in);
            String input = sc.nextLine();
            enter = input.isEmpty();
        }
    }
}

public class Main {

    public static void main(String[] args) {
        Board.initializeBoard1();
//        Board.printBoard1("coords");
        System.out.println("Player 1, place your ships to the game field");
        Board.printBoard1("main");
        SpecificShip ac1 = new SpecificShip("Aircraft Carrier", 5, 1);
        SpecificShip bs1 = new SpecificShip("Battleship", 4, 1);
        SpecificShip sm1 = new SpecificShip("Submarine", 3, 1);
        SpecificShip cs1 = new SpecificShip("Cruiser", 3, 1);
        SpecificShip ds1 = new SpecificShip("Destroyer", 2, 1);
        misc.endRound();

        Board.initializeBoard2();
        System.out.println("Player 2, place your ships to the game field");
        Board.printBoard2("main");
        SpecificShip ac2 = new SpecificShip("Aircraft Carrier", 5, 2);
        SpecificShip bs2 = new SpecificShip("Battleship", 4, 2);
        SpecificShip sm2 = new SpecificShip("Submarine", 3, 2);
        SpecificShip cs2 = new SpecificShip("Cruiser", 3, 2);
        SpecificShip ds2 = new SpecificShip("Destroyer", 2, 2);
        misc.endRound();

        System.out.println("The game starts!");
        Board.printBoard1("visual");
        System.out.println("---------------------");
        Board.printBoard1("main");
        Shooting game = new Shooting(
                ac1,
                bs1,
                sm1,
                cs1,
                ds1,
                ac2,
                bs2,
                sm2,
                cs2,
                ds2
        );
        Shooting.shoot();
    }
}

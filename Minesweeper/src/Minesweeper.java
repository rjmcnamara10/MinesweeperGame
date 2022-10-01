import tester.Tester;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import javalib.impworld.*;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;

import javalib.worldimages.*;

// represents a cell or lack thereof
abstract class ACell {
  ACell left;
  ACell top;
  ACell right;
  ACell bottom;
  boolean mine;
 
  ACell(ACell left, ACell top, ACell right, ACell bottom, boolean mine) {
    this.left = left;
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.mine = mine;
  }
 
  // EFFECT: Makes the given cell the left of this cell
  void makeLeft(ACell other) {
    this.left = other;
  }

  // EFFECT: Makes the given cell the top of this cell
  void makeTop(ACell other) {
    this.top = other;
  }

  // EFFECT: Makes the given cell the right of this cell
  void makeRight(ACell other) {
    this.right = other;
  }

  // EFFECT: Makes the given cell the bottom of this cell
  void makeBottom(ACell other) {
    this.bottom = other;
  }
  
  // Returns true if this ACell is a mine
  abstract boolean isMine();
  
  // Attempts to spread to the touching cells 
  void attemptSpread() {
    // default is do nothing
  }
}

// represents an empty position
class NoCell extends ACell {
 
  NoCell(ACell left, ACell top, ACell right, ACell bottom) {
    super(left, top, right, bottom, false);
  }
 
  NoCell(NoCell extra) {
    super(extra, null, extra, null, false);
  }
  
  NoCell() {
    super(null, null, null, null, false);
  }

  // A NoCell is not a mine
  boolean isMine() {
    return false;
  }
}

// Represents a single square of the game area
class Cell extends ACell {
  int x;
  int y;
  int col;
  int row;
  int mineContacts;
  boolean mine;
  boolean flagged;
  boolean clicked;
  Color color;
 
  // constructor to initialize other cells to null
  Cell(int x, int y, int col, int row) {
    super(null, null, null, null, false);
    this.x = x;
    this.y = y;
    this.col = col;
    this.row = row;
    this.mineContacts = 0;
    this.flagged = false;
    this.clicked = false;
    this.color = this.initColor();
  }
  
  // sets the initial color for a cell
  Color initColor() {
    if (((this.col + this.row) % 2) == 0) {
      return new Color(165, 230, 80, 255);
    }
    else {
      return new Color(150, 215, 75, 255);
    }
  }
  
  // sets the revealed color for a cell
  Color revealColor() {
    if (((this.col + this.row) % 2) == 0) {
      return new Color(245, 235, 200, 255);
    }
    else {
      return new Color(235, 225, 185, 255);
    }
  }
 
  // draws a cell
  WorldImage drawCell() {
    WorldImage mineImage = new CircleImage((MinesweepWorld.CELL_SIZE / 4), 
        OutlineMode.SOLID, new Color(200, 10, 10));
    
    if (this.clicked) {
      if (this.mine) {
        return new OverlayImage(mineImage,
            new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
            OutlineMode.SOLID, this.color));
      }
      else {
        return new OverlayImage(new TextImage(Integer.toString(this.mineContacts), 
            MinesweepWorld.FONT_SIZE, FontStyle.BOLD, new Utils().getColor(this.mineContacts)),
            new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
            OutlineMode.SOLID, this.color));
      }
    }
    else {
      WorldImage emptyCell = new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
          OutlineMode.SOLID, this.color);
      if (this.flagged) {
        return new OverlayImage(MinesweepWorld.FLAG, emptyCell);
      }
      else {
        return emptyCell;
      }
    }
  }
  
  // EFFECT: changes this cell's mineContact field based on how many mines it is touching
  void minesInContact() {
    int total = 0;
    if (this.top.isMine()) {
      total = total + 1;
    }
    if (this.right.isMine()) {
      total = total + 1;
    }
    if (this.bottom.isMine()) {
      total = total + 1;
    }
    if (this.left.isMine()) {
      total = total + 1;
    }
    if (this.top.left.isMine()) {
      total = total + 1;
    }
    if (this.top.right.isMine()) {
      total = total + 1;
    }
    if (this.bottom.left.isMine()) {
      total = total + 1;
    }
    if (this.bottom.right.isMine()) {
      total = total + 1;
    }
    this.mineContacts = total;
  }

  boolean isMine() {
    return (this.mine);
  }
  
  void attemptSpread() {
    if (this.clicked || this.flagged) {
      // do nothing, already clicked
    }
    else {
      this.clicked = true;
      this.color = this.revealColor();
      if (this.mineContacts == 0) {
        this.top.attemptSpread();
        this.right.attemptSpread();
        this.bottom.attemptSpread();
        this.left.attemptSpread();
        this.top.right.attemptSpread();
        this.top.left.attemptSpread();
        this.bottom.right.attemptSpread();
        this.bottom.left.attemptSpread();
      }
    }
  }
}

class Utils {
  Utils() {}
  
  Color getColor(int contacts) {
    if (contacts == 1) {
      return new Color(80, 160, 230, 255);
    }
    else if (contacts == 2) {
      return new Color(50, 190, 40, 255);
    }
    else if (contacts == 3) {
      return new Color(215, 35, 35, 255);
    }
    else if (contacts == 4) {
      return new Color(95, 5, 190, 255);
    }
    else if (contacts == 5) {
      return new Color(255, 215, 10, 255);
    }
    else if (contacts == 6) {
      return new Color(5, 200, 200, 255);
    }
    else if (contacts == 7) {
      return new Color(250, 135, 210, 255);
    }
    else if (contacts == 8) {
      return new Color(80, 225, 140, 255);
    }
    else {
      return new Color(0, 0, 0, 0);
    }
  }
  
  // Gets the image for a flag
  WorldImage flagImage() {
    WorldImage flag = new CropImage(0, 0, (MinesweepWorld.CELL_SIZE / 4), 
        (MinesweepWorld.CELL_SIZE / 8),
        new CircleImage((MinesweepWorld.CELL_SIZE / 8), 
            OutlineMode.SOLID, new Color(200, 10, 10)));
    WorldImage pole = new RectangleImage(3, (MinesweepWorld.CELL_SIZE / 2), 
        OutlineMode.SOLID, new Color(200, 10, 10));
    flag = new OverlayOffsetImage(pole, -0.5, (MinesweepWorld.CELL_SIZE / 4), flag);
    WorldImage tri = new EquilateralTriangleImage((MinesweepWorld.CELL_SIZE / 3), 
        OutlineMode.SOLID, new Color(200, 10, 10));
    tri = new RotateImage(tri, 90.0);
    flag = new OverlayOffsetImage(tri, -(MinesweepWorld.CELL_SIZE / 8), 
        (MinesweepWorld.CELL_SIZE / 8), flag);
    return flag;
  }
  
  // Gets the image for a clock
  WorldImage clockImage() {
    WorldImage clockBase = new CircleImage((MinesweepWorld.CELL_SIZE / 2) - 3, 
        OutlineMode.SOLID, new Color(255, 230, 60, 255));
    WorldImage hand1 = new RectangleImage(2, MinesweepWorld.CELL_SIZE / 4, 
        OutlineMode.SOLID, Color.black);
    WorldImage hand2 = new RectangleImage(MinesweepWorld.CELL_SIZE / 5, 2, 
        OutlineMode.SOLID, Color.black);
    WorldImage hands = new OverlayOffsetImage(hand1, (MinesweepWorld.CELL_SIZE / 10),
        (MinesweepWorld.CELL_SIZE / 8), hand2);
    WorldImage face = new OverlayOffsetImage(hands, -(MinesweepWorld.CELL_SIZE / 10),
        (MinesweepWorld.CELL_SIZE / 8) - 2, new CircleImage(MinesweepWorld.CELL_SIZE / 3, 
        OutlineMode.SOLID, Color.white));
    clockBase = new OverlayImage(face, clockBase);
    WorldImage buttonTop = new EllipseImage((MinesweepWorld.CELL_SIZE / 3), 
        (MinesweepWorld.CELL_SIZE / 6), OutlineMode.SOLID, new Color(255, 230, 60, 255));
    WorldImage pole = new RectangleImage((MinesweepWorld.CELL_SIZE / 8),
        (MinesweepWorld.CELL_SIZE / 2), OutlineMode.SOLID, new Color(255, 230, 60, 255));
    WorldImage button = new OverlayOffsetImage(buttonTop, 0, 
        (MinesweepWorld.CELL_SIZE / 4), pole);
    WorldImage clock = new OverlayOffsetImage(clockBase, 0, 
        -(MinesweepWorld.CELL_SIZE / 3), button);
    return clock;
  }
  
  // Gets the image for the restart button
  WorldImage restartImage() {
    WorldImage outline = new CircleImage(MinesweepWorld.CELL_SIZE / 2, 
        OutlineMode.SOLID, Color.white);
    WorldImage center = new CircleImage(MinesweepWorld.CELL_SIZE / 3, 
        OutlineMode.SOLID, new Color(80, 155, 0, 255));
    WorldImage cutoutBase = new CircleImage(MinesweepWorld.CELL_SIZE / 2, 
        OutlineMode.SOLID, new Color(80, 155, 0, 255));
    WorldImage cutout = new CropImage(MinesweepWorld.CELL_SIZE / 2, 
        MinesweepWorld.CELL_SIZE / 2, MinesweepWorld.CELL_SIZE, 
        MinesweepWorld.CELL_SIZE, cutoutBase);
    WorldImage circleBase = new OverlayImage(center, outline);
    WorldImage circle = new OverlayOffsetImage(cutout, (-MinesweepWorld.CELL_SIZE / 2) - 2,
        (-MinesweepWorld.CELL_SIZE / 2) - 2, circleBase);
    WorldImage arrowHead = new TriangleImage(new Posn(0, 0), 
        new Posn((MinesweepWorld.CELL_SIZE / 2), 0), 
        new Posn((MinesweepWorld.CELL_SIZE / 4), (MinesweepWorld.CELL_SIZE / 4)),
        OutlineMode.SOLID, Color.white);
    WorldImage button = new OverlayOffsetImage(arrowHead, -(MinesweepWorld.CELL_SIZE / 6),
        (MinesweepWorld.CELL_SIZE / 6), circle);
    WorldImage buttonRotated = new RotateImage(button, 45);
    buttonRotated = new CropImage((MinesweepWorld.CELL_SIZE / 5), 0, 
        (MinesweepWorld.CELL_SIZE + 2), ((6 * MinesweepWorld.CELL_SIZE) / 5),
        buttonRotated);
    return buttonRotated;
  }

  // converts a point to a posn
  Posn pointToPosn(Point p) {
    return new Posn(p.x, p.y);
  }
  
  // Returns the font size based on the board size
  int getFontSize(int boardSize) {
    if (boardSize >= 70) {
      return 5;
    }
    else if (boardSize >= 55) {
      return 8;
    }
    else if (boardSize >= 40) {
      return 10;
    }
    else if (boardSize >= 30) {
      return 15;
    }
    else if (boardSize >= 25) {
      return 20;
    }
    else if (boardSize >= 20) {
      return 22;
    }
    else if (boardSize >= 15) {
      return 25;
    }
    else if (boardSize >= 10) {
      return 30;
    }
    else {
      return 35;
    }
  }
}

// represents the data of our world
class MinesweepWorld extends World {
  ArrayList<Cell> board;
  int flags;
  ArrayList<Cell> mines;
  boolean mineHit;
  int numClicks;
  int ticks;
  int secs;
  boolean gameStarted;
  boolean gameFinished;
  int version;
  HashMap<Integer, ArrayList<Integer>> verAndFlagList;
  ArrayList<Integer> times;
 
  MinesweepWorld(ArrayList<Cell> board, int flags, ArrayList<Cell> mines) {
    this.board = board;
    this.flags = flags;
    this.mines = mines;
    this.mineHit = false;
    this.numClicks = 0;
    this.ticks = 0;
    this.secs = 0;
    this.gameStarted = false;
    this.gameFinished = false;
    this.version = 0;
    this.verAndFlagList = new HashMap<Integer, ArrayList<Integer>>();
    this.times = new ArrayList<Integer>();
  }
 
  MinesweepWorld() {
    this.board = this.setBoard();
    this.flags = MinesweepWorld.NUM_MINES;
    this.mines = new ArrayList<Cell>();
    this.mineHit = false;
    this.numClicks = 0;
    this.ticks = 0;
    this.secs = 0;
    this.gameStarted = false;
    this.gameFinished = false;
    this.version = 0;
    this.verAndFlagList = new HashMap<Integer, ArrayList<Integer>>();
    this.times = new ArrayList<Integer>();
    this.makeTopConnections();
    this.makeRightConnections();
    this.makeBottomConnections();
    this.makeLeftConnections();
    this.setMines();
    this.setContacts();
  }
  
  MinesweepWorld(HashMap<Integer, ArrayList<Integer>> verAndFlagList) {
    this.board = this.setBoard();
    this.flags = MinesweepWorld.NUM_MINES;
    this.mines = new ArrayList<Cell>();
    this.mineHit = false;
    this.numClicks = 0;
    this.ticks = 0;
    this.secs = 0;
    this.gameStarted = false;
    this.gameFinished = false;
    this.version = 0;
    this.verAndFlagList = new HashMap<Integer, ArrayList<Integer>>();
    this.times = new ArrayList<Integer>();
    this.makeTopConnections();
    this.makeRightConnections();
    this.makeBottomConnections();
    this.makeLeftConnections();
    this.setMines();
    this.setContacts();
  }
  
  // a constant to represent the board size
  static int BOARD_SIZE = 18;
 
  // a constant to represent the cell size based on the board size
  static int CELL_SIZE = (650 / MinesweepWorld.BOARD_SIZE);
  
  // a constant to represent the number of mines
  static int NUM_MINES = 40;
  
  // a constant to represent the font size
  static int FONT_SIZE = new Utils().getFontSize(MinesweepWorld.BOARD_SIZE);
  
  // makes the flag image easily accessible
  static WorldImage FLAG = new Utils().flagImage();
  
  // makes the clock image easily accessible
  static WorldImage CLOCK = new Utils().clockImage();
  
  // makes the restart image easily accessible
  static WorldImage RESTART_BUTTON = new Utils().restartImage();
 
  // Creates a starting board
  ArrayList<Cell> setBoard() {
    ArrayList<Cell> start = new ArrayList<Cell>();
    for (int i = 0; i < (MinesweepWorld.BOARD_SIZE * MinesweepWorld.BOARD_SIZE); i = i + 1) {
      start.add(new Cell(((i % MinesweepWorld.BOARD_SIZE) * MinesweepWorld.CELL_SIZE)
          + MinesweepWorld.CELL_SIZE,
          (((i - (i % MinesweepWorld.BOARD_SIZE)) / MinesweepWorld.BOARD_SIZE)
              * MinesweepWorld.CELL_SIZE)
          + (3 * MinesweepWorld.CELL_SIZE),
          (i % MinesweepWorld.BOARD_SIZE) + 1,
          ((i - (i % MinesweepWorld.BOARD_SIZE)) / MinesweepWorld.BOARD_SIZE) + 1));
    }
    return start;
  }
  
  // EFFECT: sets some cells in the board as mines based on the number of flags
  void setMines() {
    ArrayList<Integer> allInts = new ArrayList<Integer>();
    for (int i = 0; i < this.board.size(); i = i + 1) {
      allInts.add(i);
    }
    int minesLeft = MinesweepWorld.NUM_MINES;
    int curSize = this.board.size();
    while (minesLeft > 0) {
      int rand = new Random().nextInt(curSize);
      int curIdx = allInts.get(rand);
      Cell curMine = this.board.get(curIdx);
      curMine.mine = true;
      this.mines.add(curMine);
      minesLeft = minesLeft - 1;
      allInts.remove(rand);
      curSize = curSize - 1;
    }
  }
  
  // EFFECT: changes the number for each cell's mineContacts field in the board
  void setContacts() {
    for (Cell c : this.board) {
      c.minesInContact();
    }
  }

  // makes the scene for the game
  public WorldScene makeScene() {
    WorldScene world = new WorldScene(0, 0);
    for (int i = 0; i < this.board.size(); i = i + 1) {
      Cell currentCell = this.board.get(i);
      world.placeImageXY(currentCell.drawCell(), currentCell.x, currentCell.y);
    }
    WorldImage infoBase = new RectangleImage((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE), 
        (2 * MinesweepWorld.CELL_SIZE), OutlineMode.SOLID, new Color(80, 155, 0, 255));
    WorldImage flag = new ScaleImage(MinesweepWorld.FLAG, 1.3);
    WorldImage numFlags = new TextImage(Integer.toString(this.flags), MinesweepWorld.FONT_SIZE, FontStyle.BOLD, Color.white);
    WorldImage flagCount = new OverlayOffsetImage(flag, MinesweepWorld.CELL_SIZE, 0, numFlags);
    WorldImage time = new TextImage(Integer.toString(this.secs), MinesweepWorld.FONT_SIZE, FontStyle.BOLD, Color.white);
    WorldImage timer = new OverlayOffsetImage(MinesweepWorld.CLOCK, MinesweepWorld.CELL_SIZE, 0, time);
    infoBase = new OverlayOffsetImage(MinesweepWorld.RESTART_BUTTON, 
        (MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 4, 0,
        infoBase);
    infoBase = new OverlayImage(flagCount, infoBase);
    infoBase = new OverlayOffsetImage(timer, 
        -(MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 4, 0, infoBase);
    world.placeImageXY(infoBase, 
        ((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 2) 
        + (MinesweepWorld.CELL_SIZE / 2), 
        (3 * MinesweepWorld.CELL_SIZE / 2));
    if (this.mineHit) {
      for (Cell c : this.mines) {
        c.clicked = true;
        c.color = c.revealColor();
      }
    }
    if (this.gameFinished) {
      WorldImage background = new RectangleImage(
          (MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE),
          (MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) + (2 * MinesweepWorld.CELL_SIZE),
          OutlineMode.SOLID, new Color(0, 0, 0, 150));
      world.placeImageXY(background, 
          ((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 2) 
          + (MinesweepWorld.CELL_SIZE / 2),
          ((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 2) 
          + ((3 * MinesweepWorld.CELL_SIZE) / 2));
      WorldImage text1 = new TextImage(this.endMessage(), 25, FontStyle.BOLD, Color.white);
      WorldImage text1Shadow = new TextImage(this.endMessage(), 25, 
          FontStyle.BOLD, new Color(0, 0, 0, 200));
      text1 = new OverlayOffsetImage(text1, 2, 2, text1Shadow);
      if (!(this.times.isEmpty())) {
        WorldImage text2 = new TextImage(this.bestTime(), 25, FontStyle.BOLD, Color.white);
        WorldImage text2Shadow = new TextImage(this.bestTime(), 25, 
            FontStyle.BOLD, new Color(0, 0, 0, 200));
        text2 = new OverlayOffsetImage(text2, 2, 2, text2Shadow);
        text1 = new AboveImage(text1, text2);
      }
      world.placeImageXY(text1, ((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 2) 
          + (MinesweepWorld.CELL_SIZE / 2),
          ((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) / 2) 
          + ((3 * MinesweepWorld.CELL_SIZE) / 2));
    }
    return world;
  }
  
  // EFFECT: alters the state of the game based on the clicked cell
  public void onMousePressed(Posn p, String buttonName) {
    int curVersion = this.version;
    boolean cellFound = false;
    boolean searchedAllCells = false;
    Cell cell = null;
    
    // finds the cell that was clicked
    for (int i = 0; !cellFound; i = i + 1) {
      if ((this.board.get(i).x - (MinesweepWorld.CELL_SIZE / 2)) <= p.x 
          && (this.board.get(i).x + (MinesweepWorld.CELL_SIZE / 2)) >= p.x 
          && (this.board.get(i).y - (MinesweepWorld.CELL_SIZE / 2)) <= p.y 
          && (this.board.get(i).y + (MinesweepWorld.CELL_SIZE / 2)) >= p.y) {
        cell = this.board.get(i);
        cellFound = true;
      }
      else {
        if ((this.board.size() - 1) == i) {
          cellFound = true;
          searchedAllCells = true;
        }
      }
    }
    if ((p.x >= (((MinesweepWorld.CELL_SIZE * MinesweepWorld.BOARD_SIZE) / 4) 
        - (MinesweepWorld.CELL_SIZE / 2)))
        && (p.x <= (((MinesweepWorld.CELL_SIZE * MinesweepWorld.BOARD_SIZE) / 4) 
            + ((3 * MinesweepWorld.CELL_SIZE) / 2)))
        && (p.y >= MinesweepWorld.CELL_SIZE)
        && (p.y <= (2 * MinesweepWorld.CELL_SIZE))) {
      this.newGame();
      }
    else if (this.gameFinished || searchedAllCells) {
      // do nothing
    }
    else if (buttonName.equals("RightButton") && !cell.clicked) {
      if (cell.flagged) {
        this.flags = flags + 1;
        }
      else {
        this.flags = flags - 1;
        }
      cell.flagged = !(cell.flagged);
      }
    else if (cell.flagged) {
        //do nothing
      }
    else {
      if ((this.numClicks == 0) && ((cell.mineContacts != 0) || cell.mine)) {
        this.storeFlags();
        MinesweepWorld temp = new MinesweepWorld(this.verAndFlagList);
        this.board = temp.board;
        this.flags = temp.flags;
        this.mines = temp.mines;
        this.mineHit = false;
        this.numClicks = 0;
        this.ticks = 0;
        this.secs = 0;
        this.version = curVersion + 1;
        this.onMousePressed(p, buttonName);
        this.replaceFlags();
        this.gameStarted = true;
      }
      else {
        this.gameStarted = true;
        this.numClicks = numClicks + 1;
        cell.clicked = true;
        cell.color = cell.revealColor();
        if (cell.mine) {
          this.mineHit = true;
          this.gameFinished = true;
        }
      }
      if (cell.mineContacts == 0 && !cell.mine && buttonName.equals("LeftButton")) {
        cell.top.attemptSpread();
        cell.right.attemptSpread();
        cell.bottom.attemptSpread();
        cell.left.attemptSpread();
        cell.top.right.attemptSpread();
        cell.top.left.attemptSpread();
        cell.bottom.right.attemptSpread();
        cell.bottom.left.attemptSpread();
      }
    }
}

  // EFFECT: In the verAndFlagList field, maps the integer representing the 
  // current version to an array list which stores the index of each flagged cell
  void storeFlags() {
    if (this.version == 0) {
      ArrayList<Integer> flagIndices = new ArrayList<Integer>();
      for (Cell c : this.board) {
        if (c.flagged) {
          flagIndices.add(this.board.indexOf(c));
        }
      }
      this.verAndFlagList.put(0, flagIndices);
    }
    else {
      // do nothing
    }
  }
  
  // EFFECT: Makes some cells flagged based on which were flagged in the original version
  // of this MinesweepWorld
  void replaceFlags() {
    ArrayList<Integer> firstList = this.verAndFlagList.get(0);
    for (int i : firstList) {
      //this.board.get(i).clicked = false;
      this.board.get(i).flagged = true;
    }
  }

//  // EFFECT: alters the state of the game based on cell being hovered over
//  public void onMouseEntered(Posn p) {
//    boolean found = false;
//    Cell cell = null;
//      
//    for (int i = 0; i < this.board.size(); i = i + 1) {
//        if ((this.board.get(i).x - (MinesweepWorld.CELL_SIZE / 2)) <= p.x 
//            && (this.board.get(i).x + (MinesweepWorld.CELL_SIZE / 2)) >= p.x 
//            && (this.board.get(i).y - (MinesweepWorld.CELL_SIZE / 2)) <= p.y 
//            && (this.board.get(i).y + (MinesweepWorld.CELL_SIZE / 2)) >= p.y) {
//          cell = this.board.get(i);
//          found = true;
//        }
//      }
//      if (!(found) || cell.clicked) {
//        // do nothing
//      }
//      else {
//        cell.color = new Color(205, 255, 145, 255);
//      }
//    }
//  
//  // EFFECT: alters the state of the game based on cell being hovered over
//  public void onMouseExited(Posn p) {
//    boolean found = false;
//    Cell cell = null;
//      
//    for (int i = 0; i < this.board.size(); i = i + 1) {
//        if ((this.board.get(i).x - (MinesweepWorld.CELL_SIZE / 2)) <= p.x 
//            && (this.board.get(i).x + (MinesweepWorld.CELL_SIZE / 2)) >= p.x 
//            && (this.board.get(i).y - (MinesweepWorld.CELL_SIZE / 2)) <= p.y 
//            && (this.board.get(i).y + (MinesweepWorld.CELL_SIZE / 2)) >= p.y) {
//          cell = this.board.get(i);
//          found = true;
//        }
//      }
//      if (!(found) || cell.clicked) {
//        // do nothing
//      }
//      else {
//        cell.color = cell.initColor();
//      }
//    }
  
  public void onTick() {
    //this.onMouseEntered(new Utils().pointToPosn(MouseInfo.getPointerInfo().getLocation()));
    //this.onMouseExited(new Utils().pointToPosn(MouseInfo.getPointerInfo().getLocation()));
    if (this.gameStarted && !(this.gameFinished)) {
      this.ticks = ticks + 1;
    }
    this.convertToSecs();
    this.checkIfDone();
  }
  
  // EFFECT: changes the secs field based on the ticks
  void convertToSecs() {
    if (!this.gameFinished && ((this.ticks != 0) && (this.ticks % 17 == 0))) {
      this.secs = secs + 1;
    }
  }
  
  void checkIfDone() {
    if (this.mineHit) {
      this.gameFinished = true;
    }
    else {
      boolean gameDone = true;
      for (Cell c : this.board) {
        gameDone = gameDone && (c.clicked || c.mine);
      }
      this.gameFinished = gameDone;
      if (gameDone) {
        this.times.add(this.secs);
      }
    }
  }
  
  // EFFECT: Connects the appropriate cells to their top
  void makeTopConnections() {
    for (Cell c : this.board) {
      if ((this.board.indexOf(c) >= 0) && (this.board.indexOf(c) < MinesweepWorld.BOARD_SIZE)) {
        c.makeTop(new NoCell(new NoCell()));
      }
      else {
        c.makeTop(this.board.get(this.board.indexOf(c) - MinesweepWorld.BOARD_SIZE));
      }
    }
  }
  
  // EFFECT: Connects the appropriate cells to their right
  void makeRightConnections() {
    for (Cell c : this.board) {
      if (this.board.indexOf(c) % MinesweepWorld.BOARD_SIZE == MinesweepWorld.BOARD_SIZE - 1) {
        c.makeRight(new NoCell(new NoCell()));
      }
      else {
        c.makeRight(this.board.get(this.board.indexOf(c) + 1));
      }
    }
  }
  
  // EFFECT: Connects the appropriate cells to their bottom
  void makeBottomConnections() {
    for (Cell c : this.board) {
      if ((this.board.indexOf(c) 
          >= ((MinesweepWorld.BOARD_SIZE * MinesweepWorld.BOARD_SIZE) - MinesweepWorld.BOARD_SIZE))
          && (this.board.indexOf(c) < (MinesweepWorld.BOARD_SIZE * MinesweepWorld.BOARD_SIZE))) {
        c.makeBottom(new NoCell(new NoCell()));
      }
      else {
        c.makeBottom(this.board.get(this.board.indexOf(c) + MinesweepWorld.BOARD_SIZE));
      }
    }
  }
  
  // EFFECT: Connects the appropriate cells to their left
  void makeLeftConnections() {
    for (Cell c : this.board) {
      if (this.board.indexOf(c) % MinesweepWorld.BOARD_SIZE == 0) {
        c.makeLeft(new NoCell(new NoCell()));
      }
      else {
        c.makeLeft(this.board.get(this.board.indexOf(c) - 1));
      }
    }
  }
  
  // EFFECT: Resets the current game and creates a new board
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      this.newGame();
    }
  }
  
  // EFFECT: Makes a new game
  void newGame() {
    MinesweepWorld temp = new MinesweepWorld();
    this.board = temp.board;
    this.flags = temp.flags;
    this.mines = temp.mines;
    this.mineHit = false;
    this.numClicks = 0;
    this.ticks = 0;
    this.secs = 0;
    this.gameStarted = false;
    this.gameFinished = false;
    this.version = 0;
    this.verAndFlagList = temp.verAndFlagList;
  }
  
  // Gets the message for a win or loss depending on the game state
  String endMessage() {
    if (this.mineHit) {
      return "Sorry, YOU LOST!";
    }
    else {
      return "You won in " + Integer.toString(this.secs) + " seconds!";
    }
  }
  
  // Gets the message to say the best time so far
  String bestTime() {
    this.times.sort(new IntegerComparator());
    return "Your current record is " + Integer.toString(this.times.get(0)) + " seconds";
  }
  
//  // Determines when to end the game
//  public WorldEnd worldEnds() {
//    if (this.mineHit) {
//      return new WorldEnd(true, this.makeAFinalScene());
//    }
//    else {
//      return new WorldEnd(false, this.makeScene());
//    }
//  }
//  
//  // Makes the final scene of a game, representing either a win or a loss
//  public WorldScene makeAFinalScene() {
//    for (Cell c : this.mines) {
//      c.clicked = true;
//      c.color = c.revealColor();
//    }
//    return this.makeScene();
//  }
  }

class IntegerComparator implements Comparator<Integer> {

  // Compares two integers
  public int compare(Integer i1, Integer i2) {
    return i1.compareTo(i2);
  }  
}

class ExamplesMinesweep {
  ExamplesMinesweep() {}
 
  Cell c1;
  Cell c2;
  Cell c3;
  Cell c4;
  Cell c5;
  Cell c6;
  Cell c7;
  Cell c8;
  Cell c9;
  Cell c10;
  Cell c11;
  Cell c12;
  Cell c13;
  Cell c1c;
  Cell c2c;
  Cell c3c;
  Cell c4c;
  ACell noCell;
  WorldScene world1;
  WorldScene world2;
  WorldScene world3;
  
  ArrayList<Cell> cellList2x2;
  ArrayList<Cell> cellList3x3;
  ArrayList<Cell> cellListCopy;
  
  MinesweepWorld world2x2;
  MinesweepWorld world3x3;
  MinesweepWorld worldCopy;
 
  void initCond() {
    this.noCell = new NoCell(new NoCell());
    this.c1 = new Cell(0, 0, 1, 1);
    this.c2 = new Cell(MinesweepWorld.CELL_SIZE, 0, 2, 1);
    this.c3 = new Cell(0, MinesweepWorld.CELL_SIZE, 1, 2);
    this.c4 = new Cell(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE, 2, 2);
    this.c1c = new Cell(0, 0, 1, 1);
    this.c2c = new Cell(MinesweepWorld.CELL_SIZE, 0, 2, 1);
    this.c3c = new Cell(0, MinesweepWorld.CELL_SIZE, 1, 2);
    this.c4c = new Cell(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE, 2, 2);
    
    this.cellListCopy = new ArrayList<Cell>(Arrays.asList(c1c, c2c, c3c, c4c));
    
    this.worldCopy = new MinesweepWorld(cellListCopy, 3, new ArrayList<Cell>(Arrays.asList(c1c)));
   
    c1.makeLeft(noCell);
    c1.makeTop(noCell);
    c1.makeRight(c2);
    c1.makeBottom(c3);
   
    c2.makeLeft(c1);
    c2.makeTop(noCell);
    c2.makeRight(noCell);
    c2.makeBottom(c4);
   
    c3.makeLeft(noCell);
    c3.makeTop(c1);
    c3.makeRight(c4);
    c3.makeBottom(noCell);
   
    c4.makeLeft(c3);
    c4.makeTop(c2);
    c4.makeRight(noCell);
    c4.makeBottom(noCell);
    
    this.world1 = new WorldScene(0, 0);
    this.world2 = new WorldScene(0, 0);
    this.world3 = new WorldScene(0, 0);
    
    this.cellList2x2 = new ArrayList<Cell>(Arrays.asList(c1, c2, c3, c4));
    
    this.world2x2 = new MinesweepWorld(cellList2x2, 1, new ArrayList<Cell>(Arrays.asList(c3)));
    
    this.c5 = new Cell(0, 0, 1, 1);
    this.c6 = new Cell(MinesweepWorld.CELL_SIZE, 0, 2, 1);
    this.c7 = new Cell(2 * MinesweepWorld.CELL_SIZE, 0, 3, 1);
    this.c8 = new Cell(0, MinesweepWorld.CELL_SIZE, 2, 1);
    this.c9 = new Cell(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE, 2, 2);
    this.c10 = new Cell(2 * MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE, 2, 3);
    this.c11 = new Cell(0, 2 * MinesweepWorld.CELL_SIZE, 3, 1);
    this.c12 = new Cell(MinesweepWorld.CELL_SIZE, 2 * MinesweepWorld.CELL_SIZE, 3, 2);
    this.c13 = new Cell(2 * MinesweepWorld.CELL_SIZE, 2 * MinesweepWorld.CELL_SIZE, 3, 3);
   
    c5.makeLeft(noCell);
    c5.makeTop(noCell);
    c5.makeRight(c6);
    c5.makeBottom(c8);
   
    c6.makeLeft(c5);
    c6.makeTop(noCell);
    c6.makeRight(c7);
    c6.makeBottom(c9);
   
    c7.makeLeft(c6);
    c7.makeTop(noCell);
    c7.makeRight(noCell);
    c7.makeBottom(c10);
   
    c8.makeLeft(noCell);
    c8.makeTop(c5);
    c8.makeRight(c9);
    c8.makeBottom(c11);
    
    c9.makeLeft(c8);
    c9.makeTop(c6);
    c9.makeRight(c10);
    c9.makeBottom(c12);
   
    c10.makeLeft(c9);
    c10.makeTop(c7);
    c10.makeRight(noCell);
    c10.makeBottom(c13);
    
    c11.makeLeft(noCell);
    c11.makeTop(c8);
    c11.makeRight(c12);
    c11.makeBottom(noCell);
    
    c12.makeLeft(c11);
    c12.makeTop(c9);
    c12.makeRight(c13);
    c12.makeBottom(noCell);
    
    c13.makeLeft(c12);
    c13.makeTop(c10);
    c13.makeRight(noCell);
    c13.makeBottom(noCell);
    
    this.cellList3x3 = new ArrayList<Cell>(Arrays.asList(c5, c6, c7, c8, c9, c10, c11, c12, c13));
    
    this.world3x3 = new MinesweepWorld(cellList3x3, 4, new ArrayList<Cell>());
  }
  
  void testMakeLeft(Tester t) {
    initCond();
    
    t.checkExpect(c1.left, noCell);
    c1.makeLeft(c2);
    t.checkExpect(c1.left, c2);
    c1.makeLeft(noCell);
    t.checkExpect(c1.left, noCell);
    
    t.checkExpect(noCell.left, new NoCell());
    noCell.makeLeft(c4);
    t.checkExpect(noCell.left, c4);
    
    initCond();
  }
  
  void testMakeRight(Tester t) {
    initCond();
    
    t.checkExpect(c1.right, c2);
    c1.makeRight(c3);
    t.checkExpect(c1.right, c3);
    c1.makeRight(noCell);
    t.checkExpect(c1.right, noCell);
    
    t.checkExpect(noCell.right, new NoCell());
    noCell.makeRight(c4);
    t.checkExpect(noCell.right, c4);
    
    initCond();
  }
  
  void testMakeTop(Tester t) {
    initCond();
    
    t.checkExpect(c1.top, noCell);
    c1.makeTop(c3);
    t.checkExpect(c1.top, c3);
    c1.makeTop(noCell);
    t.checkExpect(c1.top, noCell);
    
    t.checkExpect(noCell.top, null);
    noCell.makeTop(c4);
    t.checkExpect(noCell.top, c4);
    
    initCond();
  }
  
  void testMakeBottom(Tester t) {
    initCond();
    
    t.checkExpect(c1.bottom, c3);
    c1.makeBottom(c4);
    t.checkExpect(c1.bottom, c4);
    c1.makeBottom(noCell);
    t.checkExpect(c1.bottom, noCell);
    
    t.checkExpect(noCell.bottom, null);
    noCell.makeBottom(c4);
    t.checkExpect(noCell.bottom, c4);
    
    initCond();
  }
  
  void testDrawCell(Tester t) {
    initCond();
    
    t.checkExpect(c1.drawCell(), new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
        OutlineMode.SOLID, new Color(165, 230, 80, 255)));
    t.checkExpect(c2.drawCell(), new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
        OutlineMode.SOLID, new Color(150, 215, 75, 255)));
    
    c1.clicked = true;
    c2.clicked = true;
    c1.color = c1.revealColor();
    c2.color = c2.revealColor();
    
    t.checkExpect(c1.drawCell(), new OverlayImage(new TextImage("0", 
        MinesweepWorld.FONT_SIZE, FontStyle.BOLD, new Utils().getColor(c1.mineContacts)),
        new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
        OutlineMode.SOLID, new Color(245, 235, 200, 255))));
    t.checkExpect(c2.drawCell(), new OverlayImage(new TextImage("0",
        MinesweepWorld.FONT_SIZE, FontStyle.BOLD, new Utils().getColor(c1.mineContacts)),
        new RectangleImage(MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE,
        OutlineMode.SOLID, new Color(235, 225, 185, 255))));
  }
  
  void testMinesInContact(Tester t) {
    initCond();
    
    t.checkExpect(c8.mineContacts, 0);
    t.checkExpect(c9.mineContacts, 0);
    c5.mine = true;
    //c8.top.mine = true;
    c8.minesInContact();
    c9.minesInContact();
    t.checkExpect(c8.mineContacts, 1);
    t.checkExpect(c9.mineContacts, 1);
    //t.checkExpect(c8.top, c5);
    //t.checkExpect(c5.mine, true);
    //t.checkExpect(c8.top.mine, true);
    c6.mine = true;
    c7.mine = true;
    c8.minesInContact();
    c9.minesInContact();
    t.checkExpect(c8.mineContacts, 2);
    t.checkExpect(c9.mineContacts, 3);
    c8.mine = true;
    c10.mine = true;
    c11.mine = true;
    c12.mine = true;
    c13.mine = true;
    c9.minesInContact();
    t.checkExpect(c9.mineContacts, 8);
    
    initCond();
  }
  
  void testSetBoard(Tester t) {
    MinesweepWorld testSetWorld = new MinesweepWorld();
    t.checkExpect(testSetWorld.board.size(), MinesweepWorld.BOARD_SIZE * MinesweepWorld.BOARD_SIZE);
    t.checkExpect(testSetWorld.board.get(new Random().nextInt(testSetWorld.board.size())) 
        instanceof Cell, true);
  }

  void testMakeScene(Tester t) {
    initCond();
    
    WorldScene world = new WorldScene(0, 0);
    world.placeImageXY(c1.drawCell(), 0, 0);
    world.placeImageXY(c2.drawCell(), MinesweepWorld.CELL_SIZE, 0);
    world.placeImageXY(c3.drawCell(), 0, MinesweepWorld.CELL_SIZE);
    world.placeImageXY(c4.drawCell(), MinesweepWorld.CELL_SIZE, MinesweepWorld.CELL_SIZE);
    
    t.checkExpect(world2x2.makeScene(), world);
  }
  
  void testMinesweepWorld(Tester t) {
    MinesweepWorld world = new MinesweepWorld();
    world.bigBang((MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE) + MinesweepWorld.CELL_SIZE, 
        (MinesweepWorld.BOARD_SIZE * MinesweepWorld.CELL_SIZE)
        + (7 * MinesweepWorld.CELL_SIZE), 1 / 18.0);
  }
}
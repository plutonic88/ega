package ega.parsers;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ega.games.SymmetricMatrixGame;

/**
 * Class for reading a game from a file in the Gamut "SimpleOutput" format
 */

public class EGATParser {

  // empty constructor
  private EGATParser() {
  }

  /**
   * Read in a game specification, given the file name
   */
  public static SymmetricMatrixGame readSymmetricGame(String fileName) {
    SAXParserFactory spf = SAXParserFactory.newInstance();
    try {
      EGATSymmetricGameHandler handler = new EGATSymmetricGameHandler();
      SAXParser parser = spf.newSAXParser();
      parser.parse(fileName, handler);
      return handler.getGame();
    }
    catch (Exception e) {
      throw new RuntimeException("Error parsing EGAT game from file: " + e.getMessage());
    }
  }
}

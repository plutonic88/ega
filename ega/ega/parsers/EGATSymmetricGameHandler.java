package ega.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ega.games.SymmetricMatrixGame;

/**
 * Created by IntelliJ IDEA.
 * User: ckiekint
 * Date: Jun 17, 2007
 * Time: 11:26:55 PM
 */
public class EGATSymmetricGameHandler extends DefaultHandler {

  SymmetricMatrixGame g;
  String name;
  String description;

  Map<String, Integer> players;
  int nPlayers;

  Map<String, Integer> actions;
  int nActs;

  Map<Integer, Integer> currentOutcome;
  List<Double> currentPayoffs;

  int count;

  public EGATSymmetricGameHandler() {
    players = new HashMap<String, Integer>();
    actions = new HashMap<String, Integer>();
    currentOutcome = new HashMap<Integer, Integer>();
    currentPayoffs = new ArrayList<Double>();
  }

  @Override
  public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
    if (qName.equals("nfg")) {
      handleStartNfg(attributes);
    } else if (qName.equals("player")) {
      handleStartPlayer(attributes);
    } else if (qName.equals("action")) {
      handleStartAction(attributes);
    } else if (qName.equals("payoffs")) {
      handleStartPayoffs(attributes);
    } else if (qName.equals("payoff")) {
      handleStartPayoff(attributes);
    } else if (qName.equals("outcome")) {
      handleStartOutcome(attributes);
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    if (qName.equals("nfg")) {
      handleEndNfg();
    } else if (qName.equals("player")) {
      handleEndPlayer();
    } else if (qName.equals("action")) {
      handleEndAction();
    } else if (qName.equals("payoff")) {
      System.out.println(++count);
      handleEndPayoff();
    } else if (qName.equals("payoffs")) {
      handleEndPayoffs();
    } else if (qName.equals("outcome")) {
      handleEndOutcome();
    }
  }

  private void handleStartNfg(Attributes attributes) throws SAXException {
    name = attributes.getValue("name");
    description = attributes.getValue("description");
    nPlayers = 0;
    nActs = 0;
    count = 0;
    players.clear();
    actions.clear();
    currentOutcome.clear();
    currentPayoffs.clear();
  }

  private void handleStartPlayer(Attributes attributes) throws SAXException {
    players.put(attributes.getValue("id"), nPlayers);
    nPlayers++;
  }

  private void handleStartAction(Attributes attributes) throws SAXException {
    actions.put(attributes.getValue("id"), nActs + 1);
    nActs++;
  }

  // at this point we have the number of players and actions, so we can actually
  // create the game instance
  private void handleStartPayoffs(Attributes attributes) throws SAXException {
    g = new SymmetricMatrixGame(nPlayers, nActs);
    g.setDescription(description);
  }

  private void handleStartPayoff(Attributes attributes) throws SAXException {
    currentOutcome.clear();
    currentPayoffs.clear();
  }

  private void handleStartOutcome(Attributes attributes) throws SAXException {
    Integer action = actions.get(attributes.getValue("action"));
    Integer count = Integer.parseInt(attributes.getValue("count"));
    Double payoff = Double.parseDouble(attributes.getValue("value"));
    currentOutcome.put(action, count);
    currentPayoffs.add(payoff);
  }

  private void handleEndNfg() throws SAXException {
  }

  private void handleEndPlayer() throws SAXException {
  }

  private void handleEndAction() throws SAXException {
  }

  private void handleEndPayoff() throws SAXException {
    double[] tmp = new double[currentPayoffs.size()];
    for (int i = 0; i < currentPayoffs.size(); i++) {
      tmp[i] = currentPayoffs.get(i);
    }
    g.setPayoffs(currentOutcome, tmp);
  }

  private void handleEndPayoffs() throws SAXException {
  }

  private void handleEndOutcome() throws SAXException {

  }

  public SymmetricMatrixGame getGame() {
    return g;
  }
}

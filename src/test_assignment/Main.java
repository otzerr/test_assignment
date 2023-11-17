package test_assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		// Setting the variables, dictionaries and arrays
		Integer casinoBalance = 0;
		Dictionary<String, Integer> balanceDict= new Hashtable<>();
		Dictionary<String, Integer> casinoChanges= new Hashtable<>();
		Dictionary<String, Integer> playerMatches= new Hashtable<>();
		Dictionary<String, Integer> playerWins= new Hashtable<>();
		ArrayList<String> IllegalList = new ArrayList<String>();
		ArrayList<String> PlayerStats = new ArrayList<String>();
		ArrayList<String> IllegalActions = new ArrayList<String>();
		
		// Function in try because of file reading
		try {
			File myFile =  new File("src/test_assignment/player_data.txt");
			Scanner myRead = new Scanner(myFile);
			// Iterating through the file to get all the players and their actions
			while (myRead.hasNextLine()) {
				String str = myRead.nextLine();
				ArrayList<String> gamerList = new ArrayList<>(Arrays.asList(str.split(",")));
				
				// Logic for deposit action
				if (gamerList.get(1).equals("DEPOSIT")) {
					if (balanceDict.get(gamerList.get(0)) == null) {
						balanceDict.put(gamerList.get(0), Integer.valueOf(gamerList.get(3)));
						System.out.println(gamerList.get(0) + " has deposited: " + gamerList.get(3) + " to their balance");
					} else {
						Integer depoMoney = Integer.valueOf(gamerList.get(3));
						Integer currentMoney = balanceDict.get(gamerList.get(0));
						balanceDict.put(gamerList.get(0), depoMoney + currentMoney);
						System.out.println(gamerList.get(0) + " has deposited: " + gamerList.get(3) + " to their balance");
					}
				// Logic for withdrawal action
				} else if (gamerList.get(1).equals("WITHDRAW")) {

					if (balanceDict.get(gamerList.get(0)) == null) { // Handling illegal action
						System.out.println("Illegal action by:" + gamerList.get(0) +  " Not enough balance!!");
						if (IllegalList.contains(gamerList.get(0))) {
							System.out.println("User already has one illegal action, ignoring!"); // Ignoring due to output only requiring the first illegal action user tired to make!
						} else {
							IllegalList.add(gamerList.get(0));
							IllegalActions.add(gamerList.get(0) + " WITHDRAW " + "null " + gamerList.get(3) + " null" );			
						}	
					} else {
						Integer withdrawMoney = Integer.valueOf(gamerList.get(3));
						Integer currentMoney = balanceDict.get(gamerList.get(0));
						if (withdrawMoney - currentMoney <= -1) {
							System.out.println("Illegal action by: " + gamerList.get(0) +  " Not enough balance!!");
							if (IllegalList.contains(gamerList.get(0))) {
								System.out.println("User already has one illegal action, ignoring!"); // Ignoring due to output only requiring the first illegal action user tired to make!
							} else {
								IllegalList.add(gamerList.get(0));
								IllegalActions.add(gamerList.get(0) + " WITHDRAW " + "null " + gamerList.get(3) + " null" );			
							}
						} else {
							Integer finalAmount = withdrawMoney - currentMoney;
							balanceDict.put(gamerList.get(0), finalAmount);
							System.out.println(gamerList.get(0) + " has withdrawn: " + gamerList.get(3) + " from their balance");
						}
					}	
				// Logic for betting action
				} else if (gamerList.get(1).equals("BET")) {

					if (balanceDict.get(gamerList.get(0)) == null) { // Handling illegal action
						System.out.println("Illegal action by:" + gamerList.get(0) +  " Not enough balance!!");
						if (IllegalList.contains(gamerList.get(0))) {
							System.out.println("User already has one illegal action, ignoring!"); // Ignoring due to output only requiring the first illegal action user tired to make!
						} else {
							String betLine = gamerList.get(2);
							IllegalList.add(gamerList.get(0));
							IllegalActions.add(gamerList.get(0) + " BET " + betLine + " " + gamerList.get(3) + " " + gamerList.get(4));			
						}	
					} else {
						String betLine = GetBet(gamerList.get(2));
						ArrayList<String> betStats = new ArrayList<>(Arrays.asList(betLine.split(",")));
						if (betLine.equals("") || betLine.equals("Error")) {
							System.out.println("Match with the ID provided was not found!");
						} else {
							Integer betMoney = Integer.valueOf(gamerList.get(3));
							Integer currentMoney = balanceDict.get(gamerList.get(0));
							if (betMoney > currentMoney) {
								System.out.println("Illegal action by:" + gamerList.get(0) +  " Not enough balance!!");
								IllegalList.add(gamerList.get(0));
							} else {
								String playerBet = (gamerList.get(4));
								String winningBet = betStats.get(3);
								Integer winGet = 0;
								if (winningBet.equals("A")) {
									winGet = 1;
								} else {
									winGet = 2;
								}
								if (playerBet.equals(winningBet) || winningBet.equals("DRAW")) {
									Integer betWin = 0;
									String matchType = "";
									if (winningBet.equals("DRAW")) {
										betWin = betMoney;
										matchType = "DRAW";
									} else {
										Float betYes = betMoney * Float.parseFloat(betStats.get(winGet));
										betWin = Math.round(betYes);
										balanceDict.put(gamerList.get(0), balanceDict.get(gamerList.get(0)) + betWin);
									}
									Integer casinoPayout = 0;
									boolean BetBigger = false;
									if (betWin < betMoney) {
										casinoPayout = betMoney - betWin;
										BetBigger = true;
									} else {
										casinoPayout = betWin;
									}
									if (playerMatches.get(gamerList.get(0)) == null) {
										playerMatches.put(gamerList.get(0), 1);
										if (matchType.equals("DRAW")) {
											System.out.println("Match ended in a draw " + gamerList.get(0) + " got their coins back!");
										} else {

											playerWins.put(gamerList.get(0), 1);
											System.out.println(gamerList.get(0) + " won: " + betWin);
											if (casinoChanges.get(gamerList.get(0)) == null) {
												if (BetBigger == true) {
													casinoChanges.put(gamerList.get(0), +casinoPayout);
												} else {
													casinoChanges.put(gamerList.get(0), -casinoPayout);
												}

											} else {
												if (BetBigger == true) {
													casinoChanges.put(gamerList.get(0), +casinoPayout);
												} else {
													casinoChanges.put(gamerList.get(0), -casinoPayout);
												}
											}
										}

									} else {
										playerMatches.put(gamerList.get(0), playerMatches.get(gamerList.get(0)) + 1);
										if (matchType.equals("DRAW")) {
											System.out.println("Match ended in a draw " + gamerList.get(0) + " got their coins back!");
										} else {
											playerWins.put(gamerList.get(0), playerWins.get(gamerList.get(0)) + 1);
											System.out.println(gamerList.get(0) + " won: " + betWin);
											if (casinoChanges.get(gamerList.get(0)) == null) {
												if (BetBigger == true) {
													casinoChanges.put(gamerList.get(0), +casinoPayout);
												} else {
													casinoChanges.put(gamerList.get(0), -casinoPayout);
												}
											} else {
												if (BetBigger == true) {
													casinoChanges.put(gamerList.get(0), casinoChanges.get(gamerList.get(0)) + casinoPayout);
												} else {
													casinoChanges.put(gamerList.get(0), casinoChanges.get(gamerList.get(0)) - casinoPayout);
												}
											}
										}

									}
								} else {
									if (playerMatches.get(gamerList.get(0)) == null) {
										playerMatches.put(gamerList.get(0), 1);
										balanceDict.put(gamerList.get(0), balanceDict.get(gamerList.get(0)) - betMoney);
										System.out.println(gamerList.get(0) + " lost a bet worth: " + betMoney);
										if (casinoChanges.get(gamerList.get(0)) == null) {
											casinoChanges.put(gamerList.get(0), betMoney);
										} else {
											casinoChanges.put(gamerList.get(0), casinoChanges.get(gamerList.get(0)) + betMoney);
										}
									} else {
										playerMatches.put(gamerList.get(0), playerMatches.get(gamerList.get(0)) + 1);
										balanceDict.put(gamerList.get(0), balanceDict.get(gamerList.get(0)) - betMoney);
										if (casinoChanges.get(gamerList.get(0)) == null) {
											casinoChanges.put(gamerList.get(0), betMoney);
										} else {
											casinoChanges.put(gamerList.get(0), casinoChanges.get(gamerList.get(0)) + betMoney);
										}
										System.out.println(gamerList.get(0) + " lost a bet worth: " + betMoney);
									}				
								}
							}
						}
					}
				}
			}
			myRead.close();
		} catch (FileNotFoundException e) {
			System.out.println("Error reading the file");
			e.printStackTrace();
		}

		Integer size = IllegalList.size();
		for (int i = 0; i < size; i++) {
			casinoChanges.remove(IllegalList.get(i));
		}
		Enumeration<String> playersEnu = balanceDict.keys();
		while (playersEnu.hasMoreElements()) {
			String playerID = playersEnu.nextElement();
			
			if (IllegalList.contains(playerID)) { // Removing all user actions so they do not impact the casino balance
				System.out.println(playerID + " not displayed in results due to at least one illegal action"); 
			} else {
				double Matches = playerMatches.get(playerID);
				double Wins = 0;
				if (playerWins.get(playerID) == null) {
					Wins = 0;
				} else {
					Wins = playerWins.get(playerID);
				}
				double WinRate = Wins / Matches;
				WinRate = Math.round(WinRate * 100.0) / 100.0;
				PlayerStats.add(playerID + " " + balanceDict.get(playerID) + " " + WinRate);
			}
		}
		
		Enumeration<Integer> casenu = casinoChanges.elements(); // Handling casino balance changes
		while (casenu.hasMoreElements()) {
			Integer theValue = casenu.nextElement();
			casinoBalance = casinoBalance + (Integer) theValue;
		}
		
		Integer size2 = PlayerStats.size();
		Integer size3 = IllegalActions.size();
		
		try {  // Handling the writing of results
			FileWriter OutputFile = new FileWriter("src/test_assignment/results.txt");
			String outputString = "";
			for (int i = 0; i < size2; i++) {
				outputString = outputString + PlayerStats.get(i) + "\n";
				
			}
			outputString = outputString + "\n";
			for (int i = 0; i < size3; i++) {
				outputString = outputString + IllegalActions.get(i) + "\n";
			}
			outputString = outputString + "\n";
			outputString = outputString + casinoBalance;
			OutputFile.write(outputString);
			OutputFile.close();
			System.out.println("Successfully wrote results to results.txt!");
		
		} catch (IOException e) {
			System.out.println("Error occured when writing results!");
			e.printStackTrace();
		}
	}
	
	public static String GetBet(String betID) {
		// Function for getting the bet results from the match_data file
		String theLine = "";
		try {
			File matchData = new File("src/test_assignment/match_data.txt");
			Scanner readMatch = new Scanner(matchData);
			while (readMatch.hasNextLine()) {
				String matchLine = readMatch.nextLine();
				ArrayList<String> betStats = new ArrayList<>(Arrays.asList(matchLine.split(",")));
				if (betStats.get(0).equals(betID)) {
					theLine = matchLine;
				}
			}
			readMatch.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("Error reading match data!");
			e.printStackTrace();
			theLine = "Error";
		}
		return theLine;
	}
}

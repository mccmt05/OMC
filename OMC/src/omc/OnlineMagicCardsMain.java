package omc;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import omc.clan.ClanURLs;
import omc.clan.ClanUtils;
import omc.dojo.DOJOURLsOnly;
import omc.dojo.DOJOURLsShared;
import omc.dojo.DOJOUtils;
import omc.price.PriceUtils;


public class OnlineMagicCardsMain 
{
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		int menuChoice = 0;
		
		PriceUtils pu = new PriceUtils();
		List<ProfitObject> profitList = null;
		
		HashMap<String, PriceInfo> clanCardMap = null;
		HashMap<String, PriceInfo> dojoCardMap = null;
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("Welcome to Online Magic Cards Tool");
		
		Calendar currentCal = null;
		Calendar pricesRetrievedCal = null;
		Calendar pricesRetrievedCalCompare = null;
		while(menuChoice != 9)
		{
			try
			{	
				System.out.println("Please choose an option from the following:");
				System.out.println("\t1: Figure out cards to profit on between bots");
				System.out.println("\t2: Get best buy price from bot");
				System.out.println("\t3: Get best sell price from bot");
				System.out.println("\t4: Get cards bots are buying between value of x,y");
				System.out.println("\t5: Get cards bots are selling between value of x,y");
				System.out.println("\t6: Get value of inventory from external file");
				System.out.println("\t9: Exit program");
				System.out.print("Which option would you like to do? ");
				menuChoice = Integer.parseInt(br.readLine());
				
				if(pricesRetrievedCal != null)
				{
					pricesRetrievedCalCompare = (Calendar) pricesRetrievedCal.clone();
					pricesRetrievedCalCompare.add(Calendar.MINUTE, 15);
				}
				
				currentCal = Calendar.getInstance();
				
				if(menuChoice != 9 && (pricesRetrievedCal == null || currentCal.compareTo(pricesRetrievedCalCompare) > 0))
				{
					System.out.println("Getting card prices. Please wait...");
					//Get ClanTeam prices
					ClanUtils cu = new ClanUtils();
					clanCardMap = new HashMap<String, PriceInfo>();
					String[] clanUrls = ClanURLs.getUrls();
					for(int i=0; i < clanUrls.length; i++)
					{
						if(i!=0)
						{
							clanCardMap.putAll(cu.processClanPrices(pu.getPrices(new URL(clanUrls[i]), true), clanUrls[i].substring(42)));
						}
						else
						{
							clanCardMap.putAll(cu.processClanBooster(pu.getPrices(new URL(clanUrls[0]), true)));
						}
					}

					//Get DOJO prices
					DOJOUtils du = new DOJOUtils();
					String[] dojoUrls = DOJOURLsShared.getUrls();
					dojoCardMap = new HashMap<String, PriceInfo>();
					for(int i=0; i < dojoUrls.length; i++)
					{
						dojoCardMap.putAll(du.processDojoPrices(pu.getPrices(new URL(dojoUrls[i]), false)));
					}
					
					if(menuChoice != 1)
					{
						String[] dojoUrlsOnly = DOJOURLsOnly.getUrls();
						for(int j=0; j < dojoUrlsOnly.length; j++)
						{
							dojoCardMap.putAll(du.processDojoPrices(pu.getPrices(new URL(dojoUrlsOnly[j]), false)));						
						}
					}
					
					pricesRetrievedCal = (Calendar) currentCal.clone();
				}
				
				switch(menuChoice)
				{
					case 1:
						ProfitObject pObj = null;
						profitList = new ArrayList<ProfitObject>();
						for(Entry<String, PriceInfo> entry : dojoCardMap.entrySet())
						{
							pObj = pu.canProfitBeMade(clanCardMap, entry.getValue());
							if(pObj!=null)
							{
								profitList.add(pObj);
							}
						}
						
						Collections.sort(profitList);
						
						for(int k=0; k < profitList.size(); k++)
						{
							System.out.println(profitList.get(k));
						}
						System.out.println();
						break;
					case 2:
						System.out.print("Please enter card(s) you would like to buy: ");
						String inputDataBuy = br.readLine();
						pu.buyingCards(inputDataBuy, clanCardMap, dojoCardMap);
						break;
					case 3:
						System.out.print("Please enter card(s) you would like to sell: ");
						String inputDataSell = br.readLine();
						pu.sellingCards(inputDataSell, clanCardMap, dojoCardMap, true);
						break;
					case 4:
						System.out.print("Please enter the low and high value for cards being bought by bots: ");
						String[] botsBuyPrices = br.readLine().split(",");
						pu.getCardsBotsBuyingBetween(Double.parseDouble(botsBuyPrices[0]), Double.parseDouble(botsBuyPrices[1]), 
								clanCardMap, dojoCardMap);
						break;
					case 5:
						System.out.print("Please enter the low and high value for cards being sold by bots: ");
						String[] botsSellPrices = br.readLine().split(",");
						pu.getCardsBotsSellingBetween(Double.parseDouble(botsSellPrices[0]), Double.parseDouble(botsSellPrices[1]), 
								clanCardMap, dojoCardMap);
						break;
					case 6:
						System.out.print("Please enter the file path and name: ");
						String inputFileName = br.readLine();
						if(!inputFileName.endsWith(".csv"))
						{
							System.out.println("I can only do this operation using a csv file from Magic Online.");
						}
						else
						{
							pu.getInventoryPrice(inputFileName, clanCardMap, dojoCardMap);
						}
						break;
					case 9:
						System.out.println("Thanks for using!");
						break;
					default:
						System.out.println("Invalid option. Please try again.");
						break;
				}
			} 
			catch (Exception e) 
			{
				System.out.println("Sorry there was an exception: ");
				e.printStackTrace();
				try 
				{
					Thread.sleep(500);
				} 
				catch (InterruptedException e1) 
				{
					e1.printStackTrace();
				}
			} 
		}
	}
}

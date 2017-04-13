package omc.price;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map.Entry;

import omc.CardPriceSearch;
import omc.PriceBetweenObject;
import omc.PriceInfo;
import omc.ProfitObject;
import omc.clan.ClanHeaders;
import omc.dojo.DOJOHeaders;


public class PriceUtils 
{
	private static DecimalFormat fourDForm = new DecimalFormat("###.####");
	
	public String getPrices(URL inUrl, boolean fromClan)
	{	
		StringBuffer rtnSb = new StringBuffer();
		
		try 
		{
			HttpURLConnection urlConn = (HttpURLConnection) inUrl.openConnection();
			
			if(fromClan)
			{
				urlConn.setRequestMethod("GET");
				urlConn.setRequestProperty("Host", ClanHeaders.HOST_HDR);
				urlConn.setRequestProperty("Connection", ClanHeaders.CONNECTION_HDR);
				urlConn.setRequestProperty("Accept", ClanHeaders.ACCEPT_HDR);
				urlConn.setRequestProperty("User-Agent", ClanHeaders.USER_AGENT_HDR);
				urlConn.setRequestProperty("Referer", ClanHeaders.REFERER_HDR);
				//TODO: For some reason this spits back garbage in the resulting stream
//				urlConn.setRequestProperty("Accept-Encoding", ClanHeaders.ACCEPT_ENCODING_HDR);
				urlConn.setRequestProperty("Accept-Language", ClanHeaders.ACCEPT_LANGUAGE_HDR);
				urlConn.setRequestProperty("Cookie", ClanHeaders.COOKIE_HDR);
			}
			else
			{
				urlConn.setRequestMethod("GET");
				urlConn.setRequestProperty("Host", DOJOHeaders.HOST_HDR);
				urlConn.setRequestProperty("Connection", DOJOHeaders.CONNECTION_HDR);
				urlConn.setRequestProperty("Accept", DOJOHeaders.ACCEPT_HDR);
				urlConn.setRequestProperty("User-Agent", DOJOHeaders.USER_AGENT_HDR);
				//urlConn.setRequestProperty("Referer", DOJOHeaders.REFERER_HDR);
				//TODO: For some reason this spits back garbage in the resulting stream
//				urlConn.setRequestProperty("Accept-Encoding", DOJOHeaders.ACCEPT_ENCODING_HDR);
				urlConn.setRequestProperty("Accept-Language", DOJOHeaders.ACCEPT_LANGUAGE_HDR);
				urlConn.setRequestProperty("Cookie", DOJOHeaders.COOKIE_HDR);	
				urlConn.setRequestProperty("Upgrade-Insecure-Request", DOJOHeaders.UPGRADE_INSECURE_REQ_HDR);
			}
			
			BufferedReader bufRead = new BufferedReader(
					new InputStreamReader(urlConn.getInputStream()));
			
			String inputLine;
			while ((inputLine = bufRead.readLine()) != null) 
			{
				rtnSb.append(inputLine);
			}
			bufRead.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		return rtnSb.toString();
	}
	
	public ProfitObject canProfitBeMade(HashMap<String,PriceInfo> hMapClan, PriceInfo piDojo)
	{
		ProfitObject pObj = null;
		PriceInfo piClan = null;
		
		piClan = hMapClan.get(piDojo.getSetCode() + piDojo.getCardName());
		
		if(piClan != null)
		{
			if(piClan.getPriceToBuy() < piDojo.getPriceToSell() 
					&& piClan.getPriceToBuy() != 0.0
					&& piClan.getHasStockToSell())
			{
				pObj = new ProfitObject();
				pObj.setSetCode(piClan.getSetCode());
				pObj.setCardName(piClan.getCardName());
				pObj.setBuyFromCode("Clan");
				pObj.setBuyPrice(piClan.getPriceToBuy());
				pObj.setSellToCode("DOJO");
				pObj.setSellPrice(piDojo.getPriceToSell());
				pObj.setProfit(piDojo.getPriceToSell() - piClan.getPriceToBuy());
				pObj.setInventoryBots(piClan.getInventoryBots());
			}
			else if(piClan.getPriceToSell() > piDojo.getPriceToBuy()
					&& piDojo.getPriceToBuy() > 0.0
					&& piDojo.getHasStockToSell())
			{
				pObj = new ProfitObject();
				pObj.setSetCode(piClan.getSetCode());
				pObj.setCardName(piClan.getCardName());
				pObj.setBuyFromCode("DOJO");
				pObj.setBuyPrice(piDojo.getPriceToBuy());
				pObj.setSellToCode("Clan");
				pObj.setSellPrice(piClan.getPriceToSell());
				pObj.setProfit(piClan.getPriceToSell() - piDojo.getPriceToBuy());
				pObj.setInventoryBots(piDojo.getInventoryBots());
			}
		}
		
		return pObj;
	}
	
	public void buyingCards(String inputDataBuy, HashMap<String, PriceInfo> clanCardMap, HashMap<String, PriceInfo> dojoCardMap)
	{
		CardPriceSearch cpsBuy = null;
		double buyDif = 0.0;
		double buyDifTotal = 0.0;
		double buyTotal = 0.0;
		for(String indivCard : inputDataBuy.split("@;"))
		{
			if(indivCard.length() != 0)
			{
				StringBuffer buySB = null;
				
				cpsBuy = new CardPriceSearch(indivCard);
				//Find the least of the buying prices
				if(clanCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()) != null ||
						dojoCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()) != null)
				{
					if(clanCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()) != null 
							&& clanCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()).getHasStockToSell())
					{
						cpsBuy.setClanPrice(clanCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()).getPriceToBuy());
					}
					else
					{
						cpsBuy.setClanPrice(9999.99);
					}
					
					if(dojoCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()) != null
							&& dojoCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()).getHasStockToSell())
					{
						cpsBuy.setDojoPrice(dojoCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()).getPriceToBuy());
					}
					else
					{
						cpsBuy.setDojoPrice(9999.99);
					}
					
					if(cpsBuy.getClanPrice() < cpsBuy.getDojoPrice())
					{
						buySB = new StringBuffer();
						buySB.append("Buy ");
						buySB.append(cpsBuy.getQuantity());
						buySB.append(" ");
						buySB.append(cpsBuy.getSetCode());
						buySB.append(" ");
						buySB.append(cpsBuy.getCardName());
						buySB.append(" from Clan[");
						buySB.append(clanCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()).getInventoryBots());
						buySB.append("] for ");
						buySB.append(fourDForm.format(cpsBuy.getClanPrice()));
						buySB.append(" each and save a total of ");
						if(cpsBuy.getDojoPrice() == 9999.99)
						{
							buyDif = 0.0;
						}
						else
						{
							buyDif = cpsBuy.getQuantity() * (cpsBuy.getDojoPrice() - cpsBuy.getClanPrice());
						}
						buySB.append(fourDForm.format(buyDif));
						buyTotal = buyTotal + (cpsBuy.getQuantity() * cpsBuy.getClanPrice());
					}
					else if(cpsBuy.getClanPrice() > cpsBuy.getDojoPrice())
					{
						buySB = new StringBuffer();
						buySB.append("Buy ");
						buySB.append(cpsBuy.getQuantity());
						buySB.append(" ");
						buySB.append(cpsBuy.getSetCode());
						buySB.append(" ");
						buySB.append(cpsBuy.getCardName());
						buySB.append(" from DOJO[");
						buySB.append(dojoCardMap.get(cpsBuy.getSetCode() + cpsBuy.getCardName()).getInventoryBots());
						buySB.append("] for ");
						buySB.append(fourDForm.format(cpsBuy.getDojoPrice()));
						buySB.append(" each and save a total of ");
						if(cpsBuy.getClanPrice() == 9999.99)
						{
							buyDif = 0.0;
						}
						else
						{
							buyDif = cpsBuy.getQuantity() * (cpsBuy.getClanPrice() - cpsBuy.getDojoPrice());
						}
						buySB.append(fourDForm.format(buyDif));
						buyTotal = buyTotal + (cpsBuy.getQuantity() * cpsBuy.getDojoPrice());
					}
					else
					{
						if(cpsBuy.getClanPrice() == 9999.99 && cpsBuy.getDojoPrice() == 9999.99)
						{
							buySB = new StringBuffer();
							buySB.append("Can't find a bot that is selling ");
							buySB.append(cpsBuy.getQuantity());
							buySB.append(" ");
							buySB.append(cpsBuy.getSetCode());
							buySB.append(" ");
							buySB.append(cpsBuy.getCardName());
						}
						else
						{
							buySB = new StringBuffer();
							buySB.append("Buy ");
							buySB.append(cpsBuy.getQuantity());
							buySB.append(" ");
							buySB.append(cpsBuy.getSetCode());
							buySB.append(" ");
							buySB.append(cpsBuy.getCardName());
							buySB.append(" from either, price is the same which is ");
							buySB.append(fourDForm.format(cpsBuy.getClanPrice()));
							buyDif = 0.0;
							buyTotal = buyTotal + (cpsBuy.getQuantity() * cpsBuy.getClanPrice());
						}
					}
					
					buyDifTotal = buyDifTotal + buyDif;
					System.out.println(buySB.toString());
				}
				else
				{
					buySB = new StringBuffer();
					buySB.append("I didn't find a buying price for ");
					buySB.append(cpsBuy.getSetCode());
					buySB.append(" ");
					buySB.append(cpsBuy.getCardName());
					System.out.println(buySB.toString());
				}
			}
		}
		
		StringBuffer totalsBuySB = new StringBuffer();
		totalsBuySB.append("Follow the instructions and spend ");
		totalsBuySB.append(fourDForm.format(buyTotal));
		totalsBuySB.append(" for all card(s), saving you ");
		totalsBuySB.append(fourDForm.format(buyDifTotal));
		System.out.println(totalsBuySB.toString());
	}
	
	public void sellingCards(String inputDataSell, HashMap<String, PriceInfo> clanCardMap, HashMap<String, PriceInfo> dojoCardMap, boolean verbose)
	{
		CardPriceSearch cpsSell = null;
		double sellDif = 0.0;
		double sellDifTotal = 0.0;
		double sellTotal = 0.0;
		int sellClan = 0;
		int sellDojo = 0;
		int sellToEither = 0;
		int noSale = 0;
		for(String indivCard : inputDataSell.split("@;"))
		{
			if(indivCard.length() != 0)
			{
				StringBuffer sellSB = null;
				cpsSell = new CardPriceSearch(indivCard);
				//Find the least of the buying prices
				if(clanCardMap.get(cpsSell.getSetCode() + cpsSell.getCardName()) != null ||
						dojoCardMap.get(cpsSell.getSetCode() + cpsSell.getCardName()) != null)
				{
					if(clanCardMap.get(cpsSell.getSetCode() + cpsSell.getCardName()) != null)
					{
						cpsSell.setClanPrice(clanCardMap.get(cpsSell.getSetCode() + cpsSell.getCardName()).getPriceToSell());
					}
					else
					{
						cpsSell.setClanPrice(0.0);
					}
					
					if(dojoCardMap.get(cpsSell.getSetCode() + cpsSell.getCardName()) != null)
					{
						cpsSell.setDojoPrice(dojoCardMap.get(cpsSell.getSetCode() + cpsSell.getCardName()).getPriceToSell());
					}
					else
					{
						cpsSell.setDojoPrice(0.0);
					}
					
					if(cpsSell.getClanPrice() < cpsSell.getDojoPrice())
					{
						if(verbose)
						{
							sellSB = new StringBuffer();
							sellSB.append("Sell ");
							sellSB.append(cpsSell.getQuantity());
							sellSB.append(" ");
							sellSB.append(cpsSell.getSetCode());
							sellSB.append(" ");
							sellSB.append(cpsSell.getCardName());
							sellSB.append(" to DOJO for a price of ");
							sellSB.append(fourDForm.format(cpsSell.getDojoPrice()));
							sellSB.append(" each and get a total of ");
							sellDif = cpsSell.getQuantity() * (cpsSell.getDojoPrice() - cpsSell.getClanPrice());
							sellSB.append(fourDForm.format(sellDif));
							sellSB.append(" more");
						}
						sellDojo++;
						sellTotal = sellTotal + (cpsSell.getQuantity() * cpsSell.getDojoPrice());
					}
					else if(cpsSell.getClanPrice() > cpsSell.getDojoPrice())
					{
						if(verbose)
						{
							sellSB = new StringBuffer();
							sellSB.append("Sell ");
							sellSB.append(cpsSell.getQuantity());
							sellSB.append(" ");
							sellSB.append(cpsSell.getSetCode());
							sellSB.append(" ");
							sellSB.append(cpsSell.getCardName());
							sellSB.append(" to Clan for a price of ");
							sellSB.append(fourDForm.format(cpsSell.getClanPrice()));
							sellSB.append(" each and get a total of ");
							sellDif = cpsSell.getQuantity() * (cpsSell.getClanPrice() - cpsSell.getDojoPrice());
							sellSB.append(fourDForm.format(sellDif));
							sellSB.append(" more");
						}
						sellClan++;
						sellTotal = sellTotal + (cpsSell.getQuantity() * cpsSell.getClanPrice());
					}
					else
					{
						if(cpsSell.getClanPrice() == 0.0 && cpsSell.getDojoPrice() == 0.0)
						{
							if(verbose)
							{
								sellSB = new StringBuffer();
								sellSB.append("Can't find a bot that wants to buy ");
								sellSB.append(cpsSell.getQuantity());
								sellSB.append(" ");
								sellSB.append(cpsSell.getSetCode());
								sellSB.append(" ");
								sellSB.append(cpsSell.getCardName());
							}
							noSale++;
						}
						else
						{
							if(verbose)
							{
								sellSB = new StringBuffer();
								sellSB.append("Sell ");
								sellSB.append(cpsSell.getQuantity());
								sellSB.append(" ");
								sellSB.append(cpsSell.getSetCode());
								sellSB.append(" ");
								sellSB.append(cpsSell.getCardName());
								sellSB.append(" to either, price is the same which is ");
								sellSB.append(fourDForm.format(cpsSell.getClanPrice()));
								sellSB.append(fourDForm.format(cpsSell.getClanPrice()));
							}
							sellToEither++;
							sellDif = 0.0;
							sellTotal = sellTotal + (cpsSell.getQuantity() * cpsSell.getClanPrice());
						}
					}
					
					sellDifTotal = sellDifTotal + sellDif;
					if(verbose)
					{
						System.out.println(sellSB.toString());
					}
				}
				else
				{
					if(verbose)
					{
						sellSB = new StringBuffer();
						sellSB.append("I didn't find a selling price for ");
						sellSB.append(cpsSell.getSetCode());
						sellSB.append(" ");
						sellSB.append(cpsSell.getCardName());
						System.out.println(sellSB.toString());
					}
					noSale++;
				}
			}
		}
		
		if(verbose)
		{
			StringBuffer totalsSellSBVerbose = new StringBuffer();
			totalsSellSBVerbose.append("Follow the instructions and get ");
			totalsSellSBVerbose.append(fourDForm.format(sellTotal));
			totalsSellSBVerbose.append(" for all card(s), which makes you an extra ");
			totalsSellSBVerbose.append(fourDForm.format(sellDifTotal));
			System.out.println(totalsSellSBVerbose.toString());
		}
		else
		{
			StringBuffer totalsSellSBNonVerbose = new StringBuffer();
			totalsSellSBNonVerbose.append("I found a total of ");
			totalsSellSBNonVerbose.append(sellClan + sellDojo + sellToEither + noSale);
			totalsSellSBNonVerbose.append(" different cards.  \nOf those to get the best price ");
			totalsSellSBNonVerbose.append(sellClan);
			totalsSellSBNonVerbose.append(" should be sold to Clan, ");
			totalsSellSBNonVerbose.append(sellDojo);
			totalsSellSBNonVerbose.append(" should be sold to Dojo and ");
			totalsSellSBNonVerbose.append(sellToEither);
			totalsSellSBNonVerbose.append(" can be sold to either.  \nThere were also ");
			totalsSellSBNonVerbose.append(noSale);
			totalsSellSBNonVerbose.append(" cards I couldn't find a sell price for.\n");
			totalsSellSBNonVerbose.append("The total sell price I can calculate is ");
			totalsSellSBNonVerbose.append(fourDForm.format(sellTotal));
			System.out.println(totalsSellSBNonVerbose.toString());
		}
	}
	
	public void getCardsBotsBuyingBetween(double low, double high, HashMap<String, PriceInfo> clanMap, HashMap<String, PriceInfo> dojoMap)
	{
		//Issue in that if one bot buys for more than the high....then it won't select that card
		//from that bot, which would be more revenue
		
		HashMap<String, PriceInfo> clanMatchesMap = new HashMap<String, PriceInfo>();
		HashMap<String, PriceInfo> dojoMatchesMap = new HashMap<String, PriceInfo>();
		
		for(PriceInfo piClan : clanMap.values())
		{
			if(piClan.getPriceToSell() >= low && piClan.getPriceToSell() <= high)
			{
				clanMatchesMap.put(piClan.getSetCode()+piClan.getCardName(), piClan);
			}
		}
		
		for(PriceInfo piDojo : dojoMap.values())
		{
			if(piDojo.getPriceToSell() >= low && piDojo.getPriceToSell() <= high)
			{
				dojoMatchesMap.put(piDojo.getSetCode()+piDojo.getCardName(), piDojo);
			}
		}
		
		PriceInfo otherPi = null;
		HashMap<String, PriceBetweenObject> higherValueMap = new HashMap<String, PriceBetweenObject>();
		PriceBetweenObject pbo = null;
		for(Entry<String, PriceInfo> indivEntry : clanMatchesMap.entrySet())
		{
			if(dojoMatchesMap.containsKey(indivEntry.getKey()))
			{
				otherPi = dojoMatchesMap.get(indivEntry.getKey());
				pbo = new PriceBetweenObject();
				pbo.setSetCode(otherPi.getSetCode());
				pbo.setCardName(otherPi.getCardName());
				pbo.setClanPrice(indivEntry.getValue().getPriceToSell());
				pbo.setDojoPrice(otherPi.getPriceToSell());
				higherValueMap.put(indivEntry.getKey(), pbo);
			}
			else
			{
				pbo = new PriceBetweenObject();
				pbo.setSetCode(indivEntry.getValue().getSetCode());
				pbo.setCardName(indivEntry.getValue().getCardName());
				pbo.setClanPrice(indivEntry.getValue().getPriceToSell());
				pbo.setDojoPrice(0.0);
				higherValueMap.put(indivEntry.getKey(), pbo);
			}
		}
		
		for(Entry<String, PriceInfo> indivEntry : dojoMatchesMap.entrySet())
		{
			if(!higherValueMap.containsKey(indivEntry.getKey()))
			{
				pbo = new PriceBetweenObject();
				pbo.setSetCode(indivEntry.getValue().getSetCode());
				pbo.setCardName(indivEntry.getValue().getCardName());
				pbo.setDojoPrice(indivEntry.getValue().getPriceToSell());
				pbo.setClanPrice(0.0);
				higherValueMap.put(indivEntry.getKey(), pbo);
			}
		}
		
		StringBuffer outSB = null;
		for(PriceBetweenObject pboIndiv : higherValueMap.values())
		{
			outSB = new StringBuffer();
			outSB.append(pboIndiv.getSetCode());
			outSB.append(" ");
			outSB.append(pboIndiv.getCardName());
			outSB.append(" can be sold for ");
			if(pboIndiv.getClanPrice() > pboIndiv.getDojoPrice())
			{
				outSB.append(pboIndiv.getClanPrice());
				outSB.append(" to Clan");
			}
			else if(pboIndiv.getClanPrice() < pboIndiv.getDojoPrice())
			{
				outSB.append(pboIndiv.getDojoPrice());
				outSB.append(" to Dojo");
			}
			else
			{
				outSB.append(pboIndiv.getClanPrice());
				outSB.append(" to either");
			}
			
			System.out.println(outSB.toString());
		}
	}
	
	public void getCardsBotsSellingBetween(double low, double high, HashMap<String, PriceInfo> clanMap, HashMap<String, PriceInfo> dojoMap)
	{
		//Issue in that if one bot sells for less than the low....then it won't select that card
		//from that bot, which would be a cheaper option
		
		HashMap<String, PriceInfo> clanMatchesMap = new HashMap<String, PriceInfo>();
		HashMap<String, PriceInfo> dojoMatchesMap = new HashMap<String, PriceInfo>();
		
		for(PriceInfo piClan : clanMap.values())
		{
			if(low <= piClan.getPriceToBuy() && high >= piClan.getPriceToBuy())
			{
				clanMatchesMap.put(piClan.getSetCode()+piClan.getCardName(), piClan);
			}
		}
		
		for(PriceInfo piDojo : dojoMap.values())
		{
			if(low <= piDojo.getPriceToBuy() && high >= piDojo.getPriceToBuy())
			{
				dojoMatchesMap.put(piDojo.getSetCode()+piDojo.getCardName(), piDojo);
			}
		}
		
		PriceInfo otherPi = null;
		HashMap<String, PriceBetweenObject> cheaperValueMap = new HashMap<String, PriceBetweenObject>();
		PriceBetweenObject pbo = null;
		for(Entry<String, PriceInfo> indivEntry : clanMatchesMap.entrySet())
		{
			if(dojoMatchesMap.containsKey(indivEntry.getKey()))
			{
				otherPi = dojoMatchesMap.get(indivEntry.getKey());
				pbo = new PriceBetweenObject();
				pbo.setSetCode(otherPi.getSetCode());
				pbo.setCardName(otherPi.getCardName());
				pbo.setClanPrice(indivEntry.getValue().getPriceToBuy());
				pbo.setDojoPrice(otherPi.getPriceToBuy());
				cheaperValueMap.put(indivEntry.getKey(), pbo);
			}
			else
			{
				pbo = new PriceBetweenObject();
				pbo.setSetCode(indivEntry.getValue().getSetCode());
				pbo.setCardName(indivEntry.getValue().getCardName());
				pbo.setClanPrice(indivEntry.getValue().getPriceToBuy());
				pbo.setDojoPrice(indivEntry.getValue().getPriceToBuy()+0.01);
				cheaperValueMap.put(indivEntry.getKey(), pbo);
			}
		}
		
		for(Entry<String, PriceInfo> indivEntry : dojoMatchesMap.entrySet())
		{
			if(!cheaperValueMap.containsKey(indivEntry.getKey()))
			{
				pbo = new PriceBetweenObject();
				pbo.setSetCode(indivEntry.getValue().getSetCode());
				pbo.setCardName(indivEntry.getValue().getCardName());
				pbo.setDojoPrice(indivEntry.getValue().getPriceToBuy());
				pbo.setClanPrice(indivEntry.getValue().getPriceToBuy()+0.01);
				cheaperValueMap.put(indivEntry.getKey(), pbo);
			}
		}
		
		StringBuffer outSB = null;
		for(PriceBetweenObject pboIndiv : cheaperValueMap.values())
		{
			outSB = new StringBuffer();
			outSB.append(pboIndiv.getSetCode());
			outSB.append(" ");
			outSB.append(pboIndiv.getCardName());
			outSB.append(" can be bought for ");
			if(pboIndiv.getClanPrice() < pboIndiv.getDojoPrice())
			{
				outSB.append(pboIndiv.getClanPrice());
				outSB.append(" from Clan");
			}
			else if(pboIndiv.getClanPrice() > pboIndiv.getDojoPrice())
			{
				outSB.append(pboIndiv.getDojoPrice());
				outSB.append(" from Dojo");
			}
			else
			{
				outSB.append(pboIndiv.getClanPrice());
				outSB.append(" from either");
			}
			
			System.out.println(outSB.toString());
		}
	}
	
	public void getInventoryPrice(String fileName, HashMap<String, PriceInfo> clanMap, HashMap<String, PriceInfo> dojoMap)
		throws Exception
	{	
		String line = null;
        FileReader fileReader = new FileReader(fileName);
        BufferedReader bufferedReader =  new BufferedReader(fileReader);
        
        
        StringBuffer fullCardList = new StringBuffer();
        String cardName = null;
        String quantity = null;
        String set = null;
        StringBuffer indivCard = null;
        boolean headerline = true;
        while((line = bufferedReader.readLine()) != null) 
        {
        	if(!headerline)
        	{
	        	indivCard = new StringBuffer(100);
	        	String[] splitOffName = line.split("\",");
	        	cardName = splitOffName[0].replaceAll("\"", "").replaceAll("Æ", "Ae");
	        	String[] dataSplit = splitOffName[1].split(",");
	        	quantity = dataSplit[0];
	        	set = dataSplit[3];
	        	indivCard.append(quantity);
	        	indivCard.append("@");
	        	indivCard.append(set);
	        	indivCard.append("@");
	        	indivCard.append(cardName);
	        	indivCard.append("@;");
	        	fullCardList.append(indivCard.toString());
        	}
        	else
        	{
        		headerline = false;
        	}
        }
        
        sellingCards(fullCardList.toString(), clanMap, dojoMap, false);
        
        bufferedReader.close();
	}
}

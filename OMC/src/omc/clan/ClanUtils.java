package omc.clan;

import java.util.HashMap;

import omc.PriceInfo;


public class ClanUtils 
{
	public HashMap<String, PriceInfo> processClanPrices(String inString, String setCode)
	{
		String dataOnly = stripOffStartAndEndChars(inString);
		return createHashMapOfObjects(dataOnly, setCode);
	}
	
	public HashMap<String, PriceInfo> processClanBooster(String inString)
	{
		String dataOnly = stripOffStartAndEndChars(inString);
		return createHashMapOfObjects(dataOnly, "BOOSTER");
	}
	
	private String stripOffStartAndEndChars(String inString)
	{
		String[] stripStart = inString.split("var d =\\s+\\[");
		String[] stripEnd = stripStart[1].split("\\s+\\$\\(");
		return stripEnd[0];
	}
	
	private HashMap<String, PriceInfo> createHashMapOfObjects(String inData, String setCode)
	{
		HashMap<String, PriceInfo> rtnMap = new HashMap<String, PriceInfo>();
		String nonPrices = inData.split("\\];")[0];
		String prices = inData.split("\\];")[1];
		String[] nonPricesSplitArray = nonPrices.split("\\],");
		String[] pricesSplitArray = prices.split(";");
		String[] indivDataSplitArray = null;
		PriceInfo pi = null;
		PriceInfo[] priceInfoArray = new PriceInfo[nonPricesSplitArray.length];
		try
		{
			for(int i=0; i < nonPricesSplitArray.length; i++)
			{
				pi = new PriceInfo();
				indivDataSplitArray = nonPricesSplitArray[i].split("\",");
				if(setCode.equalsIgnoreCase("BOOSTER"))
				{
					pi.setSetCode(stripOutBracketsAndQuotes(indivDataSplitArray[0]).replaceAll("\\s+", ""));
					pi.setCardName(stripOutBracketsAndQuotes(indivDataSplitArray[1].substring(1)));
				}
				else
				{
					pi.setSetCode(setCode);
					pi.setCardName(stripOutBracketsAndQuotes(indivDataSplitArray[0]).trim());
				}
				
				
				if(indivDataSplitArray[4].equalsIgnoreCase("null"))
				{
					pi.setHasStockToSell(false);
				}
				else
				{
					StringBuffer inventoryBotsBuff = new StringBuffer();
					pi.setHasStockToSell(true);
					if(indivDataSplitArray[4].replaceAll("\\]", "").trim().endsWith("\"") )
							//|| indivDataSplitArray[3].matches(".*]?s*"))
						//TODO: For some reason this regex match is matching on cards listed like CT(4),CT2(4)...so only outputting CT(4)
					{
						inventoryBotsBuff.append(stripOutBracketsAndQuotes(indivDataSplitArray[4]));
					}
					else
					{
						inventoryBotsBuff.append(stripOutBracketsAndQuotes(indivDataSplitArray[4]));
						boolean exitLoop = false;
						int j = 4;
						do
						{
							inventoryBotsBuff.append(", ");
							inventoryBotsBuff.append(stripOutBracketsAndQuotes(indivDataSplitArray[j]));
							if(indivDataSplitArray[j].contains("\""))
							{
								exitLoop = true;
							}
							j++;
						}while(!exitLoop);
					}
					
					pi.setInventoryBots(inventoryBotsBuff.toString());
				}
				
				priceInfoArray[i] = pi;
			}
			
			int arrayPos = 999;
			int priceType = 0;
			String positionInfo = "";
			double price = 0.0;
			for(int k = 0; k < pricesSplitArray.length; k++)
			{
				arrayPos = 999;
				priceType = 0;
				positionInfo = pricesSplitArray[k].split("=")[0];
				arrayPos = Integer.parseInt(positionInfo.split("\\[")[1].split("\\]")[0]);
				priceType = Integer.parseInt(positionInfo.split("\\[")[2].split("\\]")[0]);
				price = Double.valueOf(pricesSplitArray[k].split("=")[1]).doubleValue();
				if(priceType == 2)
				{
					priceInfoArray[arrayPos].setPriceToSell(price);
				}
				else if(priceType == 3)
				{
					priceInfoArray[arrayPos].setPriceToBuy(price);
				}
			}
			
			for(int m = 0; m < priceInfoArray.length; m++)
			{
				rtnMap.put(priceInfoArray[m].getSetCode() + priceInfoArray[m].getCardName(), priceInfoArray[m]);
			}
		}
		catch(Exception e)
		{
			System.out.println("Clan - Problem parsing card data: " + pi.getSetCode() + "-" + pi.getCardName());
		}
		
		return rtnMap;
	}
	
	private String stripOutBracketsAndQuotes(String inString)
	{
		String temp = inString.replaceAll("\\[", "");
		String temp2 = temp.replaceAll("\\]", "");
		return temp2.replaceAll("\"", "");
	}
}

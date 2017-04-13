package omc.dojo;

import java.util.HashMap;

import omc.PriceInfo;


public class DOJOUtils 
{
	public HashMap<String, PriceInfo> processDojoPrices(String inString)
	{
		String dataOnly = stripOffStartAndEndChars(inString);
		return createHashMapOfObjects(dataOnly);
	}
	
	private String stripOffStartAndEndChars(String inString)
	{
		String[] stripStart = inString.split(":\\s+\\[");
		String[] stripEnd = stripStart[1].split("\\]\\s+}");
		return stripEnd[0];
	}
	
	private HashMap<String, PriceInfo> createHashMapOfObjects(String inData)
	{
		HashMap<String, PriceInfo> priceMap = new HashMap<String, PriceInfo>();
		String[] fullDataSplitArray = inData.split("\"\\],");
		String[] indivDataSplitArray = null;
		String[] nameSplitArray = null;
		String[] indivPriceArray = null;
		PriceInfo pi = null;
		for(int i=0; i < fullDataSplitArray.length; i++)
		{
			try
			{
				pi = new PriceInfo();
				indivDataSplitArray = fullDataSplitArray[i].split("\",");
				pi.setSetCode(stripOutBracketsAndQuotes(indivDataSplitArray[0]));	
				if(indivDataSplitArray[2].contains("The foil version"))
				{
					nameSplitArray = indivDataSplitArray[2].split("priceTableCardLinkClicked\\(\\\\\"");
					pi.setCardName(nameSplitArray[1].replace("\\", ""));
					indivPriceArray = indivDataSplitArray[5].split(",");
				}
				else
				{
					nameSplitArray = indivDataSplitArray[2].split(">");
					pi.setCardName(stripNonNameCharacters(nameSplitArray[1]));					
					indivPriceArray = indivDataSplitArray[4].split(",");
				}
				
				//This means that bot does not have any to buy
				if(indivPriceArray[0].equals("\"-"))
				{
					pi.setPriceToBuy(0.00);
					if(indivPriceArray.length > 1)
					{
						if(indivPriceArray[1].equals("\"-"))
						{
							pi.setPriceToSell(0.00);
						}
						else
						{
							pi.setPriceToSell(Double.valueOf(indivPriceArray[1]).doubleValue());
						}
					}
					else
					{
						String[] indivPriceArray2 = indivDataSplitArray[5].split(",");
						if(indivPriceArray2[0].equals("\"-"))
						{
							pi.setPriceToBuy(0.00);
						}
						else
						{
							pi.setPriceToBuy(Double.valueOf(indivPriceArray2[0]).doubleValue());
						}
					}
					
					pi.setHasStockToSell(false);
				}
				else
				{
					int inventoryStartIndex = 5;
					boolean needInventoryStill = true;
					pi.setPriceToBuy(Double.valueOf(indivPriceArray[0]));
					StringBuffer inventoryBuffer = new StringBuffer();
					if(null!=indivPriceArray[1])
					{
						if(indivPriceArray[1].equalsIgnoreCase("\"-"))
						{
							pi.setPriceToSell(0.00);
						}
						else
						{
							pi.setPriceToSell(Double.valueOf(indivPriceArray[1]).doubleValue());
							if(null!=indivPriceArray[2])
							{
								if(!indivPriceArray[2].toUpperCase().contains("OUT OF STOCK"))
								{
									pi.setHasStockToSell(true);
									inventoryBuffer.append(inventoryBotNameStrip(indivPriceArray[2].split("</div>\\\\\">")[1]));
									if(indivPriceArray.length > 3)
									{
										pi.setHasStockToSell(true);
										for(int j=3; j < indivPriceArray.length; j++)
										{
											inventoryBuffer.append(",");
											inventoryBuffer.append(inventoryBotNameStrip(indivPriceArray[j]));
										}
									}
								}
								else
								{
									pi.setHasStockToSell(false);
								}

								needInventoryStill =  false;
							}
						}
					}
					else
					{
						if(indivPriceArray[1].equalsIgnoreCase("\"-"))
						{
							pi.setPriceToSell(0.00);
						}
						else
						{
							pi.setPriceToSell(Double.valueOf(indivDataSplitArray[5]).doubleValue());
							inventoryStartIndex = 6;
						}
					}
					
					if(needInventoryStill)
					{						
						if(indivDataSplitArray.length > 6)
						{
							inventoryBuffer.append(inventoryBotNameStrip(indivDataSplitArray[inventoryStartIndex]));
							//There is more inventory than just the buffer...
							for(int getAllInventory = inventoryStartIndex+1; 
									getAllInventory < indivDataSplitArray.length; getAllInventory++)
							{
								inventoryBuffer.append(",");
								inventoryBuffer.append(inventoryBotNameStrip(indivDataSplitArray[getAllInventory]));
							}
							pi.setHasStockToSell(true);
						}
						else
						{
							inventoryBuffer.append(inventoryBotNameStrip(indivDataSplitArray[inventoryStartIndex]));
							if(inventoryBuffer.toString().toUpperCase().contains("OUT OF STOCK"))
							{
								pi.setHasStockToSell(false);
							}
							else
							{
								pi.setHasStockToSell(true);
							}
						}
					}
						
					pi.setInventoryBots(inventoryBuffer.toString());
					priceMap.put(pi.getSetCode() + pi.getCardName(), pi);
				}
			}
			catch(Exception e)
			{
				//TODO: Currently failing on the when foil is cheaper than regular message...
				System.out.println("DOJO - Problem parsing card data: " + pi.getSetCode() + "-" + pi.getCardName());
			}
		}
		
		return priceMap;
	}
	
	private String stripOutBracketsAndQuotes(String inString)
	{
		String temp = inString.replaceAll("\\[", "");
		String temp2 = temp.replaceAll("\\]", "");
		return temp2.replaceAll("\"", "");
	}
	
	private String inventoryBotNameStrip(String inString)
	{		
		String temp = inString.replaceAll("\\[", "(");
		String temp2 = temp.replaceAll("\\]", ")");
		String temp3 = temp2.replaceAll("</span>", "");
		return temp3.replaceAll("\"", "");
	}
	
	private String stripNonNameCharacters(String inString)
	{
		return inString.replaceAll("</a", "");
	}
}

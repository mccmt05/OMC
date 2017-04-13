package omc;

public class PriceInfo 
{
	private String setCode;
	private String cardName;
	private double priceToBuy;
	private double priceToSell;
	private boolean hasStockToSell;
	private String inventoryBots;
	
	public String getSetCode() 
	{
		return setCode;
	}
	public void setSetCode(String setCode) 
	{
		this.setCode = setCode;
	}
	public String getCardName() 
	{
		return cardName;
	}
	public void setCardName(String cardName) 
	{
		this.cardName = cardName;
	}
	public double getPriceToBuy() 
	{
		return priceToBuy;
	}
	public void setPriceToBuy(double priceToBuy) 
	{
		this.priceToBuy = priceToBuy;
	}
	public double getPriceToSell() 
	{
		return priceToSell;
	}
	public void setPriceToSell(double priceToSell) 
	{
		this.priceToSell = priceToSell;
	}
	
	public boolean getHasStockToSell() 
	{
		return hasStockToSell;
	}
	public void setHasStockToSell(boolean hasStockToSell)
	{
		this.hasStockToSell = hasStockToSell;
	}
	
	public String getInventoryBots() 
	{
		return inventoryBots;
	}
	public void setInventoryBots(String inventoryBots) 
	{
		this.inventoryBots = inventoryBots;
	}
	
	public String toString()
	{
		StringBuffer rtnSB = new StringBuffer();
		rtnSB.append(getSetCode());
		rtnSB.append(", ");
		rtnSB.append(getCardName());
		rtnSB.append(", Buy @");
		rtnSB.append(getPriceToBuy());
		rtnSB.append(", Sell @");
		rtnSB.append(getPriceToSell());
		if(getHasStockToSell())
		{
			rtnSB.append(" . There is currently stock available to buy [");
			rtnSB.append(getInventoryBots());
			rtnSB.append("]");
		}
		else
		{
			rtnSB.append(" . There is currently no stock available to buy");
		}
		return rtnSB.toString();
	}
}

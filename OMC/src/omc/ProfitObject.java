package omc;

import java.text.DecimalFormat;

public class ProfitObject implements Comparable<ProfitObject>
{
	private String setCode;
	private String cardName;
	private String buyFromCode;
	private double buyPrice;
	private String sellToCode;
	private double sellPrice;
	private double profit;
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
	public String getBuyFromCode() 
	{
		return buyFromCode;
	}
	public void setBuyFromCode(String buyFromCode) 
	{
		this.buyFromCode = buyFromCode;
	}
	public double getBuyPrice() 
	{
		return buyPrice;
	}
	public void setBuyPrice(double buyPrice) 
	{
		this.buyPrice = buyPrice;
	}
	public String getSellToCode() 
	{
		return sellToCode;
	}
	public void setSellToCode(String sellToCode) 
	{
		this.sellToCode = sellToCode;
	}
	public double getSellPrice() 
	{
		return sellPrice;
	}
	public void setSellPrice(double sellPrice) 
	{
		this.sellPrice = sellPrice;
	}
	public double getProfit() 
	{
		return profit;
	}
	public void setProfit(double profit) 
	{
		this.profit = profit;
	}
	
	public String getInventoryBots() 
	{
		return inventoryBots;
	}
	public void setInventoryBots(String inventoryBots) 
	{
		this.inventoryBots = inventoryBots;
	}
	
	@Override
	public int compareTo(ProfitObject po) 
	{
		return Double.compare(this.getProfit(), po.getProfit());
	}
	
	public String toString()
	{
		StringBuffer rtnSB = new StringBuffer();
		rtnSB.append(getSetCode());
		rtnSB.append(", ");
		rtnSB.append(getCardName());
		rtnSB.append(" - Buy from ");
		rtnSB.append(getBuyFromCode());
		rtnSB.append("[");
		rtnSB.append(getInventoryBots());
		rtnSB.append("]");
		rtnSB.append(" for ");
		rtnSB.append(getBuyPrice());
		rtnSB.append(" and sell to ");
		rtnSB.append(getSellToCode());
		rtnSB.append(" for ");
		rtnSB.append(getSellPrice());
		rtnSB.append(" to make a profit of ");
		DecimalFormat threeDForm = new DecimalFormat("###.###");
		rtnSB.append(threeDForm.format(getProfit()));
		return rtnSB.toString();
	}
}

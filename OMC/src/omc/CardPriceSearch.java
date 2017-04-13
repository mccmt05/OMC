package omc;

public class CardPriceSearch 
{
	private int quantity;
	private String setCode;
	private String cardName;
	private double clanPrice;
	private double dojoPrice;
	
	public CardPriceSearch(String inData)
	{
		String[] dataArray = inData.split("@");
		this.setQuantity(Integer.parseInt(dataArray[0]));
		this.setSetCode(dataArray[1]);
		this.setCardName(dataArray[2]);
	}
	
	public int getQuantity() 
	{
		return quantity;
	}
	public void setQuantity(int quantity) 
	{
		this.quantity = quantity;
	}
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
	public double getClanPrice() 
	{
		return clanPrice;
	}
	public void setClanPrice(double clanPrice) 
	{
		this.clanPrice = clanPrice;
	}
	public double getDojoPrice() 
	{
		return dojoPrice;
	}
	public void setDojoPrice(double dojoPrice) 
	{
		this.dojoPrice = dojoPrice;
	}
}

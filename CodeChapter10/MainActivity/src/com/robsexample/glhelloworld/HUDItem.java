package com.robsexample.glhelloworld;



public class HUDItem 
{
	private boolean m_ItemValid;
	private String 	m_ItemName;
	private int 	m_NumericalValue;
	private String 	m_TextValue = null;
	private Vector3 m_ScreenPosition;

	private BillBoardCharacterSet m_Text;
	private Texture m_Icon;
	private BillBoard m_HUDImage;
	
	private boolean m_Dirty = false;
	
	private boolean m_IsVisible = true;
	
	HUDItem(String 	ItemName, 	
			int 	NumericalValue, 	
			Vector3 ScreenPosition,
			BillBoardCharacterSet Text,
			Texture Icon,
			BillBoard HUDImage)
	{
		m_ItemName 		= ItemName;
		m_NumericalValue= NumericalValue;
		m_ScreenPosition= ScreenPosition;
		m_Text 			= Text;
		m_Icon 			= Icon;
		m_HUDImage		= HUDImage;
	}
	
	
	void SetVisible(boolean Visible)
	{
		m_IsVisible = Visible;
	}
	
	boolean IsVisible()
	{
		return m_IsVisible;
	}
	
	void SetTextValue(String Value)
	{
		m_TextValue = Value;
		m_Dirty = true;
	}
	
	String GetTextValue()
	{
		return m_TextValue;
	}
	
	void SetDirty(boolean value)
	{
		m_Dirty = value;
	}
	
	boolean IsDirty()
	{
		return m_Dirty;
	}
	
	boolean IsValid()
	{
		return m_ItemValid;
	}
	
	// Get Functions
	String GetName()
	{
		return m_ItemName;
	}
	
	int GetNumbericalValue()
	{
		return m_NumericalValue;
	}

	Vector3 GetLocalScreenPosition()
	{
		return m_ScreenPosition;
	}

	BillBoardCharacterSet GetText()
	{
		return m_Text;
	}
	
	Texture GetIcon()
	{
		return m_Icon;
	}

	BillBoard GetHUDImage()
	{
		return m_HUDImage;
	}
	
	// Set Functions
	void SetItemValidState(boolean state)
	{
		m_ItemValid = state;
	}
	
	void SetItemName(String ItemName)
	{
		m_ItemName = ItemName;
	}
	
	void SetNumericalValue(int NumericalValue)
	{
		m_NumericalValue = NumericalValue;
	}

	void SetLocalScreenPosition(Vector3 ScreenPosition)
	{
		m_ScreenPosition = ScreenPosition;
	}
	
	void SetCharacterSet(BillBoardCharacterSet Text)
	{
		m_Text = Text;
	}
	
	void SetIcon(Texture Icon)
	{
		m_Icon = Icon;
	}

	void SetHudImage(BillBoard HUDImage)
	{
		m_HUDImage = HUDImage;
	}

}


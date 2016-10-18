package com.robsexample.glhelloworld;


enum EntryMenuStates
{
	None,
	NextCharacterPressed,
	PreviousCharacterPressed,
	Enter
}

public class HighScoreEntryMenu 
{
	// Maximum number of characters to enter for Player's name
	private int MAX_ENTRY_CHARACTERS = 3;
	
	// Entry
	// Current Character position in name/iniitials such as 1, 2,4
	private int m_EntryIndex = 0;	
	private char[] m_Entry = new char[MAX_ENTRY_CHARACTERS];
	
	// Buttons
	private MenuItem m_NextCharacterButton;
	private MenuItem m_PreviousCharacterButton;
	private MenuItem m_EnterButton;
	
	// Character Set for Initials
	private BillBoardCharacterSet m_Text;
	private int m_NumberCharactersInSet = 0;
	private int m_CharacterSetIndex = 0;
	private int m_FontWidth;
	private int m_FontHeight;
	
	// Contains Player Name Entry Area
	private BillBoard m_HighScoreEntryMenuImage;
	
	// True if Menu Texture needs to be updated
	private boolean m_Dirty = true;
	
	// Player Name position on texture of Menu
	private int m_StartingEntryPositionX;
	private int m_StartingEntryPositionY;
	
	private int m_CurrentEntryPositionX;
	private int m_CurrentEntryPositionY;
	
	
	// Entry Done
	private boolean m_EntryFinished = false;
	
	

	HighScoreEntryMenu(MenuItem NextCharacterButton,
					   MenuItem PreviousCharacterButton,
					   MenuItem EnterButton,
					   BillBoardCharacterSet Text,
					   BillBoard HighScoreEntryMenuImage,
					   int StartingEntryXPos,
					   int StartingEntryYPos)
	{
		m_NextCharacterButton = NextCharacterButton;
		m_PreviousCharacterButton = PreviousCharacterButton;
		m_EnterButton = EnterButton;
		m_Text = Text;
		m_HighScoreEntryMenuImage = HighScoreEntryMenuImage;
		   
		m_FontWidth = m_Text.GetFontWidth();
		m_FontHeight = m_Text.GetFontHeight();	
		
		m_NumberCharactersInSet = m_Text.GetNumberCharactersInSet();
		
		m_CurrentEntryPositionX = StartingEntryXPos;
		m_CurrentEntryPositionY = StartingEntryYPos;
		
		m_StartingEntryPositionX = StartingEntryXPos;
		m_StartingEntryPositionY = StartingEntryYPos;
		
		ResetMenu();
	}

	void ResetMenu()
	{
		m_CharacterSetIndex = 10;
		
		m_EntryIndex = 0;
	
		m_CurrentEntryPositionX = m_StartingEntryPositionX;
		m_CurrentEntryPositionY = m_StartingEntryPositionY;
	
		m_Text.SetText("...".toCharArray());
		m_Text.RenderToBillBoard(m_HighScoreEntryMenuImage, m_CurrentEntryPositionX, m_CurrentEntryPositionY);
		
		m_EntryFinished = false;
	}
	
	
	boolean IsEntryFinished()
	{
		return m_EntryFinished;
	}
	
	char[] GetEntry()
	{
		return m_Entry;
	}
	
	void ProcessEnterMenuSelection()
	{
		char EnteredChar = FindCurrentCharacter();
		
		m_Entry[m_EntryIndex] = EnteredChar;
		
		m_EntryIndex++;
		if (m_EntryIndex >= MAX_ENTRY_CHARACTERS)
		{
			m_EntryFinished = true;
		}
		
		m_CurrentEntryPositionX = m_CurrentEntryPositionX + m_FontWidth;
		
		m_Dirty = true;
	}
	
	void ProcessPreviousMenuSelection()
	{
		// Go to next character 
		m_CharacterSetIndex--;
				
		if (m_CharacterSetIndex < 0)
		{
			m_CharacterSetIndex = m_NumberCharactersInSet-1;
		}
		m_Dirty = true;
	}
	
	void ProcessNextMenuSelection()
	{
		// Go to next character 
		m_CharacterSetIndex++;
		
		if (m_CharacterSetIndex >= m_NumberCharactersInSet)
		{
			m_CharacterSetIndex = 0;
		}
		m_Dirty = true;
	}
	
	void RenderTextToMenu(String Character, int XPos, int YPos)
	{
		m_Text.SetText(Character.toCharArray());
		m_Text.RenderToBillBoard(m_HighScoreEntryMenuImage, XPos , YPos);
	}

	char FindCurrentCharacter()
	{
		BillBoardFont Font = m_Text.GetCharacter(m_CharacterSetIndex);
		
		return Font.GetCharacter();
	}
	
	void RenderEntryToMenu()
	{
		char CurrentCharacter = FindCurrentCharacter();
		String StringCharacter = CurrentCharacter + "";
		
		RenderTextToMenu(StringCharacter, m_CurrentEntryPositionX, m_CurrentEntryPositionY);
	}
	
	EntryMenuStates GetEntryMenuStatus(float TouchX, float TouchY,
			 					  	   int ViewPortWidth,
			 					  	   int ViewPortHeight)
	{
		EntryMenuStates Selection = EntryMenuStates.None;

		boolean Touched = false;

		// Next character Menu Item
		Touched = m_NextCharacterButton.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);	 			   				
		if (Touched)
		{
			Selection = EntryMenuStates.NextCharacterPressed;
		}

		// Previous character Menu Item
		Touched =  m_PreviousCharacterButton.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);
		if (Touched)
		{
			Selection = EntryMenuStates.PreviousCharacterPressed;
		}

		// Enter Menu Item
		Touched =  m_EnterButton.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);
		if (Touched)
		{
			Selection = EntryMenuStates.Enter;
		}

		return Selection;
	}

	void UpdateHighScoreEntryMenu(Camera Cam)
	{
		//Update Menu Texture if changed
		if (m_Dirty)
		{
			// If need to alter Menu texture then render new texture data
			RenderEntryToMenu();			
			m_Dirty = false;
		}
	
		// Update Buttons
		m_NextCharacterButton.UpdateObject3d(Cam);
		m_PreviousCharacterButton.UpdateObject3d(Cam);
		m_EnterButton.UpdateObject3d(Cam);
		
		// Update Initial Entry Area
		m_HighScoreEntryMenuImage.UpdateObject3d(Cam);
		
	}
	
	void RenderHighScoreEntryMenu(Camera Cam, PointLight Light, boolean DebugOn)
	{
		// Render Buttons
		m_NextCharacterButton.DrawObject(Cam, Light);
		m_PreviousCharacterButton.DrawObject(Cam, Light);
		m_EnterButton.DrawObject(Cam, Light);
		
		// Render Billboard with Entry Menu info
		m_HighScoreEntryMenuImage.DrawObject(Cam, Light);
	}
	
}

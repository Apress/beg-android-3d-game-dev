package com.robsexample.glhelloworld;



enum MenuStates
{
	None,
	NewGame,
	ContinueCurrentGame,
	HighScoreTable,
	Copyright
}

public class MainMenu 
{
	// Menu Selections
	MenuItem m_NewGameItem;
	MenuItem m_ContinueGameItem;
	MenuItem m_HighScoresItem;
	MenuItem m_CopyRightItem;
	

		
	MainMenu(MenuItem NewGameItem,
			 MenuItem ContinueGameItem,
			 MenuItem HighScoresItem,
			 MenuItem CopyRightItem)
	{
		m_NewGameItem		= NewGameItem;
		m_ContinueGameItem	= ContinueGameItem;
		m_HighScoresItem	= HighScoresItem;
		m_CopyRightItem 	= CopyRightItem;
	}

	MenuStates GetMainMenuStatus(float TouchX, float TouchY,
								 int ViewPortWidth,
		  						 int ViewPortHeight)
	{
		MenuStates Selection = MenuStates.None;
		
		boolean Touched = false;
		
		// New Game Menu Item
		Touched = m_NewGameItem.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);	 			   				
		if (Touched)
		{
			Selection = MenuStates.NewGame;
		}
		
		// New ContinueGame Menu Item
		Touched =  m_ContinueGameItem.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);
		if (Touched)
		{
			Selection = MenuStates.ContinueCurrentGame;
		}
				
		// New HighScoreTable Menu Item
		Touched =  m_HighScoresItem.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);
		if (Touched)
		{
			Selection = MenuStates.HighScoreTable;
		}
		
		// CopyRight Menu Item
		Touched =  m_CopyRightItem.Touched(TouchX, TouchY, ViewPortWidth, ViewPortHeight);
		if (Touched)
		{
			Selection = MenuStates.Copyright;
		}
				
		
	
		return Selection;
	}
	
	void RenderMenu(Camera Cam, PointLight Light, boolean DebugOn)
	{
		m_NewGameItem.DrawObject(Cam, Light);
		m_ContinueGameItem.DrawObject(Cam, Light);
		m_HighScoresItem.DrawObject(Cam, Light);
		m_CopyRightItem.DrawObject(Cam, Light);
	}
	
	void UpdateMenu(Camera Cam)
	{
		m_NewGameItem.UpdateObject3d(Cam);
		m_ContinueGameItem.UpdateObject3d(Cam);
		m_HighScoresItem.UpdateObject3d(Cam);
		m_CopyRightItem.UpdateObject3d(Cam);
	}
	
}

package com.robsexample.glhelloworld;

import java.util.Arrays;
import java.util.Collections;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;


import android.util.Log;


public class HighScoreTable 
{

	// Holds Player's High Scores
	
		// Create new HighScoreEntry record for a new high score
		// Add to HighScoreTable
		// Sort HighScore Table according to score in ascending order
		// Render top high scores to the Billboard representing the top player scoreboard
		// Ajust Billboard Orientation so that it faces camera
		// Render Final Billboard to screen.
		
		private Context m_Context;
		
		private String HIGH_SCORES = "HighScores";
		private int MAX_RANK = 10;
		private int MAX_SCORES = 11;
		private HighScoreEntry[] m_HighScoreTable = new HighScoreEntry[MAX_SCORES];
		
		private BillBoardCharacterSet m_Text;
		private int m_FontWidth;
		private int m_FontHeight;
		
		private Texture m_BackGroundTexture;
		private BillBoard m_HighScoreTableImage;
		
		private boolean m_Dirty = false;
		
	
		HighScoreTable(Context iContext,
					   BillBoardCharacterSet CharacterSet,
					   BillBoard HighScoreTableImage)
		{
			m_Context = iContext;
			m_BackGroundTexture = new Texture(iContext, R.drawable.background);
			
			String 	Initials 		= "AAA";
			int 	Score			= 0;
			
			// Initialize High Score Entries
			for (int i = 0; i < MAX_SCORES; i++)
			{
				m_HighScoreTable[i] = new HighScoreEntry(Initials,Score);
				m_HighScoreTable[i].SetItemValidState(false);
			}
			
			m_Text = CharacterSet;
			m_FontWidth = m_Text.GetFontWidth();
			m_FontHeight = m_Text.GetFontHeight();
			
			m_HighScoreTableImage = HighScoreTableImage;
			
			
			// Load In Saved high Scores
			LoadHighScoreTable(HIGH_SCORES);
			m_Dirty = true;
		}
		
		void SaveHighScoreTable(String Handle)
		{
			// We need an Editor object to make preference changes.
		    // All objects are from android.context.Context
		    SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		    SharedPreferences.Editor editor = settings.edit();
		      
		    for (int i = 0; i < MAX_RANK; i++)
		    {
		    	editor.putString("Name" + i, m_HighScoreTable[i].GetInitials());
		    	editor.putInt("Score" + i, m_HighScoreTable[i].GetScore());
		    	
		    }
		   
		    // Commit the edits!
		    editor.commit();
		}
		
		void LoadHighScoreTable(String Handle)
		{
			// Restore preferences
		    SharedPreferences settings = m_Context.getSharedPreferences(Handle, 0);
		
		    for (int i = 0; i < MAX_RANK; i++)
		    {
		    	String Name = settings.getString("Name" + i, "...");
		    	int Score = settings.getInt("Score" + i, 0);
		    
		    	m_HighScoreTable[i].SetName(Name);
		    	m_HighScoreTable[i].SetScore(Score);
		   
		    	if (Score > 0)
		    	{
		    		m_HighScoreTable[i].SetItemValidState(true);
		    	}
		    }
		}

		int NumberValidHighScores()
		{
			int NumberValidScores = 0;
			for (int i = 0; i < MAX_RANK; i++)
			{
				if (m_HighScoreTable[i].IsValid())
				{
					NumberValidScores++;
				}
			}
			return NumberValidScores;
		}
		
		int MaxNumberHighScores()
		{
			return MAX_RANK;
		}
		
		int GetLowestScore()
		{
			// Get Lowest valid score
			int LowestScore = 0;
			int ValidScores = 0;
			
			for (int i = 0; i < MAX_RANK; i++)
			{
				if (m_HighScoreTable[i].IsValid())
				{
					ValidScores++;
				}
			}
			
			if (ValidScores > 0)
			{
				LowestScore = m_HighScoreTable[ValidScores-1].GetScore();
			}
			
			return LowestScore;
		}
		
		int FindEmptySlot()
		{
			int EmptySlot = -1;
			for (int i = 0; i < MAX_SCORES; i++)
			{
				if (m_HighScoreTable[i].IsValid() == false)
				{
					return i;
				}
			}
			
			return EmptySlot;
		}

		boolean AddItem(HighScoreEntry Item)
		{
			boolean result = false;
			
			int EmptySlot =  FindEmptySlot();
			if (EmptySlot >= 0)
			{
				m_HighScoreTable[EmptySlot] = Item;	
				m_HighScoreTable[EmptySlot].SetItemValidState(true);
				result = true;
				m_Dirty = true;
			}
			return result;
		}
		
		void SortHighScoreTable()
		{
			Collections.sort(Arrays.asList(m_HighScoreTable));
			
			// Only keep top 10 and make room for another to be added to end of array
			m_HighScoreTable[MAX_SCORES-1].SetItemValidState(false);
		}
		
		void ClearHighScoreTable()
		{
			Texture HighScoreTableTexture = m_HighScoreTableImage.GetTexture(0);
			
			// Clear Composite Texture;
			Bitmap BackGroundBitmap = m_BackGroundTexture.GetTextureBitMap();
			HighScoreTableTexture.CopySubTextureToTexture(0, 0, 0, BackGroundBitmap);	
		}
		
		void RenderTitle()
		{
			m_Text.SetText("High".toCharArray());
			m_Text.RenderToBillBoard(m_HighScoreTableImage, 0 , 0);
			
			m_Text.SetText("Scores".toCharArray());
			m_Text.RenderToBillBoard(m_HighScoreTableImage, 5*m_FontWidth , 0);
		}
		
		void CopyHighScoreEntryToHighScoreTable(int Rank, Camera Cam, HighScoreEntry Item)
		{
			// Put HighScore Entry onto Final Composite Bitmap
			
			// CharacterPosition
			int HeightOffset = 10;
			int CharPosX = 0; 
			int CharPosY = m_FontHeight + (Rank * (m_FontHeight + HeightOffset)); 
			
			// Render Rank
			String RankStr = Rank + ".";
			m_Text.SetText(RankStr.toCharArray());
			m_Text.RenderToBillBoard(m_HighScoreTableImage, CharPosX, CharPosY);
			
			// Render Player Name/Initials and render to composite billboard
			String Name = Item.GetInitials();
			m_Text.SetText(Name.toCharArray());
			
			CharPosX = CharPosX + m_FontWidth * 3;
			m_Text.RenderToBillBoard(m_HighScoreTableImage, CharPosX, CharPosY);
			
			// Render Numerical Value and render to composite billboard
			String Score = String.valueOf(Item.GetScore()); 
			m_Text.SetText(Score.toCharArray());
			
			int BlankSpace = 4 * m_FontWidth;
			CharPosX = CharPosX + Name.length() + BlankSpace;
			m_Text.RenderToBillBoard(m_HighScoreTableImage, CharPosX, CharPosY);
		}

		void UpdateHighScoreTable(Camera Cam)
		{
			if (m_Dirty)
			{
				// Sort High Score Table in ascending order for score
				SortHighScoreTable();
			
				// Clear High Score Table and set background texture
				ClearHighScoreTable();
			
				// Render Title
				RenderTitle();
			
				// For the Top Ranked entries copy these to the HighScore Table BillBoard
				for (int i = 0; i < MAX_RANK; i++)
				{
					if (m_HighScoreTable[i].IsValid())
					{
						CopyHighScoreEntryToHighScoreTable(i+1, Cam, m_HighScoreTable[i]);
					}
				}
			
				// Save High Scores
				Log.e("HIGHSCORETABLE","SAVING HIGH SCORES!!!!");
				SaveHighScoreTable(HIGH_SCORES);
				
				m_Dirty = false;
			}
			
			// Update BillBoard orientation for Score
			m_HighScoreTableImage.UpdateObject3d(Cam);
		}
		
		void RenderHighScoreTable(Camera Cam, PointLight Light, boolean DebugOn)
		{
			// Render Final High Score Table Composite Image 
			m_HighScoreTableImage.DrawObject(Cam, Light);	
		}
		
		
		
		
	
	
	
	
}

package com.robsexample.glhelloworld;




import android.util.Log;


import android.graphics.Bitmap;


public class BillBoardCharacterSet 
{
	// Holds character fonts for use with opengl
	// Use Settext() to set the text you want to display then use RenderToBillBoard to put this text on
	// the input BillBoard object's texture
	
	// Total Character Set
	static int MAX_CHARACTERS = 50;
	private int m_NumberCharacters = 0; // Number characters in the character set
	private BillBoardFont[] m_CharacterSet = new BillBoardFont[MAX_CHARACTERS];
	
	// Current Text
	private int MAX_CHARACTERS_TEXT = 100;
	
	private char[] m_Text = new char[MAX_CHARACTERS_TEXT];
	private BillBoardFont[] m_TextBillBoard = new BillBoardFont[MAX_CHARACTERS_TEXT];

	BillBoardCharacterSet()
	{

	}
	
	
	int GetNumberCharactersInSet()
	{
		return m_NumberCharacters;
	}
	
	BillBoardFont GetCharacter(int index)
	{
		BillBoardFont Font = null;
		
		if (index < m_NumberCharacters)
		{
			Font = m_CharacterSet[index];
		}
	
		return Font;
	}
	
	int GetFontWidth()
	{
		int Width = 0;
		if (m_NumberCharacters > 0)
		{
			BillBoardFont Character = m_CharacterSet[0];
			Texture Tex = Character.GetTexture(0);
			Bitmap Image = Tex.GetTextureBitMap();
			Width = Image.getWidth();
		}
		
		return Width;
	}
	
	int GetFontHeight()
	{
		int Height = 0;
		if (m_NumberCharacters > 0)
		{
			BillBoardFont Character = m_CharacterSet[0];
			Texture Tex = Character.GetTexture(0);
			Bitmap Image = Tex.GetTextureBitMap();
			Height = Image.getHeight();
		}
		
		return Height;
	}

	boolean AddToCharacterSet(BillBoardFont Character)
	{
		if (m_NumberCharacters < MAX_CHARACTERS)
		{
			m_CharacterSet[m_NumberCharacters] = Character;
			m_NumberCharacters++;
			return true;
		}
		else
		{
			Log.e("BILLBOARD CHARACTER SET" , "NOT ENOUGH ROOM TO ADD ANOTHER CHARACTER TO CHARACTER SET");
			return false;
		}
	}
	
	BillBoardFont FindBillBoardCharacter(char character)
	{
		BillBoardFont Font = null;
		
		for (int i = 0; i < m_NumberCharacters; i++)
		{
			if (m_CharacterSet[i].IsFontCharacter(character))
			{
				Font = m_CharacterSet[i];
			}
		}
		
		return Font;
	}
	
	void SetText(char[] Text)
	{
		String TextStr = new String(Text);
		TextStr = TextStr.toLowerCase();
		
		m_Text = TextStr.toCharArray();
			
		for (int i = 0; i < m_Text.length; i++)
		{
			BillBoardFont Character = FindBillBoardCharacter(m_Text[i]);
			if (Character != null)
			{
				m_TextBillBoard[i] = Character;
			}
			else
			{
				Log.e("CHARACTER SET ERROR" , "SETTEXT ERROR , " + m_Text[i] + " NOT FOUND!!!!!");
			}
		}
	}

	void DrawFontToComposite(BillBoardFont Obj, int X, int Y, BillBoard Composite)
	{
		Texture TexSource = Obj.GetTexture(0);
		Bitmap BitmapSource = TexSource.GetTextureBitMap();
		int BitmapSourceWidth = BitmapSource.getWidth();
		
		Texture TexDest = Composite.GetTexture(0);
		Bitmap BitmapDest = TexDest.GetTextureBitMap();
		int BitmapDestWidth = BitmapDest.getWidth();
		
		// Put Sub Image on Composite
		int XEndTexture = X + BitmapSourceWidth;
		if (XEndTexture >= BitmapDestWidth)
		{
			Log.e("BillBoardCharacterSet::DrawFontToComposite" , "ERROR Overwriting Dest Texture, Last X Position To Write = " +
		             XEndTexture + ", Max Destination Width = " + BitmapDestWidth);
		}
		else
		{
			TexDest.CopySubTextureToTexture(0, X, Y, BitmapSource);
		}
	}
	
	void RenderToBillBoard(BillBoard Composite, int XOffset, int YOffset)
	{	
		int Length = m_Text.length;
		for (int i = 0; i < Length; i++)
		{
			BillBoardFont Character = m_TextBillBoard[i];
			if (Character != null)
			{		
				// Draw this font to the composite by copying the bitmap image data
				Texture Tex = Character.GetTexture(0);
				Bitmap Image = Tex.GetTextureBitMap();
				int Width = Image.getWidth();
				int XCompositeOffset = XOffset + (Width * i);
			
				DrawFontToComposite(Character, XCompositeOffset, YOffset, Composite);
			}
		}
	}
	
	
}

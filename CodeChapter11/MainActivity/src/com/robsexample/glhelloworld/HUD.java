package com.robsexample.glhelloworld;



import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.content.Context;


public class HUD 
{
	private int MAX_HUDITEMS = 10;
	private HUDItem[] m_HUDItems = new HUDItem[MAX_HUDITEMS];
	private Texture m_BlankTexture;

	HUD(Context iContext)
	{
		m_BlankTexture = new Texture(iContext, R.drawable.blankhud);
		
		String 	ItemName 		= "NONE";
		int 	NumericalValue	= 0;
		Vector3 ScreenPosition	= null;

		BillBoardCharacterSet CharacterSet = null;
		Texture Icon 		= null;
		BillBoard HUDImage 	= null;
		
		// Initialize m_HUDItems
		for (int i = 0; i < MAX_HUDITEMS; i++)
		{
			m_HUDItems[i] = new HUDItem(ItemName, 	
										NumericalValue, 	
										ScreenPosition, 
										CharacterSet,
										Icon,
										HUDImage);
			m_HUDItems[i].SetItemValidState(false);
		}
	}
	
	int FindEmptyHUDItemSlot()
	{
		int EmptySlot = -1;
		for (int i = 0; i < MAX_HUDITEMS; i++)
		{
			if (m_HUDItems[i].IsValid() == false)
			{
				return i;
			}
		}
		
		return EmptySlot;
	}
	
	boolean AddHUDItem(HUDItem Item)
	{
		boolean result = false;
		
		int EmptySlot =  FindEmptyHUDItemSlot();
		if (EmptySlot >= 0)
		{
			m_HUDItems[EmptySlot] = Item;	
			m_HUDItems[EmptySlot].SetItemValidState(true);
			m_HUDItems[EmptySlot].SetDirty(true);
			result = true;
		}
		return result;
	}

	int FindHUDItem(String ID)
	{
		int Slot = -1;
		for (int i = 0; i < MAX_HUDITEMS; i++)
		{
			if ((m_HUDItems[i].GetName() == ID) &&
				(m_HUDItems[i].IsValid()))
			{
				Slot = i;
			}
		}
	
		return Slot;
	}
	
	HUDItem GetHUDItem(String ItemID)
	{
		HUDItem Item = null;
		int Slot = FindHUDItem(ItemID);
		
		if (Slot >= 0)
		{
			Item = m_HUDItems[Slot];
		}
		return Item;
	}

	boolean DeleteHUDItem(String ItemName)
	{
		boolean result = false;
		int Slot =  FindHUDItem(ItemName);
		if (Slot >= 0)
		{
			m_HUDItems[Slot].SetItemValidState(false);
			result = true;
		}
		return result;
	}
	
	void UpdateHUDItemNumericalValue(String ID, int NumericalValue)
	{
		int Slot =  FindHUDItem(ID);
		
		HUDItem HItem = m_HUDItems[Slot];
		
		if (HItem != null)
		{ 	
			// Update Key fields in HUDItem	
			HItem.SetNumericalValue(NumericalValue);
			HItem.SetDirty(true);
		}
	}

	void UpdateHUDItem(Camera Cam, HUDItem Item)
	{
		// Update HUDItem position and rotation in the 3d world
		// to face the camera.
    	Vector3 PositionLocal = Item.GetLocalScreenPosition();
    	Vector3 PositionWorld = new Vector3(0,0,0);
        
        Vector3 CamPos = new Vector3(Cam.GetCameraEye().x, 
        							 Cam.GetCameraEye().y,
        							 Cam.GetCameraEye().z);
        Vector3 CameraForward = Cam.GetOrientation().GetForwardWorldCoords();
		Vector3 CameraUp = Cam.GetOrientation().GetUpWorldCoords();
		Vector3 CameraRight = Cam.GetOrientation().GetRightWorldCoords(); //Vector3.CrossProduct(CameraForward, CameraUp);
        
		// Local Camera Offsets
		Vector3 CamHorizontalOffset = Vector3.Multiply(PositionLocal.x, CameraRight);
		Vector3 CamVerticalOffset = Vector3.Multiply(PositionLocal.y, CameraUp);
		
		float ZOffset = Cam.GetProjNear() + PositionLocal.z;
		Vector3 CamDepthOffset = Vector3.Multiply(ZOffset, CameraForward);
		
		// Create Final PositionWorld Vector
		PositionWorld = Vector3.Add(CamPos, CamHorizontalOffset);
		PositionWorld = Vector3.Add(PositionWorld, CamVerticalOffset);
		PositionWorld = Vector3.Add(PositionWorld, CamDepthOffset); 
        
		// Put images from icon and numerical data onto the composite hud texture
		BillBoard 	HUDComposite 		= Item.GetHUDImage();
		Texture 	HUDCompositeTexture = HUDComposite.GetTexture(0);
		Bitmap 		HUDCompositeBitmap 	= HUDCompositeTexture.GetTextureBitMap();
		
		BillBoardCharacterSet Text = Item.GetText();
		
		int FontWidth = Text.GetFontWidth();
		
		Texture Icon = Item.GetIcon();
		int IconWidth = 0;
		
		if (Item.IsDirty())
		{
			// Clear Composite Texture;
			Bitmap BlankBitmap = m_BlankTexture.GetTextureBitMap();
			HUDCompositeTexture.CopySubTextureToTexture(0, 0, 0, BlankBitmap);
					
			if (Icon != null)
			{
				// Draw Icon on composite
				Bitmap HealthBitmap = Icon.GetTextureBitMap();
				IconWidth = HealthBitmap.getWidth();
				HUDCompositeTexture.CopySubTextureToTexture(0,0,0, HealthBitmap);
			}
			
			// Update Numerical Value and render to composite billboard
			String text = String.valueOf(Item.GetNumbericalValue()); 
			Text.SetText(text.toCharArray());
			Text.RenderToBillBoard(HUDComposite, IconWidth, 0);
			
			
			
			// Update Text Value and render to composite billboard
			String TextValue = Item.GetTextValue();
			if (TextValue != null)
			{
				int XPosText = IconWidth + (text.length() * FontWidth); 
				Text.SetText(TextValue.toCharArray());
				Text.RenderToBillBoard(HUDComposite, XPosText, 0);
			}
        
        	Item.SetDirty(false);
		}
	
        HUDComposite.m_Orientation.GetPosition().Set(PositionWorld.x, PositionWorld.y, PositionWorld.z);
       
        // Update BillBoard orientation for Score
        HUDComposite.UpdateObject3d(Cam);
 
	}

	void UpdateHUD(Camera Cam)
	{
		for (int i = 0; i < MAX_HUDITEMS; i++)
		{
			if (m_HUDItems[i].IsValid() && m_HUDItems[i].IsVisible())
			{
				UpdateHUDItem(Cam,m_HUDItems[i]);
			}
		}
	}
	
	void RenderHUD(Camera Cam, PointLight light)
	{
		for (int i = 0; i < MAX_HUDITEMS; i++)
		{
			if (m_HUDItems[i].IsValid()&& m_HUDItems[i].IsVisible())
			{
				HUDItem Item = m_HUDItems[i];
				BillBoard HUDComposite = Item.GetHUDImage();
				//HUDComposite.DrawObject(Cam, light, false);
				HUDComposite.DrawObject(Cam, light);
				
			}
		}
	}
	
}

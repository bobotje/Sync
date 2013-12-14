package sync.common.tileentity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import sync.common.core.SessionState;
import sync.common.item.ChunkLoadHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityShellConstructor extends TileEntityDualVertical 
{
	public float constructionProgress;
	
	public int doorTime;
	
	public boolean doorOpen;

	public TileEntityShellConstructor()
	{
		super();
		
		constructionProgress = 0.0F;
		
		doorTime = 0;
		
		doorOpen = false;
	}
	
	@Override
	public void updateEntity()
	{
		super.updateEntity();
		
		if(top && pair != null)
		{
			constructionProgress = ((TileEntityShellConstructor)pair).constructionProgress;
			doorOpen = ((TileEntityShellConstructor)pair).doorOpen;
		}
		if(isPowered())
		{
			constructionProgress += powerAmount();
			if(constructionProgress > SessionState.shellConstructionPowerRequirement)
			{
				constructionProgress = SessionState.shellConstructionPowerRequirement;
			}
			
			if(worldObj.isRemote && !top)
			{
				spawnParticles();
			}
		}
		if(!top)
		{
			if(doorOpen)
			{
				if(doorTime < TileEntityShellStorage.animationTime)
				{
					doorTime++;
				}
				List list = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
				if(list.isEmpty())
				{
					doorOpen = false;
					
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
			else
			{
				if(doorTime > 0)
				{
					doorTime--;
				}
			}
			if(!worldObj.isRemote && constructionProgress > 0.0F && !ChunkLoadHandler.shellTickets.containsKey(this))
			{
				ChunkLoadHandler.addShellAsChunkloader(this);
			}
		}
	}
	
	public void setup(TileEntityDualVertical scPair, boolean isTop, int placeYaw)
	{
		pair = scPair;
		top = isTop;
		face = placeYaw;
	}
	
	public boolean isPowered()
	{
		if(top && pair != null)
		{
			return ((TileEntityShellConstructor)pair).isPowered();
		}
		if(playerName.equalsIgnoreCase(""))
		{
			return false;
		}
		return true;
	}
	
	@Override
	public float powerAmount()
	{
		return 40F;
	}

	@Override
	public float getBuildProgress()
	{
		return constructionProgress;
	}
	
	@SideOnly(Side.CLIENT)
	public void spawnParticles()
	{
//		float prog = MathHelper.clamp_float(this.constructionProgress, 0.0F, SessionState.shellConstructionPowerRequirement) / (float)SessionState.shellConstructionPowerRequirement;
//		if(prog > 0.95F)
//		{
//			float angle = 0;
//			
//			System.out.println(face);
//			Minecraft.getMinecraft().effectRenderer.addEffect(new EntityPaintFX(worldObj, xCoord + 0.5D , yCoord + 0.5D, zCoord + 0.5D, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 1.0F));
//		}
	}

	@Override
    public void writeToNBT(NBTTagCompound tag)
    {
		super.writeToNBT(tag);
		tag.setFloat("constructionProgress", constructionProgress);
		tag.setBoolean("doorOpen", doorOpen);
    }
	 
	@Override
    public void readFromNBT(NBTTagCompound tag)
    {
		super.readFromNBT(tag);
		constructionProgress = tag.getFloat("constructionProgress");
		doorOpen = tag.getBoolean("doorOpen");
		
		resync = true;
    }
	
}
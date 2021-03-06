package bq_standard.client.gui.tasks;

import betterquesting.api.client.gui.GuiElement;
import betterquesting.api.client.gui.misc.IGuiEmbedded;
import betterquesting.api.utils.RenderUtils;
import bq_standard.tasks.TaskMeeting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiTaskMeeting extends GuiElement implements IGuiEmbedded
{
	private final TaskMeeting task;
	private final Minecraft mc;
	private Entity target;
	
	private final int posX;
	private final int posY;
	private final int sizeX;
	private final int sizeY;
	
	public GuiTaskMeeting(TaskMeeting task, int posX, int posY, int sizeX, int sizeY)
	{
		this.mc = Minecraft.getMinecraft();
		this.task = task;
		this.posX = posX;
		this.posY = posY;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	@Override
	public void drawBackground(int mx, int my, float partialTick)
	{
		if(target != null)
		{
			GlStateManager.pushMatrix();
			
			GlStateManager.scale(1F, 1F, 1F);
			GlStateManager.color(1F, 1F, 1F, 1F);
			
			float angle = (float)(Minecraft.getSystemTime()%30000D / 30000D * 360D);
			float scale = 64F;
			
			if(target.height * scale > (sizeY - 48))
			{
				scale = (sizeY - 48)/target.height;
			}
			
			if(target.width * scale > sizeX)
			{
				scale = sizeX/target.width;
			}
			
			RenderUtils.RenderEntity(posX + sizeX/2, posY + sizeY/2 + MathHelper.ceil(target.height/2F*scale) + 8, (int)scale, angle, 0F, target);
			
			GlStateManager.popMatrix();
		} else
		{
			if(EntityList.isRegistered(new ResourceLocation(task.idName)))
			{
				target = EntityList.createEntityByIDFromName(new ResourceLocation(task.idName), mc.world);
				target.readFromNBT(task.targetTags);
			}
		}
		
		String tnm = target != null? target.getName() : task.idName;
		String txt = I18n.format("bq_standard.gui.meet", tnm) + " x" + task.amount;
		mc.fontRenderer.drawString(txt, posX + sizeX/2 - mc.fontRenderer.getStringWidth(txt)/2, posY, getTextColor());
	}

	@Override
	public void drawForeground(int mx, int my, float partialTick)
	{
	}

	@Override
	public void onMouseClick(int mx, int my, int click)
	{
	}

	@Override
	public void onMouseScroll(int mx, int my, int scroll)
	{
	}

	@Override
	public void onKeyTyped(char c, int keyCode)
	{
	}
	
}

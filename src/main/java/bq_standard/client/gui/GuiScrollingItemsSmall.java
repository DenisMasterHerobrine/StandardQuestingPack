package bq_standard.client.gui;

import betterquesting.api.client.gui.GuiElement;
import betterquesting.api.client.gui.lists.GuiScrollingBase;
import betterquesting.api.client.gui.lists.IScrollingEntry;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.oredict.OreDictionary;

public class GuiScrollingItemsSmall extends GuiScrollingBase<GuiScrollingItemsSmall.ScrollingEntryItem>
{
	private final Minecraft mc;
	
	public GuiScrollingItemsSmall(Minecraft mc, int x, int y, int w, int h)
	{
		super(mc, x, y, w, h);
		this.mc = mc;
		this.allowDragScroll(true);
	}
	
	public void addItem(BigItemStack stack)
	{
		addItem(stack, stack.getBaseStack().getDisplayName());
	}
	
	public void addItem(BigItemStack stack, String description)
	{
		this.getEntryList().add(new ScrollingEntryItem(mc, stack, description));
	}
	
	public static class ScrollingEntryItem extends GuiElement implements IScrollingEntry
	{
		private final Minecraft mc;
		private BigItemStack stack;
		private String desc = "";
		
		private NonNullList<ItemStack> subStacks = NonNullList.<ItemStack>create();
		
		public ScrollingEntryItem(Minecraft mc, BigItemStack stack, String desc)
		{
			this.mc = mc;
			this.stack = stack;
			
			this.setDescription(desc);
			
			if(stack == null)
			{
				return;
			}
			
			if(stack.oreDict != null && stack.oreDict.length() > 0)
			{
				for(ItemStack oreStack : OreDictionary.getOres(stack.oreDict))
				{
					if(oreStack == null)
					{
						continue;
					}
					
					Item oItem = oreStack.getItem();
					
					NonNullList<ItemStack> tmp = NonNullList.<ItemStack>create();
					
					if(oreStack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
					{
						oItem.getSubItems(CreativeTabs.SEARCH, tmp);
					}
					
					if(tmp.size() <= 0)
					{
						if(!subStacks.contains(oreStack))
						{
							subStacks.add(oreStack.copy());
						}
					} else
					{
						for(ItemStack s : tmp)
						{
							if(!subStacks.contains(s))
							{
								subStacks.add(s.copy());
							}
						}
					}
				}
			} else if(stack.getBaseStack().getItemDamage() == OreDictionary.WILDCARD_VALUE)
			{
				stack.getBaseStack().getItem().getSubItems(CreativeTabs.SEARCH, subStacks);
			}
			
			if(subStacks.size() <= 0)
			{
				subStacks.add(stack.getBaseStack());
			}
		}
		
		public void setDescription(String desc)
		{
			this.desc = desc == null? "" : desc;
		}
		
		@Override
		public void drawBackground(int mx, int my, int px, int py, int width)
		{
			GlStateManager.pushMatrix();
			
			RenderUtils.DrawLine(px, py, px + width, py, 1F, getTextColor());
			
			GlStateManager.color(1F, 1F, 1F, 1F);
			
			GlStateManager.translate(px, py, 0F);
			//GlStateManager.scale(2F, 2F, 2F);
			
			this.mc.renderEngine.bindTexture(currentTheme().getGuiTexture());
			this.drawTexturedModalRect(0, 0, 0, 48, 18, 18);
			
			GlStateManager.enableDepth();
			
			if(stack != null)
			{
				ItemStack tmpStack = subStacks.get((int)(Minecraft.getSystemTime()/1000)%subStacks.size()).copy();
				tmpStack.setTagCompound(stack.GetTagCompound());
				
				try
				{
					RenderUtils.RenderItemStack(mc, tmpStack, 1, 1, (stack.stackSize > 1 ? "" + stack.stackSize : ""));
				} catch(Exception e){}
			}
			
			GlStateManager.disableDepth();
			
			GlStateManager.popMatrix();
			
			RenderUtils.drawSplitString(mc.fontRenderer, desc, px + 20, py + 4, width - 20, getTextColor(), false, 0, 0);
		}
		
		@Override
		public void drawForeground(int mx, int my, int px, int py, int width)
		{
			if(stack != null && isWithin(mx, my, px + 1, py + 1, 16, 16))
			{
				ItemStack tmpStack = subStacks.get((int)(Minecraft.getSystemTime()/1000)%subStacks.size()).copy();
				tmpStack.setTagCompound(stack.GetTagCompound());
				
				try
				{
					this.drawTooltip(tmpStack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL), mx, my, mc.fontRenderer);
				} catch(Exception e){}
			}
		}

        @Override
        public int getHeight()
        {
            return 18;
        }

        @Override
        public boolean canDrawOutsideBox(boolean isForeground)
        {
            return isForeground;
        }

		@Override
		public void onMouseClick(int mx, int my, int px, int py, int click, int index)
		{
			// JEI/NEI support here
			if(stack != null && isWithin(mx, my, px + 1, py + 1, 16, 16))
			{
			    try {
                        codechicken.nei.jei.JEIIntegrationManager.openRecipeGui(stack.getBaseStack());
                }
                catch (Exception e) {
                }
			}
		}
	}
}

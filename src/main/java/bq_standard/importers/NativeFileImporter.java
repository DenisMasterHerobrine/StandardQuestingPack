package bq_standard.importers;

import betterquesting.api.client.importers.IImporter;
import betterquesting.api.enums.EnumSaveType;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api.utils.FileExtensionFilter;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api.utils.NBTConverter;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;
import java.io.FileFilter;

public class NativeFileImporter implements IImporter
{
	public static NativeFileImporter instance = new NativeFileImporter();
	
	@Override
	public String getUnlocalisedName()
	{
		return "bq_standard.importer.nat_file.name";
	}
	
	@Override
	public String getUnlocalisedDescription()
	{
		return "bq_standard.importer.nat_file.desc";
	}
	
	@Override
	public FileFilter getFileFilter()
	{
		return new FileExtensionFilter(".json");
	}

	@Override
	public void loadFiles(IQuestDatabase questDB, IQuestLineDatabase lineDB, File[] files)
	{
		QuestMergeUtility mergeUtil = new QuestMergeUtility(questDB, lineDB);
		
		for(File selected : files)
		{
			if(selected == null || !selected.exists())
			{
				continue;
			}
			
			NBTTagCompound nbt = NBTConverter.JSONtoNBT_Object(JsonHelper.ReadFromFile(selected), new NBTTagCompound(), true);
			
			ImportedQuests impQ = new ImportedQuests(questDB);
			ImportedQuestLines impL = new ImportedQuestLines(lineDB);
			
			impQ.readFromNBT(nbt.getTagList("questDatabase", 10), EnumSaveType.CONFIG);
			impL.readFromNBT(nbt.getTagList("questLines", 10), EnumSaveType.CONFIG);
			
			mergeUtil.merge(impQ, impL);
		}
	}
}

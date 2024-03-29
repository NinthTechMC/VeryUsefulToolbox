package pistonmc.vutoolbox.init;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import pistonmc.vutoolbox.ModNetwork;
import pistonmc.vutoolbox.render.ModelToolbox;
import pistonmc.vutoolbox.render.ToolboxSpecialRenderRegistry;

public class InitClient implements Init {
	InitCommon common = new InitCommon();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		
		common.preInit(event);

		new ModelToolbox().register();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		common.init(event);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		common.postInit(event);
		ModNetwork.registerClientMessages();
		ToolboxSpecialRenderRegistry.load();
	}


}

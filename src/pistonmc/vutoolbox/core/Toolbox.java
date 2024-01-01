package pistonmc.vutoolbox.core;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import pistonmc.vutoolbox.ModUtils;
import pistonmc.vutoolbox.low.NBTToolbox;

/**
 * The core Toolbox logic
 */
public class Toolbox {
	public static final int NUM_TOP_SLOTS = 9;
	private static final int NUM_MIDDLE_LEFT_SLOTS = 15;
	private static final int NUM_MIDDLE_SLOTS = 9;
	private static final int NUM_MIDDLE_RIGHT_SLOTS = 9;
	private static final int NUM_BOTTOM_SLOTS = 9;
	private static final int NUM_UPGRADE_SLOTS = 6;
	
	public static final int NUM_INFINITY_SLOTS = 2;
	// @formatter:off
	public static final int NUM_TOTAL_SLOTS = 
		NUM_TOP_SLOTS +
		NUM_MIDDLE_LEFT_SLOTS +
		NUM_MIDDLE_SLOTS +
		NUM_MIDDLE_RIGHT_SLOTS +
		NUM_BOTTOM_SLOTS +
		NUM_UPGRADE_SLOTS +
		NUM_INFINITY_SLOTS * 2; // times 2 for input and output
	// @formatter:on
	
	// @formatter:off
	private static final int AUTOMATION_SLOT_START = 
		NUM_TOP_SLOTS +
		NUM_MIDDLE_LEFT_SLOTS +
		NUM_MIDDLE_SLOTS +
		NUM_BOTTOM_SLOTS;
	// @formatter:on
	public static final int[] AUTOMATION_SLOTS;
	static {
		AUTOMATION_SLOTS = new int[NUM_BOTTOM_SLOTS];
		for (int i = 0; i < AUTOMATION_SLOTS.length; i++) {
			AUTOMATION_SLOTS[i] = AUTOMATION_SLOT_START + i;
		}
	}
	public static boolean isAutomationSlot(int slot) {
		return slot >= AUTOMATION_SLOT_START && slot < AUTOMATION_SLOT_START + NUM_BOTTOM_SLOTS;
	}
	
	private ItemStack[] slots;
	private BigItemStack[] infinitySlots;
	private ToolboxStatus status;
	
	public Toolbox() {
		slots = new ItemStack[NUM_TOTAL_SLOTS];
		infinitySlots = new BigItemStack[NUM_INFINITY_SLOTS];
		for (int i = 0; i<NUM_INFINITY_SLOTS; i++) {
			infinitySlots[i] = new BigItemStack();
		}
		status = new ToolboxStatus();
	}
	
	public ItemStack getStackInSlot(int slot) {
		return slots[slot];
	}
	
	public ItemStack decrStackSize(int slot, int requestCount) {
		return ModUtils.decrStackSize(slots, slot, requestCount);
	}
	
	/**
	 * Take out the slot, leaving null in its place
	 * @param slot (within bound)
	 * @return
	 */
	public ItemStack takeStack(int slot) {
		ItemStack stack = slots[slot];
		slots[slot] = null;
		return stack;
	}
	
	public void setSlot(int slot, ItemStack stack) {
		slots[slot] = stack;
	}
	
	public BigItemStack getInfinityStack(int slot) {
		return infinitySlots[slot];
	}
	
	/**
	 * Attempt to add to infinity stacks. Will decrease stack size of input stack on success
	 * @param stack
	 */
	public void addToInfinityStack(ItemStack stack) {
		int infLimit = status.getUpgrades().getInfinityStackLimit();
		for (int i = 0; i<infinitySlots.length && stack.stackSize > 0; i++) {
			BigItemStack slot = getInfinityStack(i);
			ItemStack stackInSlot = slot.getItemStack();
			// can only add if slot already has that item
			if (stackInSlot == null) {
				continue;
			}
			if (!stack.isItemEqual(stackInSlot)) {
				continue;
			}
			if (!ItemStack.areItemStackTagsEqual(stack, stackInSlot)) {
				continue;
			}
			int count = slot.getCount();
			if (count >= infLimit) {
				continue;
			}
			int putIn = Math.min(stack.stackSize, infLimit - count);
			slot.addCount(putIn);
			stack.stackSize -= putIn;
			if (stack.stackSize <= 0) {
				break;
			}
		}
	}
	
	public boolean canUse(UUID uuid) {
		return status.canUse(uuid);
	}
	
    /**
     * Set the player as the owner
     *
     * If the entity is not an EntityPlayer, or is null, the owner is removed
     */
    public void setOwner(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            status.setOwner(player.getDisplayName(), player.getUniqueID());
        } else {
            status.setOwner(null, null);
        }
    }
    
    public String getOwner() {
    	return status.getOwner();
    }
    
    public void setCustomName(String name) {
    	status.setCustomName(name);
    }
    
    public String getCustomName() {
    	return status.getCustomName();
    }
	
	/**
	 * Write content to a NBT tag
	 * @param tag (non-null)
	 */
	public void writeToNBT(NBTToolbox tagToolbox) {
		tagToolbox.writeItems(slots);
		tagToolbox.writeInfinityItems(infinitySlots);
		status.writeToNBT(tagToolbox);
	}
	
	public void readFromNBT(NBTToolbox tagToolbox) {
		for (int i = 0; i< NUM_TOTAL_SLOTS;i++) {
			slots[i] = null;
		}
		tagToolbox.readItemsInto(slots);
		tagToolbox.readInfinityItemsInto(infinitySlots);
		status.readFromNBT(tagToolbox);
	}

}
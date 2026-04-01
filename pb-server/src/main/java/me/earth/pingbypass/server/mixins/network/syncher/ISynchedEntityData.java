package me.earth.pingbypass.server.mixins.network.syncher;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SynchedEntityData.class)
public interface ISynchedEntityData {

    @Accessor("itemsById")
    SynchedEntityData.DataItem<?>[] getItemsById();

}

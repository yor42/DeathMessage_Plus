package yor42.deathmessageplus.mixin.util.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatEntry;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(CombatTracker.class)
public abstract class mixincombattracker {

    @Shadow @Final private List<CombatEntry> combatEntries;

    @Shadow @Nullable protected abstract CombatEntry getBestCombatEntry();

    @Shadow @Final private EntityLivingBase fighter;

    @Shadow protected abstract String getFallSuffix(CombatEntry entry);

    @Inject(method="getDeathMessage", at=@At("HEAD"), cancellable = true)
    public void getDeathMessage(CallbackInfoReturnable<ITextComponent> cir)
    {
        if (this.combatEntries.isEmpty())
        {
            cir.setReturnValue(new TextComponentTranslation("death.attack.generic", new Object[] {this.fighter.getDisplayName()}));
        }
        else
        {
            CombatEntry combatentry = this.getBestCombatEntry();
            CombatEntry combatentry1 = this.combatEntries.get(this.combatEntries.size() - 1);
            ITextComponent itextcomponent1 = combatentry1.getDamageSrcDisplayName();
            Entity entity = combatentry1.getDamageSrc().getTrueSource();
            ITextComponent itextcomponent;

            if (combatentry != null && combatentry1.getDamageSrc() == DamageSource.FALL)
            {
                ITextComponent itextcomponent2 = combatentry.getDamageSrcDisplayName();

                if (combatentry.getDamageSrc() != DamageSource.FALL && combatentry.getDamageSrc() != DamageSource.OUT_OF_WORLD)
                {
                    if (itextcomponent2 != null && (itextcomponent1 == null || !itextcomponent2.equals(itextcomponent1)))
                    {
                        Entity entity1 = combatentry.getDamageSrc().getTrueSource();
                        ItemStack itemstack1 = entity1 instanceof EntityLivingBase ? ((EntityLivingBase)entity1).getHeldItemMainhand() : ItemStack.EMPTY;

                        if (!itemstack1.isEmpty() && itemstack1.hasDisplayName())
                        {
                            itextcomponent = new TextComponentTranslation("death.fell.assist.item", new Object[] {this.fighter.getDisplayName(), itextcomponent2, itemstack1.getTextComponent()});
                        }
                        else
                        {
                            itextcomponent = new TextComponentTranslation("death.fell.assist", new Object[] {this.fighter.getDisplayName(), itextcomponent2});
                        }
                    }
                    else if (itextcomponent1 != null)
                    {
                        ItemStack itemstack = entity instanceof EntityLivingBase ? ((EntityLivingBase)entity).getHeldItemMainhand() : ItemStack.EMPTY;

                        if (!itemstack.isEmpty() && itemstack.hasDisplayName())
                        {
                            itextcomponent = new TextComponentTranslation("death.fell.finish.item", new Object[] {this.fighter.getDisplayName(), itextcomponent1, itemstack.getTextComponent()});
                        }
                        else
                        {
                            itextcomponent = new TextComponentTranslation("death.fell.finish", new Object[] {this.fighter.getDisplayName(), itextcomponent1});
                        }
                    }
                    else
                    {
                        itextcomponent = new TextComponentTranslation("death.fell.killer", new Object[] {this.fighter.getDisplayName()});
                    }
                }
                else
                {
                    itextcomponent = new TextComponentTranslation("death.fell.accident." + this.getFallSuffix(combatentry), new Object[] {this.fighter.getDisplayName()});
                }
            }
            else
            {
                itextcomponent = combatentry1.getDamageSrc().getDeathMessage(this.fighter);
            }

            cir.setReturnValue(itextcomponent);
        }
    }
}

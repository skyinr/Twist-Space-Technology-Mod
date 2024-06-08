package com.Nxer.TwistSpaceTechnology.common.machine;

import static com.Nxer.TwistSpaceTechnology.util.TextLocalization.ModName;
import static com.Nxer.TwistSpaceTechnology.util.TextLocalization.textUseBlueprint;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.*;
import static gregtech.api.enums.GT_HatchElement.*;

import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.Nxer.TwistSpaceTechnology.common.machine.multiMachineClasses.GTCM_MultiMachineBase;
import com.Nxer.TwistSpaceTechnology.common.recipeMap.GTCMRecipe;
import com.Nxer.TwistSpaceTechnology.util.TextEnums;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.logic.ProcessingLogic;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GT_HatchElementBuilder;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_StreamUtil;
import gtPlusPlus.xmod.gregtech.loaders.recipe.RecipeLoader_AlgaeFarm;

public class TST_MegaAlgaeFarm extends GTCM_MultiMachineBase<TST_MegaAlgaeFarm> {

    protected static final int MODE_ALGAE_POND = 0;
    protected static final int MODE_SUPER_ALGAE = 1;
    // Max Recipe Tier
    private static final int ALGAE_POND_TIER = 6;
    private static IStructureDefinition<TST_MegaAlgaeFarm> STRUCTURE_DEFINITION = null;

    private short mode = MODE_ALGAE_POND;
    private float speedBonus = 1;

    // TODO 结构
    // spotless: off
    private final String[][] shapeMain = new String[][] { { "CC~CC", } };
    // spotless:on

    public TST_MegaAlgaeFarm(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public TST_MegaAlgaeFarm(String aName) {
        super(aName);
    }

    @Override
    public RecipeMap<?> getRecipeMap() {
        return GTCMRecipe.SuperAlgaeRecipe;
    }

    @Override
    protected boolean isEnablePerfectOverclock() {
        return false;
    }

    @Override
    protected float getSpeedBonus() {
        if (getMode() == MODE_ALGAE_POND) {
            return 1;
        }
        return speedBonus;
    }

    @Override
    protected int getMaxParallelRecipes() {
        return switch (getMode()) {
            case MODE_ALGAE_POND -> 256;
            case MODE_SUPER_ALGAE -> 64;
            default -> 0;
        };
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        repairMachine();
    }

    @Override
    public IStructureDefinition<TST_MegaAlgaeFarm> getStructureDefinition() {
        if (STRUCTURE_DEFINITION == null) {
            STRUCTURE_DEFINITION = StructureDefinition.<TST_MegaAlgaeFarm>builder()
                .addShape(mName, transpose(shapeMain))
                .addElement(
                    'C',
                    GT_HatchElementBuilder.<TST_MegaAlgaeFarm>builder()
                        .atLeast(OutputBus, OutputHatch, InputBus)
                        .build())
                .build();
        }
        return STRUCTURE_DEFINITION;
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType(TextEnums.TooltipMachineType.toString())
            .addOutputBus(textUseBlueprint, 1)
            .addOutputHatch(textUseBlueprint, 1)
            .toolTipFinisher(ModName);
        return tt;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        repairMachine();
        return true;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new TST_MegaAlgaeFarm(this.mName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity baseMetaTileEntity, ForgeDirection side, ForgeDirection facing,
        int colorIndex, boolean active, boolean redstoneLevel) {
        return new ITexture[0];
    }

    @Override
    protected ProcessingLogic createProcessingLogic() {
        if (getMode() == MODE_ALGAE_POND) {
            return new ProcessingLogic() {

                @NotNull
                @Override
                protected Stream<GT_Recipe> findRecipeMatches(@Nullable RecipeMap<?> map) {
                    return GT_StreamUtil
                        .ofNullable(RecipeLoader_AlgaeFarm.getTieredRecipeFromCache(ALGAE_POND_TIER, false));
                }
            }.setEuModifier(0F)
                .setMaxParallelSupplier(this::getMaxParallelRecipes);
        }
        return new ProcessingLogic();
    }

    public short getMode() {
        return mode;
    }

    public void setMode(short mode) {
        this.mode = mode;
    }
}

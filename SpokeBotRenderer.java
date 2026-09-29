package com.typoman.unstablenulls.client;

import com.typoman.unstablenulls.UnstableNulls;
import com.typoman.unstablenulls.entity.SpokeBotEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SpokeBotRenderer extends HumanoidMobRenderer<SpokeBotEntity, HumanoidModel<SpokeBotEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(UnstableNulls.MOD_ID, "textures/entity/spoke_bot.png");

    public SpokeBotRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.6F);
    }

    @Override
    public ResourceLocation getTextureLocation(SpokeBotEntity entity) {
        return TEXTURE;
    }
}

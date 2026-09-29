package com.typoman.unstablenulls.client;

import com.typoman.unstablenulls.UnstableNulls;
import com.typoman.unstablenulls.entity.NullEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class NullRenderer extends HumanoidMobRenderer<NullEntity, HumanoidModel<NullEntity>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(UnstableNulls.MOD_ID, "textures/entity/null.png");

    public NullRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(NullEntity entity) {
        return TEXTURE;
    }
}

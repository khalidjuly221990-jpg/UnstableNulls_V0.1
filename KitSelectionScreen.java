package com.typoman.unstablenulls.client;

import com.typoman.unstablenulls.kit.Kit;
import com.typoman.unstablenulls.network.KitSelectPacket;
import com.typoman.unstablenulls.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class KitSelectionScreen extends Screen {

    public KitSelectionScreen() {
        super(Component.literal("§5§lCHOOSE YOUR KIT"));
    }

    @Override
    protected void init() {
        Kit[] kits = Kit.values();
        int btnW = 120;
        int btnH = 20;
        int gap = 5;
        int cols = 3;
        int rows = (kits.length + cols - 1) / cols;
        int totalW = cols * btnW + (cols - 1) * gap;
        int totalH = rows * btnH + (rows - 1) * gap;
        int startX = (this.width - totalW) / 2;
        int startY = (this.height - totalH) / 2;

        for (int i = 0; i < kits.length; i++) {
            final Kit kit = kits[i];
            int col = i % cols;
            int row = i / cols;
            int x = startX + col * (btnW + gap);
            int y = startY + row * (btnH + gap);
            this.addRenderableWidget(Button.builder(
                    Component.literal(kit.name),
                    b -> select(kit)
            ).bounds(x, y, btnW, btnH).build());
        }
    }

    private void select(Kit kit) {
        ModNetwork.CHANNEL.sendToServer(new KitSelectPacket(kit.ordinal()));
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(g, mx, my, pt);
    }
}

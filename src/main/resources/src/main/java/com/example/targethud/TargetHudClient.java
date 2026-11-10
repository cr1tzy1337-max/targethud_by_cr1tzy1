
package com.example.targethud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.EntityHitResult;

public class TargetHudClient implements ClientModInitializer {

    private static final int HUD_WIDTH = 220;
    private static final int HUD_HEIGHT = 65;

    private static final int BACKGROUND = 0xDD151515;
    private static final int BORDER = 0xFF292929;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int HEALTH_COLOR = 0xFF16B9F2;
    private static final int HEALTH_BACKGROUND = 0xFF333333;

    private static LivingEntity target;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            renderHud(drawContext);
        });
    }

    private static void renderHud(DrawContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.world == null) {
            return;
        }

        if (client.crosshairTarget instanceof EntityHitResult hit) {
            if (hit.getEntity() instanceof LivingEntity living
                    && living.isAlive()) {
                target = living;
            } else {
                target = null;
            }
        } else {
            target = null;
        }

        if (target == null || !target.isAlive()) {
            return;
        }

        int x = (client.getWindow().getScaledWidth() - HUD_WIDTH) / 2;
        int y = client.getWindow().getScaledHeight() / 2 + 35;

        drawTargetHud(context, client, target, x, y);
    }

    private static void drawTargetHud(
            DrawContext context,
            MinecraftClient client,
            LivingEntity entity,
            int x,
            int y
    ) {
        context.fill(
                x, y,
                x + HUD_WIDTH, y + HUD_HEIGHT,
                BACKGROUND
        );

        context.fill(
                x, y,
                x + HUD_WIDTH, y + 2,
                BORDER
        );

        // Голова цели
        if (entity instanceof PlayerEntity player) {
            Identifier skin = player.getSkinTextures().texture();

            context.drawTexture(
                    skin,
                    x + 8, y + 8,
                    8, 8,
                    8, 8,
                    64, 64,
                    64, 64
            );

            // Внешний слой головы
            context.drawTexture(
                    skin,
                    x + 8, y + 8,
                    40, 8,
                    8, 8,
                    64, 64,
                    64, 64
            );
        } else {
            context.fill(
                    x + 8, y + 8,
                    x + 40, y + 40,
                    0xFF555555
            );
        }

        // Имя цели
        String name = entity.getName().getString();

        context.drawText(
                client.textRenderer,
                name,
                x + 48,
                y + 10,
                WHITE,
                true
        );

        // Здоровье
        float health = entity.getHealth();
        float maxHealth = entity.getMaxHealth();

        String hpText = String.format(
                java.util.Locale.US,
                "HP: %.1f",
                health
        );

        context.drawText(
                client.textRenderer,
                hpText,
                x + 48,
                y + 30,
                WHITE,
                true
        );

        // Фон полоски здоровья
        int barX = x + 48;
        int barY = y + 49;
        int barWidth = HUD_WIDTH - 60;

        context.fill(
                barX,
                barY,
                barX + barWidth,
                barY + 6,
                HEALTH_BACKGROUND
        );

        // Заполнение полоски
        float progress = maxHealth > 0
                ? MathHelper.clamp(health / maxHealth, 0.0F, 1.0F)
                : 0.0F;

        int currentWidth = (int) (barWidth * progress);

        context.fill(
                barX,
                barY,
                barX + currentWidth,
                barY + 6,
                HEALTH_COLOR
        );
    }
}

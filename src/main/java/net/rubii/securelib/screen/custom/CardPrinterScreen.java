package net.rubii.securelib.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.network.CardPrinterPayload;

public class CardPrinterScreen extends AbstractContainerScreen<CardPrinterMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/card_printer.png");
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/arrow.png");
    private EditBox nameField;
    private EditBox colorField;

    public CardPrinterScreen(CardPrinterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        colorField = new EditBox(font, leftPos + 24, topPos + 64, 52, 16, Component.literal("6565f5"));
        colorField.setBordered(false);
        colorField.setTextColor(0x404040);
        colorField.setMaxLength(6);
        colorField.setFilter(text -> {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (!((c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f')
                )) {
                    return false;
                }
            }
            return true;
        });
        colorField.setValue(menu.blockEntity.getColor());
        colorField.setResponder(text -> {
            if (!text.equals(menu.blockEntity.getName())) {
                minecraft.getConnection().send(
                        new CardPrinterPayload(menu.blockEntity.getBlockPos(), nameField.getValue(), text)
                );
            }
        });
        colorField.setTextShadow(false);

        addRenderableWidget(colorField);



        nameField = new EditBox(font, leftPos + 18, topPos + 18, 142, 16, Component.literal("Keycard Name"));
        nameField.setBordered(false);
        nameField.setMaxLength(50);
        nameField.setValue(menu.blockEntity.getName());
        nameField.setResponder(text -> {
            if (!text.equals(menu.blockEntity.getName())) {
                minecraft.getConnection().send(
                        new CardPrinterPayload(menu.blockEntity.getBlockPos(), text, colorField.getValue())
                );
            }
        });

        addRenderableWidget(nameField);
    }


    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int i1, int i2) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - 176) / 2;
        int y = (height - 200) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, 176, 200, 256, 256);

        renderProgressArrow(guiGraphics, x, y);
        renderColor(guiGraphics, x, y);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()){
            guiGraphics.blit(ARROW_TEXTURE, x + 118, y + 78, 0, 0, menu.getScaledArrowProgress(), 12, 22, 12);
        }
    }

    private void renderColor(GuiGraphics guiGraphics, int x, int y) {
        if (colorField.getValue().length() != 6) {
            guiGraphics.fill(leftPos + 17, topPos + 77, leftPos + 52 + 17, topPos + 16 + 77, 16777215);
            return; //Render white if we cannot render anything else
        }
        int argb = (int) Long.parseLong("FF" + colorField.getValue(), 16);

        guiGraphics.fill(leftPos + 17, topPos + 59, leftPos + 52 + 17, topPos + 16 + 59, argb);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(this.font, this.title, 11, -12, 0x324058,false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 7, 90, 0x404040,false);
        guiGraphics.drawString(this.font, "#", 18, 64, 0x404040,false);

        guiGraphics.drawString(this.font, Component.translatable("gui.securelib.card_printer.card_name"), 16, 3, 0x324058,false);
        guiGraphics.drawString(this.font, Component.translatable("gui.securelib.card_printer.card_color"), 16, 48, 0x324058,false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int i) {
        if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            if (colorField.isFocused() || nameField.isFocused()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, i);
    }


}

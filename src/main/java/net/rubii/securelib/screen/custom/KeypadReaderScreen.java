package net.rubii.securelib.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.network.KeypadReaderPayloadCode;
import net.rubii.securelib.network.KeypadReaderPayloadInput;

public class KeypadReaderScreen extends AbstractContainerScreen<KeypadReaderMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/keypad.png");
    private static final ResourceLocation KEY_BUTTON = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/key.png");
    private static final ResourceLocation KEY_BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/key_pressed.png");
    private static final ResourceLocation CANCEL_BUTTON = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/cancel.png");
    private static final ResourceLocation CANCEL_BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/cancel_pressed.png");
    private static final ResourceLocation CONFIRM_BUTTON = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/confirm.png");
    private static final ResourceLocation CONFIRM_BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/confirm_pressed.png");

    private String input = "";

    private Player player;

    public KeypadReaderScreen(KeypadReaderMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.player = Minecraft.getInstance().player;
    }

    @Override
    protected void init() {
        super.init();

        addKeyButtons(leftPos + 52, topPos + 48, 27, 27);

        WidgetSprites buttonCancelSprites = new WidgetSprites(CANCEL_BUTTON, CANCEL_BUTTON, CANCEL_BUTTON_PRESSED, CANCEL_BUTTON);
        WidgetSprites buttonConfirmSprites = new WidgetSprites(CONFIRM_BUTTON, CONFIRM_BUTTON, CONFIRM_BUTTON_PRESSED, CONFIRM_BUTTON);

        ImageButton cancelButton = new ImageButton(leftPos + 52, topPos + 129, 18, 18, buttonCancelSprites, button -> {
            this.input = "";
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        ImageButton confirmButton = new ImageButton(leftPos + 106, topPos + 129, 18, 18, buttonConfirmSprites, button -> {
            updateInputInBlockEntity();
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(cancelButton);
        addRenderableWidget(confirmButton);
    }

    private void addKeyButtons(int startPosX, int startPosY, int xOffset,  int yOffset) {
        WidgetSprites buttonKeySprites = new WidgetSprites(KEY_BUTTON, KEY_BUTTON, KEY_BUTTON_PRESSED, KEY_BUTTON);

        ImageButton keyButton1 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("1");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX += xOffset;

        ImageButton keyButton2 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("2");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX += xOffset;

        ImageButton keyButton3 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("3");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX -= xOffset * 2;
        startPosY += yOffset;

        ImageButton keyButton4 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("4");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX += xOffset;

        ImageButton keyButton5 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("5");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX += xOffset;

        ImageButton keyButton6 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("6");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX -= xOffset * 2;
        startPosY += yOffset;

        ImageButton keyButton7 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("7");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX += xOffset;

        ImageButton keyButton8 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("8");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX += xOffset;

        ImageButton keyButton9 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("9");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        startPosX -= xOffset;
        startPosY += yOffset;

        ImageButton keyButton0 = new ImageButton(startPosX, startPosY, 18, 18, buttonKeySprites, button -> {
            updateInput("0");
        }) {
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(keyButton0);
        addRenderableWidget(keyButton1);
        addRenderableWidget(keyButton2);
        addRenderableWidget(keyButton3);
        addRenderableWidget(keyButton4);
        addRenderableWidget(keyButton5);
        addRenderableWidget(keyButton6);
        addRenderableWidget(keyButton7);
        addRenderableWidget(keyButton8);
        addRenderableWidget(keyButton9);
    }

    private void updateInputInBlockEntity(){
        if (input.isEmpty()) return;
        if (input.length() > 16) return;
        if (menu.blockEntity.getCode().isEmpty()){
            minecraft.getConnection().send(
                    new KeypadReaderPayloadCode(menu.blockEntity.getBlockPos(), input)
            );
            player.displayClientMessage(Component.translatable("block.securelib.keypad_reader.code_set"), true);
            input = "";
        }
        minecraft.getConnection().send(
                new KeypadReaderPayloadInput(menu.blockEntity.getBlockPos(), input, player.getStringUUID())
        );
        player.closeContainer();
    }

    private void updateInput(String value){
        if (value.length() > 1) return;
        if (input.length() + 1 > 16) return;
        input+=value;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int i1, int i2) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (width - 132) / 2;
        int y = (height - 154) / 2;

        guiGraphics.blit(GUI_TEXTURE, x, y, 0, 0, 132, 154, 256, 256);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(this.font, this.title, 29, 11, 0x324058,false);

        guiGraphics.drawString(this.font, input, 41, 26, 0x09162b,false);

        addKeyLabels(guiGraphics, true, 0x324058, 58, 53, 27, 27);
    }

    private void addKeyLabels(GuiGraphics guiGraphics, boolean shadows, int color, int startPosX, int startPosY, int xOffset,  int yOffset){
        guiGraphics.drawString(this.font, "1", startPosX, startPosY, color, shadows);

        startPosX += xOffset;

        guiGraphics.drawString(this.font, "2", startPosX, startPosY, color, shadows);

        startPosX += xOffset;

        guiGraphics.drawString(this.font, "3", startPosX, startPosY, color, shadows);

        startPosX -= xOffset * 2;
        startPosY += yOffset;

        guiGraphics.drawString(this.font, "4", startPosX, startPosY, color, shadows);

        startPosX += xOffset;

        guiGraphics.drawString(this.font, "5", startPosX, startPosY, color, shadows);

        startPosX += xOffset;

        guiGraphics.drawString(this.font, "6", startPosX, startPosY, color, shadows);

        startPosX -= xOffset * 2;
        startPosY += yOffset;

        guiGraphics.drawString(this.font, "7", startPosX, startPosY, color, shadows);

        startPosX += xOffset;

        guiGraphics.drawString(this.font, "8", startPosX, startPosY, color, shadows);

        startPosX += xOffset;

        guiGraphics.drawString(this.font, "9", startPosX, startPosY, color, shadows);

        startPosX -= xOffset;
        startPosY += yOffset;

        guiGraphics.drawString(this.font, "0", startPosX, startPosY, color, shadows);
    }
}

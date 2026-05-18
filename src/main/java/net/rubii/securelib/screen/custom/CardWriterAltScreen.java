package net.rubii.securelib.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.rubii.securelib.SecureLib;
import net.rubii.securelib.network.CardWriterAltPayload;
import net.rubii.securelib.network.CardWriterPayload;

import java.util.ArrayList;
import java.util.List;

public class CardWriterAltScreen extends AbstractContainerScreen<CardWriterAltMenu> {
    private static final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/card_writer_alt.png");
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/arrow.png");

    private static final ResourceLocation BUTTON_UP = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/button_up.png");
    private static final ResourceLocation BUTTON_UP_PRESSED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/button_up_pressed.png");
    private static final ResourceLocation BUTTON_UP_DISABLED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/button_up_disabled.png");
    private static final ResourceLocation BUTTON_DOWN = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/button_down.png");
    private static final ResourceLocation BUTTON_DOWN_PRESSED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/button_down_pressed.png");
    private static final ResourceLocation BUTTON_DOWN_DISABLED = ResourceLocation.fromNamespaceAndPath(SecureLib.MODID, "textures/gui/buttons/button_down_disabled.png");

    private EditBox frequencyField;
    private ImageButton increaseClearanceRButton;
    private ImageButton decreaseClearanceRButton;
    private ImageButton increaseClearanceMButton;
    private ImageButton decreaseClearanceMButton;
    private ImageButton increaseClearanceLButton;
    private ImageButton decreaseClearanceLButton;

    public CardWriterAltScreen(CardWriterAltMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Override
    protected void init() {
        super.init();

        //TextFields
        frequencyField = new EditBox(font, leftPos + 81, topPos + 36, 78, 16, Component.literal(""));
        frequencyField.setBordered(false);
        frequencyField.setTextColor(0xffffff);
        frequencyField.setMaxLength(12);
        frequencyField.setValue(menu.blockEntity.getFrequency());
        frequencyField.setResponder(text -> {
            if (!text.equals(menu.blockEntity.getFrequency())) {
                minecraft.getConnection().send(
                        new CardWriterAltPayload(menu.blockEntity.getBlockPos(), text, menu.blockEntity.getLClearance(), menu.blockEntity.getMClearance(), menu.blockEntity.getRClearance())
                );
            }
        });
        frequencyField.setTextShadow(true);

        addRenderableWidget(frequencyField);

        //Buttons

        WidgetSprites buttonUpSprites = new WidgetSprites(BUTTON_UP, BUTTON_UP_DISABLED, BUTTON_UP_PRESSED, BUTTON_UP);
        WidgetSprites buttonDownSprites = new WidgetSprites(BUTTON_DOWN, BUTTON_DOWN_DISABLED, BUTTON_DOWN_PRESSED, BUTTON_DOWN);

        increaseClearanceRButton = new ImageButton(leftPos + 52, topPos + 40, 18, 9, buttonUpSprites, button -> {
            updateClearance(menu.blockEntity.getLClearance(), menu.blockEntity.getMClearance(), menu.blockEntity.getRClearance() + 1);
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(increaseClearanceRButton);

        decreaseClearanceRButton = new ImageButton(leftPos + 52, topPos + 67, 18, 9, buttonDownSprites, button -> {
            updateClearance(menu.blockEntity.getLClearance(), menu.blockEntity.getMClearance(), menu.blockEntity.getRClearance() - 1);
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(decreaseClearanceRButton);

        increaseClearanceMButton = new ImageButton(leftPos + 34, topPos + 40, 18, 9, buttonUpSprites, button -> {
            updateClearance(menu.blockEntity.getLClearance(), menu.blockEntity.getMClearance() + 1, menu.blockEntity.getRClearance());
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(increaseClearanceMButton);

        decreaseClearanceMButton = new ImageButton(leftPos + 34, topPos + 67, 18, 9, buttonDownSprites, button -> {
            updateClearance(menu.blockEntity.getLClearance(), menu.blockEntity.getMClearance() - 1, menu.blockEntity.getRClearance());
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(decreaseClearanceMButton);

        increaseClearanceLButton = new ImageButton(leftPos + 16, topPos + 40, 18, 9, buttonUpSprites, button -> {
            updateClearance(menu.blockEntity.getLClearance() + 1, menu.blockEntity.getMClearance(), menu.blockEntity.getRClearance());
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(increaseClearanceLButton);

        decreaseClearanceLButton = new ImageButton(leftPos + 16, topPos + 67, 18, 9, buttonDownSprites, button -> {
            updateClearance(menu.blockEntity.getLClearance() - 1, menu.blockEntity.getMClearance(), menu.blockEntity.getRClearance());
        }){
            @Override
            public void renderWidget(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
                guiGraphics.blit(sprites.get(isActive(), isHoveredOrFocused()), getX(), getY(), 0, 0, width, height, width, height);
            }
        };

        addRenderableWidget(decreaseClearanceLButton);
    }

    private void updateClearance(Integer lValue, Integer mValue, Integer rValue){
        if (lValue > 3 || lValue < 0 ) return;
        if (mValue > 3 || mValue < 0 ) return;
        if (rValue > 3 || rValue < 0 ) return;
        minecraft.getConnection().send(
                new CardWriterAltPayload(menu.blockEntity.getBlockPos(), menu.blockEntity.getFrequency(), lValue, mValue, rValue)
        );
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
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        List<Component> debugComponents = new ArrayList<>();
        debugComponents.add(Component.literal(mouseX + " " + mouseY));

        if (hoveredSlot != null){
            if (hoveredSlot.index == 36 && mouseY <= 118 && menu.blockEntity.inventory.getStackInSlot(0).isEmpty()) {
                List<Component> components = new ArrayList<>();
                components.add(Component.translatable("block.securelib.card_writer.input_slot_limitation_alt"));
                guiGraphics.renderComponentTooltip(font, components, mouseX, mouseY);

                debugComponents.add(Component.literal("Slot: " + hoveredSlot.index));
            }
        }

        //DEBUG ONLY guiGraphics.renderComponentTooltip(font, debugComponents, mouseX, mouseY);

        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()){
            guiGraphics.blit(ARROW_TEXTURE, x + 109, y + 78, 0, 0, menu.getScaledArrowProgress(), 12, 22, 12);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.drawString(this.font, Component.translatable("block.securelib.tricard_writer"), 11, -12, 0x324058,false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 7, 90, 0x404040,false);
        guiGraphics.drawString(this.font, menu.blockEntity.getLClearance().toString(), 22, 54, 0x324058,true);
        guiGraphics.drawString(this.font, menu.blockEntity.getMClearance().toString(), 40, 54, 0x324058,true);
        guiGraphics.drawString(this.font, menu.blockEntity.getRClearance().toString(), 58, 54, 0x324058,true);

        guiGraphics.drawString(this.font, Component.translatable("block.securelib.card_writer.frequency"), 79, 20, 0x324058,false);
        guiGraphics.drawString(this.font, Component.translatable("block.securelib.card_writer.clearances"), 15, 30, 0x324058,false);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int i) {
        if (minecraft.options.keyInventory.matches(keyCode, scanCode)) {
            if (frequencyField.isFocused()) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, i);
    }
}
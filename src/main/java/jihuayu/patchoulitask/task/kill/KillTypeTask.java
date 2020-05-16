package jihuayu.patchoulitask.task.kill;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import jihuayu.patchoulitask.ModMain;
import jihuayu.patchoulitask.task.BaseTaskPage;
import jihuayu.patchoulitask.util.JEIUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.util.EntityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class KillTypeTask extends BaseTaskPage {
    public transient Entity entity;
    public transient Function<World, Entity> creator;
    public transient float renderScale, offset;
    public transient boolean errored;
    float scale = 0.9F;
    @SerializedName("offset")
    float extraOffset = -0F;
    @SerializedName("default_rotation")
    float defaultRotation = -45f;
    int num;
    transient int now_num;
    boolean rotate = true;

    String name;

    public void build(BookEntry entry, int pageNum) {
        super.build(entry, pageNum);
        creator = EntityUtil.loadEntity(name);
    }

    public boolean render1(int mouseX, int mouseY, float pticks) {
        if (!super.render1(mouseX, mouseY, pticks)) return false;
        int x = GuiBook.PAGE_WIDTH / 2 - 53;
        int y = 7;
        RenderSystem.enableBlend();
        RenderSystem.color3f(1F, 1F, 1F);
        RenderSystem.pushMatrix();
        RenderSystem.scaled(0.6,0.6,0.6);
        RenderSystem.translated(40,25,0);
        GuiBook.drawFromTexture(book, x, y, 405, 149, 106, 106);
        if (errored) {
            fontRenderer.drawStringWithShadow(I18n.format("patchouli.gui.lexicon.loading_error"), 58, 60, 0xFF0000);
        }

        if (entity != null) {
            renderEntity(parent.getMinecraft().world, rotate ? ClientTicker.total : defaultRotation);
            RenderSystem.pushMatrix();
            RenderSystem.scaled(1.5,1.5,1.5);
            if (parent.isMouseInRelativeRange(mouseX, mouseY, x+23, y+13, 60, 60)) {
                List<ITextComponent> list = new ArrayList<>();
                list.add(new TranslationTextComponent("patchouliquests.task.kill.text").appendSibling(entity.getDisplayName()));
                list.add(new TranslationTextComponent("patchouliquests.task.kill.count",now_num,num));
                parent.setTooltip(list);
            }
            parent.drawCenteredStringNoShadow(I18n.format("patchouliquests.task.kill.now",now_num,num),
                    fontRenderer.getStringWidth(I18n.format("patchouliquests.task.kill.now",now_num,num))/2+45, y+56, book.textColor);
            RenderSystem.popMatrix();
        }
        RenderSystem.popMatrix();

        return true;
    }
    private void renderEntity(World world, float rotation) {
        renderEntity(entity, world, 58, 60, rotation, renderScale, offset);
    }

    public static void renderEntity(Entity entity, World world, float x, float y, float rotation, float renderScale, float offset) {
        entity.world = world;
        RenderSystem.pushMatrix();
        RenderSystem.color3f(1F, 1F, 1F);
        MatrixStack matrix = new MatrixStack();
        matrix.translate(x, y+30, 50);
        matrix.scale(renderScale, renderScale, renderScale);
        matrix.translate(0, offset, 0);
        matrix.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180));
        matrix.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(rotation));
        EntityRendererManager erd = Minecraft.getInstance().getRenderManager();
        IRenderTypeBuffer.Impl immediate = Minecraft.getInstance().getBufferBuilders().getEntityVertexConsumers();
        erd.setRenderShadow(false);
        erd.render(entity, 0, 0, 0, 0, 1, matrix, immediate, 0xF000F0);
        erd.setRenderShadow(true);
        immediate.draw();
        RenderSystem.popMatrix();
    }
    @Override
    public void onDisplayed(GuiBookEntry parent, int left, int top) {
        super.onDisplayed(parent, left, top);

        loadEntity(parent.getMinecraft().world);
    }

    private void loadEntity(World world) {
        if (!errored && (entity == null || !entity.isAlive())) {
            try {
                entity = creator.apply(world);

                float width = entity.getWidth();
                float height = entity.getHeight();

                float entitySize = width;
                if (width < height) {
                    entitySize = height;
                }
                entitySize = Math.max(1F, entitySize);

                renderScale = 100F / entitySize * 0.8F * scale;
                offset = Math.max(height, entitySize) * 0.5F + extraOffset;

                if (name == null || name.isEmpty()) {
                    name = entity.getName().getFormattedText();
                }
            } catch (Exception e) {
                errored = true;
                ModMain.LOGGER.error("Failed to load entity", e);
            }
        }
    }
    public int mouseClicked1(double mouseX, double mouseY, int mouseButton) {
        if (super.mouseClicked1(mouseX,mouseY,mouseButton)<0)return -1;
        return 0;
    }
}

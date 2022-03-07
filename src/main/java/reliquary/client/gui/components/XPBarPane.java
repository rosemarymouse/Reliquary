package reliquary.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import reliquary.reference.Reference;

public class XPBarPane extends Component {
	private static final ResourceLocation XP_BAR = new ResourceLocation(Reference.MOD_ID, "textures/gui/xp_bar.png");
	private float xpRatio;

	public void setXpRatio(float xpRatio) {
		this.xpRatio = xpRatio;
	}

	@Override
	public int getHeightInternal() {
		return 74;
	}

	@Override
	public int getWidthInternal() {
		return 11;
	}

	@Override
	public int getPadding() {
		return 2;
	}

	@Override
	public void renderInternal(PoseStack matrixStack, int x, int y) {
		RenderSystem.setShaderTexture(0, XP_BAR);
		blit(x, y, 0, 0, 11, 74, 22, 74);

		if (xpRatio > 0) {
			int filledHeight = (int) (xpRatio * 74);
			blit(x, y + (74 - filledHeight), 11, 74 - filledHeight, 11, filledHeight, 22, 74);
		}
	}
}
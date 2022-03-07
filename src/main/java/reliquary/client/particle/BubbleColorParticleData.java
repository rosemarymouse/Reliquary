package reliquary.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import reliquary.client.init.ModParticles;

public class BubbleColorParticleData extends ColorParticleData {
	public static final Codec<BubbleColorParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("r").forGetter(ColorParticleData::getRed),
			Codec.FLOAT.fieldOf("g").forGetter(ColorParticleData::getGreen),
			Codec.FLOAT.fieldOf("b").forGetter(ColorParticleData::getBlue)
	).apply(instance, BubbleColorParticleData::new));

	public BubbleColorParticleData(float red, float green, float blue) {
		super(red, green, blue);
	}

	@Override
	public ParticleType<?> getType() {
		return ModParticles.CAULDRON_BUBBLE;
	}

	public static final Deserializer<BubbleColorParticleData> DESERIALIZER = new Deserializer<BubbleColorParticleData>() {
		@Override
		public BubbleColorParticleData fromCommand(ParticleType<BubbleColorParticleData> particleTypeIn, StringReader reader) throws CommandSyntaxException {
			return DeserializationHelper.deserialize(BubbleColorParticleData::new, reader);
		}

		@Override
		public BubbleColorParticleData fromNetwork(ParticleType<BubbleColorParticleData> particleTypeIn, FriendlyByteBuf buffer) {
			return DeserializationHelper.read(BubbleColorParticleData::new, buffer);
		}
	};
}
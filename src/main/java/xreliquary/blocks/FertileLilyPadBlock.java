package xreliquary.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BushBlock;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import xreliquary.reference.Settings;

import java.util.Random;

public class FertileLilyPadBlock extends BushBlock {
	private static final VoxelShape AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.WATER;
	}

	public FertileLilyPadBlock() {
		super(Properties.create(Material.PLANTS).tickRandomly());
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		growCropsNearby(world, pos, state);
	}

	@Override
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		world.addParticle(ParticleTypes.ENTITY_EFFECT, pos.getX() + 0.5D + rand.nextGaussian() / 8, pos.getY(), pos.getZ() + 0.5D + rand.nextGaussian() / 8, 0.0D, 0.9D, 0.5D);
	}

	private int secondsBetweenGrowthTicks() {
		return Settings.COMMON.blocks.fertileLilypad.secondsBetweenGrowthTicks.get();
	}

	private int tileRange() {
		return Settings.COMMON.blocks.fertileLilypad.tileRange.get();
	}

	private int fullPotencyRange() {
		return Settings.COMMON.blocks.fertileLilypad.fullPotencyRange.get();
	}

	private void growCropsNearby(ServerWorld world, BlockPos pos, BlockState state) {
		BlockPos.getAllInBoxMutable(pos.add(-tileRange(), -1, -tileRange()), pos.add(tileRange(), tileRange(), tileRange())).forEach(cropPos -> {
			if (!world.isBlockLoaded(cropPos)) {
				return;
			}
			BlockState cropState = world.getBlockState(cropPos);
			Block cropBlock = cropState.getBlock();

			if ((cropBlock instanceof IPlantable || cropBlock instanceof IGrowable) && !(cropBlock instanceof FertileLilyPadBlock)) {
				double distance = Math.sqrt(cropPos.distanceSq(pos));
				tickCropBlock(world, cropPos, cropState, cropBlock, distance);
			}
		});
		world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), secondsBetweenGrowthTicks() * 20);
	}

	private void tickCropBlock(ServerWorld world, BlockPos cropPos, BlockState cropState, Block cropBlock, double distance) {
		distance -= fullPotencyRange();
		distance = Math.max(1D, distance);
		double distanceCoefficient = 1D - (distance / tileRange());

		//it schedules the next tick.
		world.getPendingBlockTicks().scheduleTick(cropPos, cropBlock, (int) (distanceCoefficient * (float) secondsBetweenGrowthTicks() * 20F));
		cropBlock.randomTick(cropState, world, cropPos, world.rand);
		world.playEvent(2005, cropPos, Math.max((int) (tileRange() - distance), 1));
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		super.onEntityCollision(state, worldIn, pos, entityIn);
		if (entityIn instanceof BoatEntity) {
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
		FluidState ifluidstate = worldIn.getFluidState(pos);
		return ifluidstate.getFluid() == Fluids.WATER || state.getMaterial() == Material.ICE;
	}
}

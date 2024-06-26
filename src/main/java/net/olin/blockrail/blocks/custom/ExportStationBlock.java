package net.olin.blockrail.blocks.custom;

import net.minecraft.util.*;
import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.olin.blockrail.blocks.entity.ExportStationBlockEntity;
import net.olin.blockrail.blocks.entity.ModBlockEntities;

public class ExportStationBlock extends BlockWithEntity implements BlockEntityProvider, IWrenchable {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public ExportStationBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}
	@Override
	@SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}
	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return super.mirror(state, mirror);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (state.getBlock() != newState.getBlock()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof ExportStationBlockEntity) {
				ItemScatterer.spawn(world, pos, (ExportStationBlockEntity)blockEntity);
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	public Class<ExportStationBlockEntity> getBlockEntityClass() {
		return ExportStationBlockEntity.class;
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			NamedScreenHandlerFactory screenHandlerFactory = ((ExportStationBlockEntity) world.getBlockEntity(pos));

			if (screenHandlerFactory != null) {
				player.openHandledScreen(screenHandlerFactory);
			}
		}

		return ActionResult.SUCCESS;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.EXPORT_STATION_BLOCK_ENTITY,
				(world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new ExportStationBlockEntity(pos, state);
	}


}

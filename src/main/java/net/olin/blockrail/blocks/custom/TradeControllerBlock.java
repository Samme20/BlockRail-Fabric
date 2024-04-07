package net.olin.blockrail.blocks.custom;

import com.simibubi.create.content.equipment.wrench.IWrenchable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.olin.blockrail.blocks.entity.ExportStationBlockEntity;

import net.olin.blockrail.blocks.entity.ModBlockEntities;

import net.olin.blockrail.blocks.entity.TradeControllerBlockEntity;

import org.jetbrains.annotations.Nullable;

public class TradeControllerBlock extends BlockWithEntity implements BlockEntityProvider, IWrenchable {
    private static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public TradeControllerBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context){

		float minx = 0;float miny = 0;float minz = 0;float maxx = 0;float maxy = 0;float maxz = 0;

		if (state.get(FACING) == Direction.NORTH) { minx = 0;  miny = 0;  minz = 13;  maxx = 16;  maxy = 16;  maxz = 16; }
		else if (state.get(FACING) == Direction.EAST) { minx = 0;  miny = 0;  minz = 0;  maxx = 3;  maxy = 16;  maxz = 16; }
		else if (state.get(FACING) == Direction.SOUTH) {  minx = 0;  miny = 0;  minz = 0;  maxx = 16;  maxy = 16;  maxz = 3; }
		else if (state.get(FACING) == Direction.WEST) {  minx = 13;  miny = 0;  minz = 0;  maxx = 16;  maxy = 16;  maxz = 16; }

		return VoxelShapes.union(
				createCuboidShape(minx, miny, minz, maxx, maxy, maxz),
				createCuboidShape(0, 0, 0, 16, 4, 16)
		);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
	}
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
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

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!world.isClient) {
			NamedScreenHandlerFactory screenHandlerFactory = ((TradeControllerBlockEntity) world.getBlockEntity(pos));

			if (screenHandlerFactory != null) {
				player.openHandledScreen(screenHandlerFactory);
			}
		}

		return ActionResult.SUCCESS;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return checkType(type, ModBlockEntities.TRADE_CONTROLLER_BLOCK_ENTITY,
				(world1, pos, state1, blockEntity) -> blockEntity.tick(world1, pos, state1));
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TradeControllerBlockEntity(pos, state);
	}
}

package mod.traister101.datagenutils.data.util.tfc;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.FluidHeat;

public final class TFCFluidHeat {

	public static final float HEAT_CAPACITY = 0.003F;
	public static final FluidHeat BISMUTH = of(Metal.BISMUTH, 0.14F, 270);
	public static final FluidHeat BISMUTH_BRONZE = of(Metal.BISMUTH_BRONZE, 0.35F, 985);
	public static final FluidHeat BLACK_BRONZE = of(Metal.BLACK_BRONZE, 0.35F, 1070);
	public static final FluidHeat BRONZE = of(Metal.BRONZE, 0.35F, 950);
	public static final FluidHeat BRASS = of(Metal.BRASS, 0.35F, 930);
	public static final FluidHeat COPPER = of(Metal.COPPER, 0.35F, 1080);
	public static final FluidHeat GOLD = of(Metal.GOLD, 0.6F, 1060);
	public static final FluidHeat NICKEL = of(Metal.NICKEL, 0.48F, 1453);
	public static final FluidHeat ROSE_GOLD = of(Metal.ROSE_GOLD, 0.35F, 960);
	public static final FluidHeat SILVER = of(Metal.SILVER, 0.48F, 961);
	public static final FluidHeat TIN = of(Metal.TIN, 0.14F, 230);
	public static final FluidHeat ZINC = of(Metal.ZINC, 0.21F, 420);
	public static final FluidHeat STERLING_SILVER = of(Metal.STERLING_SILVER, 0.35F, 950);
	public static final FluidHeat WROUGHT_IRON = of(Metal.WROUGHT_IRON, 0.35F, 1535);
	public static final FluidHeat CAST_IRON = of(Metal.CAST_IRON, 0.35F, 1535);
	public static final FluidHeat PIG_IRON = of(Metal.PIG_IRON, 0.35F, 1535);
	public static final FluidHeat STEEL = of(Metal.STEEL, 0.35F, 1540);
	public static final FluidHeat BLACK_STEEL = of(Metal.BLACK_STEEL, 0.35F, 1485);
	public static final FluidHeat BLUE_STEEL = of(Metal.BLUE_STEEL, 0.35F, 1540);
	public static final FluidHeat RED_STEEL = of(Metal.RED_STEEL, 0.35F, 1540);
	public static final FluidHeat WEAK_STEEL = of(Metal.WEAK_STEEL, 0.35F, 1540);
	public static final FluidHeat WEAK_BLUE_STEEL = of(Metal.WEAK_BLUE_STEEL, 0.35F, 1540);
	public static final FluidHeat WEAK_RED_STEEL = of(Metal.WEAK_RED_STEEL, 0.35F, 1540);
	public static final FluidHeat HIGH_CARBON_STEEL = of(Metal.HIGH_CARBON_STEEL, 0.35F, 1540);
	public static final FluidHeat HIGH_CARBON_BLACK_STEEL = of(Metal.HIGH_CARBON_BLACK_STEEL, 0.35F, 1540);
	public static final FluidHeat HIGH_CARBON_BLUE_STEEL = of(Metal.HIGH_CARBON_BLUE_STEEL, 0.35F, 1540);
	public static final FluidHeat HIGH_CARBON_RED_STEEL = of(Metal.HIGH_CARBON_RED_STEEL, 0.35F, 1540);
	public static final FluidHeat UNKNOWN = of(Metal.UNKNOWN, 0.5F, 400);

	private static FluidHeat of(final Metal metal, final float baseHeatCapacity, final float meltTemperature) {
		return new FluidHeat(TFCFluids.METALS.get(metal).source().get(), meltTemperature, HEAT_CAPACITY / baseHeatCapacity);
	}
}
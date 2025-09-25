/**
 * Data Providers for TFC types. These are primarily for custom data objects TFC registers via a {@link net.dries007.tfc.util.data.DataManager}.
 * <strong>VERY IMPORTANT</strong> {@link mod.traister101.datagenutils.data.tfc.DataManagerProvider}s will inject their contents into the
 * {@link net.dries007.tfc.util.data.DataManager} they are for, however other addons and even TFC itself will not have any values present.
 * Some values are provided but this is a very painful area for datagen there's little that can be done about it as a third party.
 * {@link mod.traister101.datagenutils.data.util.tfc.TFCFluidHeat}
 */
@FieldsAreNonnullByDefault @MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
package mod.traister101.datagenutils.data.tfc;

import net.minecraft.*;

import javax.annotation.ParametersAreNonnullByDefault;
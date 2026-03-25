package com.example.bankapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.example.bankapp.R

sealed class DeviceSpec {


    abstract val profileScreenWidthFaction: Float
    abstract val textFieldWidth: Float

    abstract val qabButtonSize: Int
    abstract val qabButtonIconSize: Int
    abstract val qabButtonSpacing: Dp
    abstract val cardWidth: Float
    abstract val topPadding: Dp

    abstract val navBarItemWidth: Int

    abstract val navBarItemheight: Int

    abstract val homeAppBarHorizontalPadding: Dp

    abstract val homeAppBarTopPadding: Dp

    abstract val homeAppBarBottomPadding: Dp

    abstract val homeAppBarSpacingVertical: Dp

    abstract val homeAppBarMenuButtonSize: Int

    @Composable
    abstract fun homeUserGreetingStyle(): TextStyle

    @Composable
    abstract fun homeUserNameStyle(): TextStyle

    @Composable
    abstract fun quickActionsLabelStyle(): TextStyle
    abstract val quickActionsSpacing: Dp
    abstract val HomeCardHorizontalPadding: Dp
    abstract val HomeCardVerticalPadding: Dp

    @Composable
    abstract fun qabButtonLabelSize(): TextStyle

    abstract val homeScreenCardMinHeight: Int
    abstract val homeScreenCardRoundedCorner: Int
    abstract val homeScreenCardElevation: Int
    abstract val homeScreenCardInnerBoxRoundedCorner: Int
    abstract val homeScreenCardInnerBoxPadding: Dp
    abstract val homeScreenCardLogoSize: Int
    abstract val homeScreenCardSpacingVertical: Dp
    abstract val homeScreenCardWidth: Float
    abstract val homeScreenCardAccountInfoPadding: Dp
    abstract val homeScreenCardAccountInfoRoundedCorner: Int
    abstract val homeScreenCardAccountSectionSpacing: Dp

    abstract val transactionListItemAvatarSize: Int
    abstract val transactionListItemHorizontalPadding: Dp
    abstract val transactionListItemVerticalPadding: Dp
    abstract val transactionListItemSpacing: Dp

    abstract val transactionDateHeaderPadding: Dp

    abstract val transactionDetailAvatarSize: Int

    abstract val transactionDetailStatusItemWidth: Float
    abstract val transactionDetailCardWidth: Float
    abstract val transactionDetailCardPadding: Dp
    abstract val transactionDetailDividerPaddingVertical: Dp

    abstract val payScreenButtonSize: Int
    abstract val payScreenButtonIconSize: Int
    abstract val payScreenCardPadding: Dp
    abstract val payScreenSectionSpacing: Dp
    abstract val profileAvatarSize: Int
    abstract val profileCardPadding: Dp
    abstract val profileSectionSpacing: Dp

    @Composable
    abstract fun profileSectionTitleStyle(): TextStyle

    @Composable
    abstract fun homeTransactionHistoryLabel(): TextStyle
    @Composable
    abstract fun homeSeeAllLabel(): TextStyle

    @Composable
    abstract fun transactionDateHeaderStyle(): TextStyle

    @Composable
    abstract fun transactionListItemCounterPartyNameStyle(): TextStyle

    @Composable
    abstract fun transactionListItemDateStyle(): TextStyle

    @Composable
    abstract fun transactionListItemMoneyStyle(): TextStyle

    @Composable
    abstract fun payScreenSectionTitleStyle(): TextStyle

    abstract val beneficiaryGridColumnsSize: Int
    abstract val beneficiaryAvatarSize: Int
    abstract val beneficiaryItemSpacing: Dp



    data class MobilePortrait(

        override val textFieldWidth: Float = 0.9f,

        override val qabButtonSize: Int = R.dimen.qab_button_size,
        override val qabButtonIconSize: Int = R.dimen.qab_icon_size,
        override val qabButtonSpacing: Dp = AppSpacing.sm,
        override val cardWidth: Float = 1f,
        override val topPadding: Dp = AppSpacing.md,
        override val quickActionsSpacing: Dp = AppSpacing.sm,

        override val HomeCardHorizontalPadding: Dp = AppSpacing.sm,
        override val HomeCardVerticalPadding: Dp = AppSpacing.sm,
        override val navBarItemWidth: Int = R.dimen.bottom_nav_item_width_mobile_portrait,
        override val navBarItemheight: Int = R.dimen.bottom_nav_item_height_mobile_portrait,

        override val homeAppBarHorizontalPadding: Dp = AppSpacing.md,
        override val homeAppBarTopPadding: Dp = AppSpacing.md,
        override val homeAppBarBottomPadding: Dp = AppSpacing.sm,
        override val homeAppBarSpacingVertical: Dp = AppSpacing.xs,
        override val homeAppBarMenuButtonSize: Int = R.dimen.home_appbar_menu_button_size_mobile,

        override val homeScreenCardMinHeight: Int = R.dimen.home_screen_card_min_height,
        override val homeScreenCardRoundedCorner: Int = R.dimen.home_screen_card_rounded_corner,
        override val homeScreenCardElevation: Int = R.dimen.home_screen_card_elevation,
        override val homeScreenCardInnerBoxRoundedCorner: Int = R.dimen.home_screen_card_inner_box_rounded_corner,
        override val homeScreenCardInnerBoxPadding: Dp = AppSpacing.md,
        override val homeScreenCardLogoSize: Int = R.dimen.home_screen_card_logo_size_mobile,
        override val homeScreenCardSpacingVertical: Dp = AppSpacing.xs,
        override val homeScreenCardWidth: Float = 1f,
        override val homeScreenCardAccountInfoPadding: Dp = AppSpacing.md,
        override val homeScreenCardAccountInfoRoundedCorner: Int = R.dimen.home_screen_card_account_info_rounded_corner,
        override val homeScreenCardAccountSectionSpacing: Dp = AppSpacing.xl,

        override val transactionListItemAvatarSize: Int = R.dimen.transaction_list_item_avatar_size,
        override val transactionListItemHorizontalPadding: Dp = AppSpacing.md,
        override val transactionListItemVerticalPadding: Dp = AppSpacing.sm,
        override val transactionListItemSpacing: Dp = AppSpacing.md,

        override val transactionDateHeaderPadding: Dp = AppSpacing.md,
        override val transactionDetailAvatarSize: Int = R.dimen.transaction_detail_avatar_size_mobile,

        override val transactionDetailStatusItemWidth: Float = 0.8f,
        override val transactionDetailCardWidth: Float = 0.9f,
        override val transactionDetailCardPadding: Dp = AppSpacing.md,
        override val transactionDetailDividerPaddingVertical: Dp = AppSpacing.md,

        override val payScreenButtonSize: Int = R.dimen.pay_screen_button_size_mobile,
        override val payScreenButtonIconSize: Int = R.dimen.pay_screen_button_icon_size_mobile,
        override val payScreenCardPadding: Dp = AppSpacing.md,
        override val payScreenSectionSpacing: Dp = AppSpacing.xl,

        override val profileAvatarSize: Int = R.dimen.profile_avatar_size_mobile,
        override val profileCardPadding: Dp = AppSpacing.md,
        override val profileSectionSpacing: Dp = AppSpacing.xl,

        override val profileScreenWidthFaction: Float = 0.9f,

        override val beneficiaryGridColumnsSize: Int = 3,
        override val beneficiaryAvatarSize: Int = R.dimen.beneficiary_avatar_size_mobile,
        override val beneficiaryItemSpacing: Dp = AppSpacing.lg
        ) : DeviceSpec() {

        @Composable
        override fun qabButtonLabelSize(): TextStyle = MaterialTheme.typography.labelMedium

        @Composable
        override fun homeTransactionHistoryLabel(): TextStyle = MaterialTheme.typography.titleMedium

        @Composable
        override fun homeSeeAllLabel(): TextStyle = MaterialTheme.typography.labelLarge

        @Composable
        override fun quickActionsLabelStyle(): TextStyle = MaterialTheme.typography.titleMedium

        @Composable
        override fun homeUserGreetingStyle(): TextStyle = MaterialTheme.typography.labelMedium

        @Composable
        override fun homeUserNameStyle(): TextStyle = MaterialTheme.typography.homeUserName

        @Composable
        override fun transactionDateHeaderStyle(): TextStyle = MaterialTheme.typography.labelLarge

        @Composable
        override fun transactionListItemCounterPartyNameStyle(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun transactionListItemDateStyle(): TextStyle = MaterialTheme.typography.bodySmall

        @Composable
        override fun transactionListItemMoneyStyle(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun payScreenSectionTitleStyle(): TextStyle = MaterialTheme.typography.titleMedium

        @Composable
        override fun profileSectionTitleStyle(): TextStyle = MaterialTheme.typography.labelMedium
    }

    data class MobileLandscape(

        override val textFieldWidth: Float = 0.6f,

        override val qabButtonSize: Int = R.dimen.qab_button_size_landscape,
        override val qabButtonIconSize: Int = R.dimen.qab_icon_size_landscape,
        override val qabButtonSpacing: Dp = AppSpacing.md,
        override val cardWidth: Float = 0.85f,
        override val topPadding: Dp = AppSpacing.md,
        override val quickActionsSpacing: Dp = AppSpacing.sm,

        override val HomeCardHorizontalPadding: Dp = AppSpacing.sm,
        override val HomeCardVerticalPadding: Dp = AppSpacing.sm,
        override val navBarItemWidth: Int = R.dimen.bottom_nav_item_width_mobile_landscape,
        override val navBarItemheight: Int = R.dimen.bottom_nav_item_height_mobile_landscape,

        override val homeAppBarHorizontalPadding: Dp = AppSpacing.md,
        override val homeAppBarTopPadding: Dp = AppSpacing.md,
        override val homeAppBarBottomPadding: Dp = AppSpacing.sm,
        override val homeAppBarSpacingVertical: Dp = AppSpacing.xs,
        override val homeAppBarMenuButtonSize: Int = R.dimen.home_appbar_menu_button_size_mobile,

        override val homeScreenCardMinHeight: Int = R.dimen.home_screen_card_min_height,
        override val homeScreenCardRoundedCorner: Int = R.dimen.home_screen_card_rounded_corner,
        override val homeScreenCardElevation: Int = R.dimen.home_screen_card_elevation,
        override val homeScreenCardInnerBoxRoundedCorner: Int = R.dimen.home_screen_card_inner_box_rounded_corner,
        override val homeScreenCardInnerBoxPadding: Dp = AppSpacing.md,
        override val homeScreenCardLogoSize: Int = R.dimen.home_screen_card_logo_size_mobile,
        override val homeScreenCardSpacingVertical: Dp = AppSpacing.xs,
        override val homeScreenCardWidth: Float = 0.80f,
        override val homeScreenCardAccountInfoPadding: Dp = AppSpacing.md,
        override val homeScreenCardAccountInfoRoundedCorner: Int = R.dimen.home_screen_card_account_info_rounded_corner,
        override val homeScreenCardAccountSectionSpacing: Dp = AppSpacing.lg,

        override val transactionListItemAvatarSize: Int = R.dimen.transaction_list_item_avatar_size,
        override val transactionListItemHorizontalPadding: Dp = AppSpacing.md,
        override val transactionListItemVerticalPadding: Dp = AppSpacing.sm,
        override val transactionListItemSpacing: Dp = AppSpacing.md,

        override val transactionDateHeaderPadding: Dp = AppSpacing.md,
        override val transactionDetailAvatarSize: Int = R.dimen.transaction_detail_avatar_size_mobile,

        override val transactionDetailStatusItemWidth: Float = 0.6f,
        override val transactionDetailCardWidth: Float = 0.6f,
        override val transactionDetailCardPadding: Dp = AppSpacing.md,
        override val transactionDetailDividerPaddingVertical: Dp = AppSpacing.md,

        override val payScreenButtonSize: Int = R.dimen.pay_screen_button_size_mobile,
        override val payScreenButtonIconSize: Int = R.dimen.pay_screen_button_icon_size_mobile,
        override val payScreenCardPadding: Dp = AppSpacing.md,
        override val payScreenSectionSpacing: Dp = AppSpacing.lg,

        override val profileAvatarSize: Int = R.dimen.profile_avatar_size_mobile,
        override val profileCardPadding: Dp = AppSpacing.md,
        override val profileSectionSpacing: Dp = AppSpacing.lg,

        override val profileScreenWidthFaction: Float = 0.6f,

        override val beneficiaryGridColumnsSize: Int = 4,
        override val beneficiaryAvatarSize: Int = R.dimen.beneficiary_avatar_size_mobile,
        override val beneficiaryItemSpacing: Dp = AppSpacing.md
    ) : DeviceSpec() {

        @Composable
        override fun qabButtonLabelSize(): TextStyle = MaterialTheme.typography.labelMedium

        @Composable
        override fun quickActionsLabelStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun homeUserGreetingStyle(): TextStyle = MaterialTheme.typography.labelMedium

        @Composable
        override fun homeUserNameStyle(): TextStyle = MaterialTheme.typography.homeUserName

        @Composable
        override fun homeTransactionHistoryLabel(): TextStyle = MaterialTheme.typography.titleMedium

        @Composable
        override fun homeSeeAllLabel(): TextStyle = MaterialTheme.typography.labelLarge

        @Composable
        override fun transactionDateHeaderStyle(): TextStyle = MaterialTheme.typography.labelLarge

        @Composable
        override fun transactionListItemCounterPartyNameStyle(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun transactionListItemDateStyle(): TextStyle = MaterialTheme.typography.bodySmall

        @Composable
        override fun transactionListItemMoneyStyle(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun payScreenSectionTitleStyle(): TextStyle = MaterialTheme.typography.titleMedium

        @Composable
        override fun profileSectionTitleStyle(): TextStyle = MaterialTheme.typography.labelMedium

    }

    data class TabPortrait(

        override val textFieldWidth: Float = 0.6f,

        override val qabButtonSize: Int = R.dimen.qab_button_size_tab,
        override val qabButtonIconSize: Int = R.dimen.qab_icon_size_tab,
        override val qabButtonSpacing: Dp = AppSpacing.md,
        override val cardWidth: Float = 0.85f,
        override val topPadding: Dp = AppSpacing.lg,
        override val quickActionsSpacing: Dp = AppSpacing.md,

        override val HomeCardHorizontalPadding: Dp = AppSpacing.lg,
        override val HomeCardVerticalPadding: Dp = AppSpacing.sm,
        override val navBarItemWidth: Int = R.dimen.bottom_nav_item_width_tablet_portrait,
        override val navBarItemheight: Int = R.dimen.bottom_nav_item_height_tablet_portrait,

        override val homeAppBarHorizontalPadding: Dp = AppSpacing.lg,
        override val homeAppBarTopPadding: Dp = AppSpacing.lg,
        override val homeAppBarBottomPadding: Dp = AppSpacing.md,
        override val homeAppBarSpacingVertical: Dp = AppSpacing.xs,
        override val homeAppBarMenuButtonSize: Int = R.dimen.home_appbar_menu_button_size_tablet,

        override val homeScreenCardMinHeight: Int = R.dimen.home_screen_card_min_height_tablet,
        override val homeScreenCardRoundedCorner: Int = R.dimen.home_screen_card_rounded_corner,
        override val homeScreenCardElevation: Int = R.dimen.home_screen_card_elevation,
        override val homeScreenCardInnerBoxRoundedCorner: Int = R.dimen.home_screen_card_inner_box_rounded_corner,
        override val homeScreenCardInnerBoxPadding: Dp = AppSpacing.md,
        override val homeScreenCardLogoSize: Int = R.dimen.home_screen_card_logo_size_tablet,
        override val homeScreenCardSpacingVertical: Dp = AppSpacing.xs,
        override val homeScreenCardWidth: Float = 0.80f,
        override val homeScreenCardAccountInfoPadding: Dp = AppSpacing.md,
        override val homeScreenCardAccountInfoRoundedCorner: Int = R.dimen.home_screen_card_account_info_rounded_corner,
        override val homeScreenCardAccountSectionSpacing: Dp = AppSpacing.xl,

        override val transactionListItemAvatarSize: Int = R.dimen.transaction_list_item_avatar_size_tablet,
        override val transactionListItemHorizontalPadding: Dp = AppSpacing.md,
        override val transactionListItemVerticalPadding: Dp = AppSpacing.sm,
        override val transactionListItemSpacing: Dp = AppSpacing.lg,

        override val transactionDateHeaderPadding: Dp = AppSpacing.md,
        override val transactionDetailAvatarSize: Int = R.dimen.transaction_detail_avatar_size_tablet,

        override val transactionDetailStatusItemWidth: Float = 0.4f,
        override val transactionDetailCardWidth: Float = 0.6f,
        override val transactionDetailCardPadding: Dp = AppSpacing.lg,
        override val transactionDetailDividerPaddingVertical: Dp = AppSpacing.lg,

        override val payScreenButtonSize: Int = R.dimen.pay_screen_button_size_tablet,
        override val payScreenButtonIconSize: Int = R.dimen.pay_screen_button_icon_size_tablet,
        override val payScreenCardPadding: Dp = AppSpacing.lg,
        override val payScreenSectionSpacing: Dp = AppSpacing.xl,


        override val profileAvatarSize: Int = R.dimen.profile_avatar_size_tablet,
        override val profileCardPadding: Dp = AppSpacing.lg,
        override val profileSectionSpacing: Dp = AppSpacing.xl,

        override val profileScreenWidthFaction: Float = 0.6f,

        override val beneficiaryGridColumnsSize: Int = 4,
        override val beneficiaryAvatarSize: Int = R.dimen.beneficiary_avatar_size_tab,
        override val beneficiaryItemSpacing: Dp = AppSpacing.lg
    ) : DeviceSpec() {
        @Composable
        override fun qabButtonLabelSize(): TextStyle = MaterialTheme.typography.titleMedium

        @Composable
        override fun quickActionsLabelStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun homeUserGreetingStyle(): TextStyle = MaterialTheme.typography.labelMedium

        @Composable
        override fun homeUserNameStyle(): TextStyle = MaterialTheme.typography.homeUserNameTablet

        @Composable
        override fun homeTransactionHistoryLabel(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun homeSeeAllLabel(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun transactionDateHeaderStyle(): TextStyle = MaterialTheme.typography.labelLarge

        @Composable
        override fun transactionListItemCounterPartyNameStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun transactionListItemDateStyle(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun transactionListItemMoneyStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun payScreenSectionTitleStyle(): TextStyle = MaterialTheme.typography.headlineSmall

        @Composable
        override fun profileSectionTitleStyle(): TextStyle = MaterialTheme.typography.labelMedium

    }

    data class TabLandscape(

        override val textFieldWidth: Float = 0.5f,

        override val qabButtonSize: Int = R.dimen.qab_button_size_tab,
        override val qabButtonIconSize: Int = R.dimen.qab_icon_size_tab,
        override val qabButtonSpacing: Dp = AppSpacing.md,
        override val cardWidth: Float = 0.70f,
        override val topPadding: Dp = AppSpacing.lg,
        override val quickActionsSpacing: Dp = AppSpacing.md,

        override val HomeCardHorizontalPadding: Dp = AppSpacing.md,
        override val HomeCardVerticalPadding: Dp = AppSpacing.sm,
        override val navBarItemWidth: Int = R.dimen.bottom_nav_item_width_tablet_landscape,
        override val navBarItemheight: Int = R.dimen.bottom_nav_item_height_tablet_landscape,

        override val homeAppBarHorizontalPadding: Dp = AppSpacing.lg,
        override val homeAppBarTopPadding: Dp = AppSpacing.md,
        override val homeAppBarBottomPadding: Dp = AppSpacing.sm,
        override val homeAppBarSpacingVertical: Dp = AppSpacing.xs,
        override val homeAppBarMenuButtonSize: Int = R.dimen.home_appbar_menu_button_size_tablet,

        override val homeScreenCardMinHeight: Int = R.dimen.home_screen_card_min_height_tablet_landscape,
        override val homeScreenCardRoundedCorner: Int = R.dimen.home_screen_card_rounded_corner,
        override val homeScreenCardElevation: Int = R.dimen.home_screen_card_elevation,
        override val homeScreenCardInnerBoxRoundedCorner: Int = R.dimen.home_screen_card_inner_box_rounded_corner,
        override val homeScreenCardInnerBoxPadding: Dp = AppSpacing.md,
        override val homeScreenCardLogoSize: Int = R.dimen.home_screen_card_logo_size_tablet,
        override val homeScreenCardSpacingVertical: Dp = AppSpacing.xs,
        override val homeScreenCardWidth: Float = 0.70f,
        override val homeScreenCardAccountInfoPadding: Dp = AppSpacing.md,
        override val homeScreenCardAccountInfoRoundedCorner: Int = R.dimen.home_screen_card_account_info_rounded_corner,
        override val homeScreenCardAccountSectionSpacing: Dp = AppSpacing.lg,

        override val transactionListItemAvatarSize: Int = R.dimen.transaction_list_item_avatar_size_tablet,
        override val transactionListItemHorizontalPadding: Dp = AppSpacing.md,
        override val transactionListItemVerticalPadding: Dp = AppSpacing.sm,
        override val transactionListItemSpacing: Dp = AppSpacing.lg,

        override val transactionDateHeaderPadding: Dp = AppSpacing.md,
        override val transactionDetailAvatarSize: Int = R.dimen.transaction_detail_avatar_size_mobile,

        override val transactionDetailStatusItemWidth: Float = 0.4f,
        override val transactionDetailCardWidth: Float = 0.6f,
        override val transactionDetailCardPadding: Dp = AppSpacing.lg,
        override val transactionDetailDividerPaddingVertical: Dp = AppSpacing.lg,

        override val payScreenButtonSize: Int = R.dimen.pay_screen_button_size_tablet,
        override val payScreenButtonIconSize: Int = R.dimen.pay_screen_button_icon_size_tablet,
        override val payScreenCardPadding: Dp = AppSpacing.lg,
        override val payScreenSectionSpacing: Dp = AppSpacing.lg,

        override val profileAvatarSize: Int = R.dimen.profile_avatar_size_tablet,
        override val profileCardPadding: Dp = AppSpacing.lg,
        override val profileSectionSpacing: Dp = AppSpacing.lg,

        override val profileScreenWidthFaction: Float = 0.5f,

        override val beneficiaryGridColumnsSize: Int = 4,
        override val beneficiaryAvatarSize: Int = R.dimen.beneficiary_avatar_size_tab,
        override val beneficiaryItemSpacing: Dp = AppSpacing.lg
    ) : DeviceSpec() {
        @Composable
        override fun qabButtonLabelSize(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun quickActionsLabelStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun homeUserGreetingStyle(): TextStyle = MaterialTheme.typography.labelMedium

        @Composable
        override fun homeUserNameStyle(): TextStyle = MaterialTheme.typography.homeUserNameTablet

        @Composable
        override fun homeTransactionHistoryLabel(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun homeSeeAllLabel(): TextStyle = MaterialTheme.typography.bodyMedium

        @Composable
        override fun transactionDateHeaderStyle(): TextStyle = MaterialTheme.typography.labelLarge

        @Composable
        override fun transactionListItemCounterPartyNameStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun transactionListItemDateStyle(): TextStyle = MaterialTheme.typography.bodyLarge

        @Composable
        override fun transactionListItemMoneyStyle(): TextStyle = MaterialTheme.typography.titleLarge

        @Composable
        override fun payScreenSectionTitleStyle(): TextStyle = MaterialTheme.typography.headlineSmall

        @Composable
        override fun profileSectionTitleStyle(): TextStyle = MaterialTheme.typography.labelMedium
    }
}

object DeviceSpecProvider {
//    @Composable
//    fun getCurrentDeviceSpec(windowSizeClass: WindowSizeClass): DeviceSpec {
//
//        val width = windowSizeClass.widthSizeClass
//        val height = windowSizeClass.heightSizeClass
//
//        return when (width) {
//
//            WindowWidthSizeClass.Compact -> {
//                if (height == WindowHeightSizeClass.Compact)
//                    DeviceSpec.MobileLandscape()
//                else
//                    DeviceSpec.MobilePortrait()
//            }
//
//            WindowWidthSizeClass.Medium -> {
//                if (height == WindowHeightSizeClass.Compact)
//                    DeviceSpec.MobileLandscape()
//                else
//                    DeviceSpec.TabPortrait()
//            }
//
//            WindowWidthSizeClass.Expanded -> {
//                if (height == WindowHeightSizeClass.Compact)
//                    DeviceSpec.TabLandscape()
//                else
//                    DeviceSpec.TabPortrait()
//            }
//
//            else -> DeviceSpec.MobilePortrait()
//        }
//    }

        @Composable
        fun getCurrentDeviceSpec(windowSizeClass: WindowSizeClass): DeviceSpec {

            val configuration = LocalConfiguration.current
            val isTablet = configuration.smallestScreenWidthDp >= 600

            val isLandscape =
                windowSizeClass.heightSizeClass == WindowHeightSizeClass.Compact

            return when {
                isTablet && isLandscape -> DeviceSpec.TabLandscape()
                isTablet && !isLandscape -> DeviceSpec.TabPortrait()
                !isTablet && isLandscape -> DeviceSpec.MobileLandscape()
                else -> DeviceSpec.MobilePortrait()
            }
        }

}
package com.example.bankapp.services

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.toolbox.ImageRequest
import com.example.bankapp.R
import com.example.bankapp.entities.dtos.TransactionHistoryItemDto
import com.example.bankapp.entities.types.transaction.LedgerDirection
import com.example.bankapp.entities.types.transaction.TransactionStatus
import com.example.bankapp.entities.types.transaction.TransactionType
import com.example.bankapp.utilities.CurrencyUtils
import com.example.bankapp.utilities.uiAccNo
import com.lowagie.text.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Draws the transaction receipt as a Bitmap using android.graphics.Canvas.
 *
 * All measurements are in pixels at 1080px wide / 2.75 density (MobilePortrait reference).
 * No Compose composables are involved — output is identical on every device.
 *
 * Mirrors the layout of:
 *   TransactionDetailBody  →  top section (avatar, amount, status, date)
 *   TransactionDetailsCard →  card section (bank logo, rows, balance-after)
 */
class TransactionReceiptPainter(private val context: Context) {

    // ─── Canvas spec ────────────────────────────────────────────────────────────
    private val WIDTH = 1080                    // px — fixed reference width
    private val DENSITY = 2.75f                 // reference density

    // Helpers: dp → px at reference density
    private fun dp(value: Float) = (value * DENSITY).roundToInt()
    private fun dp(value: Int)   = (value * DENSITY).roundToInt()
    private fun sp(value: Float) = (value * DENSITY).roundToInt().toFloat()

    // ─── AppSpacing mapped to px ─────────────────────────────────────────────
    private val xs  = dp(4)
    private val sm  = dp(8)
    private val md  = dp(12)
    private val lg  = dp(16)
    private val xl  = dp(20)
    private val xxl = dp(24)
    private val xxxl = dp(32)

    // ─── Layout constants ────────────────────────────────────────────────────
    // Matches MobilePortrait DeviceSpec values
    private val PADDING_H         = dp(16)     // AppSpacing.lg outer horizontal padding
    private val AVATAR_SIZE       = dp(64)     // transaction_detail_avatar_size_mobile ≈ 64dp
    private val STATUS_WIDTH_FRAC = 0.8f       // transactionDetailStatusItemWidth
    private val CARD_WIDTH_FRAC   = 0.9f       // transactionDetailCardWidth
    private val CARD_PADDING      = dp(12)     // transactionDetailCardPadding = AppSpacing.md
    private val DIVIDER_PAD_V     = dp(12)     // transactionDetailDividerPaddingVertical
    private val BANK_LOGO_SIZE    = dp(32)     // bank_logo_size_transaction_screen ≈ 32dp
    private val STATUS_CORNER     = dp(12)     // status_section_rounded_corner ≈ 12dp
    private val CARD_CORNER       = dp(16)     // card_rounded_shape ≈ 16dp
    private val BAL_CARD_CORNER   = dp(12)     // balance_after_card_rounded_shape ≈ 12dp
    private val STATUS_ICON_SIZE  = dp(18)     // status_icon_size ≈ 18dp
    private val DIVIDER_H         = dp(1)

    // ─── Colours from goldenDarkTheme ────────────────────────────────────────
    private val colBackground        = Color.parseColor("#0B1220")
    private val colSurface           = Color.parseColor("#121A2F")
    private val colSurfaceContainer  = Color.parseColor("#1C2640")   // surfaceVariant used as surfaceContainer
    private val colOnSurface         = Color.parseColor("#E8EDF5")
    private val colOnSurfaceVariant  = Color.parseColor("#94A3B8")
    private val colPrimary           = Color.parseColor("#E6D27A")
    private val colTertiary          = Color.parseColor("#4DB89A")
    private val colOnTertiary        = Color.parseColor("#00201A")
    private val colError             = Color.parseColor("#FF6B6B")
    private val colOutlineVariant    = Color.parseColor("#1E2D45")
    private val colOnSecondary       = Color.parseColor("#0B1220")   // used for balance text
    private val colAmountGreen       = Color.parseColor("#4DB89A")   // amountGreenColor = tertiary

    // ─── Text sizes (sp → px float for Paint.textSize) ──────────────────────
    // Approximate Material3 type scale at 2.75 density:
    //   displayLarge  ~57sp  headlineSmall ~24sp  titleMedium  ~16sp
    //   bodyLarge     ~16sp  bodyMedium    ~14sp  bodySmall    ~12sp
    //   labelLarge    ~14sp  labelMedium   ~12sp  labelSmall   ~11sp
    private val tsDisplayLarge  = sp(45f)   // amount — auto-resized down from 57 if needed
    private val tsDisplaySmall  = sp(36f)   // bank name
    private val tsHeadlineSmall = sp(24f)   // balance after value
    private val tsBodyLarge     = sp(16f)
    private val tsBodyMedium    = sp(14f)
    private val tsBodySmall     = sp(12f)
    private val tsLabelLarge    = sp(14f)
    private val tsLabelMedium   = sp(12f)
    private val tsLabelSmall    = sp(11f)

    // ─── Paints ──────────────────────────────────────────────────────────────
    private fun basePaint(color: Int, size: Float, bold: Boolean = false) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = size
        typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Public API
    // ════════════════════════════════════════════════════════════════════════════

    /**
     * Render and return the receipt bitmap.
     * Must be called from a coroutine (suspends to fetch avatar from network if needed).
     */
    suspend fun draw(
        transaction: TransactionHistoryItemDto,
        countryCode: String,
        timezone: String
    ): Bitmap = withContext(Dispatchers.Default) {

        // ── Derive display values (mirrors TransactionDetailBody logic) ────────
        val isCredit    = transaction.ledgerDirection == LedgerDirection.CREDIT
        val isDeposit   = transaction.transactionType == TransactionType.DEPOSIT
        val amountColor = if (isCredit) colAmountGreen else colError
        val amountPrefix = if (isCredit) "+" else "-"

        val displayName = when {
            isDeposit -> "You (Deposit)"
            !transaction.counterpartyNickname.isNullOrEmpty() ->
                "${transaction.counterpartyNickname} (${transaction.counterpartyName})"
            else -> transaction.counterpartyName ?: "Bank"
        }

        val typeLabel = if (isDeposit) "Deposit" else "Cash Transfer"

        val amountText = "$amountPrefix${
            CurrencyUtils.formatCurrency(
                transaction.amount.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                countryCode
            )
        } ${CurrencyUtils.getCurrencySymbol(countryCode)}"

        val dateText  = transaction.transactionDate.toFullDateTimeDisplay()
        val tzText    = "Timezone: $timezone"
        val refText   = transaction.referenceNumber
        val fromLabel = if (isDeposit) "Deposit" else "From"
        val myName    = transaction.myUserName
        val myAccNo   = transaction.myAccountNo.uiAccNo
        val toName    = transaction.counterpartyName ?: "Unknown"
        val toAccNo   = transaction.counterpartyAccountNo?.uiAccNo ?: "-"
        val balanceText = "${
            CurrencyUtils.formatCurrency(
                transaction.balanceAfter.toBigDecimalOrNull() ?: BigDecimal.ZERO,
                countryCode
            )
        } ${CurrencyUtils.getCurrencySymbol(countryCode)}"

        // Status
        val (statusText, statusColor, statusBg) = when (transaction.transactionStatus) {
            TransactionStatus.COMPLETED ->
                Triple("Completed Successfully", colTertiary, withAlpha(colTertiary, 0.2f))
            TransactionStatus.FAILED ->
                Triple("Failed – Insufficient Balance", colError, withAlpha(colError, 0.2f))
            else ->
                Triple("Pending", colTertiary, withAlpha(colTertiary, 0.2f))
        }

        // Avatar bitmap (network or initials fallback)
        val pfpUrl = if (isDeposit) transaction.myPfpUrl else transaction.counterpartyPfpUrl
        // val avatarBitmap = fetchAvatar(pfpUrl, displayName)

        // Bank logo bitmap
        val bankLogoBitmap = getBankLogo()

        // ── First pass: measure total height ─────────────────────────────────
        val layout = measureLayout(
            amountText      = amountText,
            dateText        = dateText,
            tzText          = tzText,
            refText         = refText,
            myName          = myName,
            myAccNo         = myAccNo,
            toName          = toName,
            toAccNo         = toAccNo,
            isDeposit       = isDeposit,
            statusText      = statusText,
            balanceText     = balanceText,
            typeLabel       = typeLabel
        )

        // ── Allocate bitmap ───────────────────────────────────────────────────
        val bitmap = Bitmap.createBitmap(WIDTH, layout.totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // ── Draw background ───────────────────────────────────────────────────
        canvas.drawColor(colBackground)

        // ── Draw content ──────────────────────────────────────────────────────
        renderContent(
            canvas        = canvas,
            layout        = layout,

            bankLogoBitmap= bankLogoBitmap,
            displayName   = displayName,
            typeLabel     = typeLabel,
            amountText    = amountText,
            amountColor   = amountColor,
            statusText    = statusText,
            statusColor   = statusColor,
            statusBg      = statusBg,
            dateText      = dateText,
            tzText        = tzText,
            refText       = refText,
            fromLabel     = fromLabel,
            myName        = myName,
            myAccNo       = myAccNo,
            toName        = toName,
            toAccNo       = toAccNo,
            isDeposit     = isDeposit,
            balanceText   = balanceText
        )

        bitmap
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Layout measurement
    // ════════════════════════════════════════════════════════════════════════════

    private data class ReceiptLayout(
        val totalHeight:      Int,
        // Y positions of each section (top edge)
        val yAvatar:          Int,
        val yTypeLabel:       Int,
        val yAmount:          Int,
        val yStatus:          Int,
        val yDivider:         Int,
        val yDate:            Int,
        val yTz:              Int,
        val yCard:            Int,
        val cardWidth:        Int,
        val cardLeft:         Int,
        val cardHeight:       Int,
        val amountTextSize:   Float,   // auto-fitted
        // inside card
        val yCardBankSection: Int,
        val yCardDivider1:    Int,
        val yCardRef:         Int,
        val yCardDivider2:    Int,
        val yCardFrom:        Int,
        val yCardDivider3:    Int,    // only used when !isDeposit
        val yCardTo:          Int,    // only used when !isDeposit
        val yCardBalCard:     Int,
        val balCardHeight:    Int,
        val statusWidth:      Int,
        val statusLeft:       Int
    )

    private fun measureLayout(
        amountText: String,
        dateText:   String,
        tzText:     String,
        refText:    String,
        myName:     String,
        myAccNo:    String,
        toName:     String,
        toAccNo:    String,
        isDeposit:  Boolean,
        statusText: String,
        balanceText:String,
        typeLabel:  String
    ): ReceiptLayout {

        var y = PADDING_H   // top padding

        // Avatar — centred
        val yAvatar = y
        y += AVATAR_SIZE + md

        // Type label
        val yTypeLabel = y
        y += textHeight(tsLabelMedium).toInt() + md

        // Amount — auto-fit text size
        val amountMaxWidth = WIDTH - PADDING_H * 2
        val amountFitted   = fitTextSize(amountText, amountMaxWidth.toFloat(), tsDisplayLarge, 28f)
        val yAmount = y
        y += textHeight(amountFitted).toInt() + xxl

        // Status pill
        val statusW    = (WIDTH * STATUS_WIDTH_FRAC).toInt()
        val statusLeft = (WIDTH - statusW) / 2
        val statusH    = STATUS_ICON_SIZE + md * 2    // icon + vertical padding
        val yStatus = y
        y += statusH + xxl

        // Full-width divider
        val yDivider = y
        y += DIVIDER_H + md

        // Date
        val yDate = y
        y += textHeight(tsBodySmall).toInt() + xs

        // Timezone
        val yTz = y
        y += textHeight(tsLabelSmall).toInt() + xxl

        // Card
        val cardW    = (WIDTH * CARD_WIDTH_FRAC).toInt()
        val cardLeft = (WIDTH - cardW) / 2

        // Measure card internals
        val cardPad = CARD_PADDING
        var cy = cardPad

        // Bank section: logo + text centred
        val yCardBankSection = cy
        cy += BANK_LOGO_SIZE + md              // bank section height ≈ logo height

        // Divider 1
        val yCardDivider1 = cy
        cy += DIVIDER_H + DIVIDER_PAD_V * 2

        // Ref row
        val yCardRef = cy
        cy += detailRowHeight() + DIVIDER_PAD_V * 2 + DIVIDER_H

        val yCardDivider2 = cy - DIVIDER_PAD_V * 2 - DIVIDER_H   // already included above
        // (split: ref row already adds its divider below)
        cy += 0  // already advanced

        // From row (with subtext = name + masked acc)
        val yCardFrom = cy
        val fromRowH  = detailRowWithSubtextHeight()
        cy += fromRowH

        var yCardDivider3 = 0
        var yCardTo       = 0
        if (!isDeposit) {
            yCardDivider3 = cy
            cy += DIVIDER_H + DIVIDER_PAD_V * 2
            yCardTo = cy
            cy += fromRowH
        }

        cy += xxl   // LargeSpacer before balance card

        // Balance card
        val balCardH = md + textHeight(tsLabelMedium).toInt() + md + textHeight(tsHeadlineSmall).toInt() + md
        val yCardBalCard = cy
        cy += balCardH + cardPad

        val cardH = cy
        val yCard = y
        y += cardH + md + PADDING_H   // bottom padding

        return ReceiptLayout(
            totalHeight      = y,
            yAvatar          = yAvatar,
            yTypeLabel       = yTypeLabel,
            yAmount          = yAmount,
            yStatus          = yStatus,
            yDivider         = yDivider,
            yDate            = yDate,
            yTz              = yTz,
            yCard            = yCard,
            cardWidth        = cardW,
            cardLeft         = cardLeft,
            cardHeight       = cardH,
            amountTextSize   = amountFitted,
            yCardBankSection = yCardBankSection,
            yCardDivider1    = yCardDivider1,
            yCardRef         = yCardRef,
            yCardDivider2    = yCardDivider2,
            yCardFrom        = yCardFrom,
            yCardDivider3    = yCardDivider3,
            yCardTo          = yCardTo,
            yCardBalCard     = yCardBalCard,
            balCardHeight    = balCardH,
            statusWidth      = statusW,
            statusLeft       = statusLeft
        )
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Rendering
    // ════════════════════════════════════════════════════════════════════════════

    private fun renderContent(
        canvas:         Canvas,
        layout:         ReceiptLayout,

        bankLogoBitmap: Bitmap?,
        displayName:    String,
        typeLabel:      String,
        amountText:     String,
        amountColor:    Int,
        statusText:     String,
        statusColor:    Int,
        statusBg:       Int,
        dateText:       String,
        tzText:         String,
        refText:        String,
        fromLabel:      String,
        myName:         String,
        myAccNo:        String,
        toName:         String,
        toAccNo:        String,
        isDeposit:      Boolean,
        balanceText:    String
    ) {
        val cx = WIDTH / 2f

        // ── Avatar ──────────────────────────────────────────────────────────


        // ── Type label ──────────────────────────────────────────────────────
        drawCentredText(
            canvas, typeLabel,
            cx, layout.yTypeLabel + textHeight(tsLabelMedium).toInt(),
            basePaint(colOnSurfaceVariant, tsLabelMedium)
        )

        // ── Amount ──────────────────────────────────────────────────────────
        drawCentredText(
            canvas, amountText,
            cx, layout.yAmount + textHeight(layout.amountTextSize).toInt(),
            basePaint(amountColor, layout.amountTextSize, bold = true)
        )

        // ── Status pill ──────────────────────────────────────────────────────
        val statusTop    = layout.yStatus.toFloat()
        val statusBottom = statusTop + STATUS_ICON_SIZE + md * 2f
        val pillRect     = RectF(
            layout.statusLeft.toFloat(),
            statusTop,
            (layout.statusLeft + layout.statusWidth).toFloat(),
            statusBottom
        )
        val pillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = statusBg }
        canvas.drawRoundRect(pillRect, STATUS_CORNER.toFloat(), STATUS_CORNER.toFloat(), pillPaint)

        // Status text centred in pill
        val statusPaint = basePaint(statusColor, tsLabelMedium, bold = true)
        val statusTextW = statusPaint.measureText(statusText)
        val statusTextY = statusTop + (statusBottom - statusTop) / 2f + textHeight(tsLabelMedium) / 2f
        canvas.drawText(statusText, cx - statusTextW / 2f, statusTextY, statusPaint)

        // ── Full-width divider ───────────────────────────────────────────────
        val divPaint = Paint().apply {
            color = colOutlineVariant
            strokeWidth = DIVIDER_H.toFloat()
        }
        canvas.drawLine(
            PADDING_H.toFloat(), layout.yDivider.toFloat(),
            (WIDTH - PADDING_H).toFloat(), layout.yDivider.toFloat(),
            divPaint
        )

        // ── Date ────────────────────────────────────────────────────────────
        drawCentredText(
            canvas, dateText,
            cx, layout.yDate + textHeight(tsBodySmall).toInt(),
            basePaint(colOnSurfaceVariant, tsBodySmall)
        )

        // ── Timezone ────────────────────────────────────────────────────────
        drawCentredText(
            canvas, tzText,
            cx, layout.yTz + textHeight(tsLabelSmall).toInt(),
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color    = withAlpha(colOnSurfaceVariant, 0.6f)
                textSize = tsLabelSmall
            }
        )

        // ── Details Card ─────────────────────────────────────────────────────
        drawCard(
            canvas     = canvas,
            left       = layout.cardLeft.toFloat(),
            top        = layout.yCard.toFloat(),
            right      = (layout.cardLeft + layout.cardWidth).toFloat(),
            bottom     = (layout.yCard + layout.cardHeight).toFloat(),
            color      = colSurfaceContainer,
            cornerPx   = CARD_CORNER.toFloat(),
            elevation  = dp(4).toFloat()
        )

        // Card internals — offset by card top + padding
        val cardOffsetY = layout.yCard + CARD_PADDING
        val cardInnerL  = layout.cardLeft + CARD_PADDING
        val cardInnerR  = layout.cardLeft + layout.cardWidth - CARD_PADDING
        val cardInnerW  = cardInnerR - cardInnerL

        // Bank section: logo + "Vangi" text centred
        val bankSectionY = cardOffsetY + layout.yCardBankSection
        drawBankSection(canvas, bankLogoBitmap, cx, bankSectionY)

        // Divider 1
        val cardDivPaint = Paint().apply {
            color       = withAlpha(colOutlineVariant, 0.3f)
            strokeWidth = DIVIDER_H.toFloat()
        }
        val div1Y = (cardOffsetY + layout.yCardDivider1 + DIVIDER_PAD_V).toFloat()
        canvas.drawLine(cardInnerL.toFloat(), div1Y, cardInnerR.toFloat(), div1Y, cardDivPaint)

        // Reference ID row
        val refRowY = cardOffsetY + layout.yCardRef
        drawDetailRow(
            canvas, cardInnerL.toFloat(), cardInnerR.toFloat(), refRowY,
            label = "Reference ID",
            value = refText
        )

        // Divider 2
        val div2Y = (refRowY + detailRowHeight() + DIVIDER_PAD_V).toFloat()
        canvas.drawLine(cardInnerL.toFloat(), div2Y, cardInnerR.toFloat(), div2Y, cardDivPaint)

        // From / Deposit row
        val fromRowY = (div2Y + DIVIDER_PAD_V).toInt()
        drawDetailRowWithSubtext(
            canvas, cardInnerL.toFloat(), cardInnerR.toFloat(), fromRowY,
            label = fromLabel,
            name  = myName,
            accNo = "**** ${myAccNo.takeLast(4)}"
        )

        if (!isDeposit) {
            val div3Y = (fromRowY + detailRowWithSubtextHeight() + DIVIDER_PAD_V).toFloat()
            canvas.drawLine(cardInnerL.toFloat(), div3Y, cardInnerR.toFloat(), div3Y, cardDivPaint)

            val toRowY = (div3Y + DIVIDER_PAD_V).toInt()
            drawDetailRowWithSubtext(
                canvas, cardInnerL.toFloat(), cardInnerR.toFloat(), toRowY,
                label = "To",
                name  = toName,
                accNo = "**** ${toAccNo.takeLast(4)}"
            )
        }

        // Balance-after card
        val balCardTop  = (layout.yCard + layout.yCardBalCard).toFloat()
        val balCardBot  = balCardTop + layout.balCardHeight
        val balCardLeft = (layout.cardLeft + CARD_PADDING).toFloat()
        val balCardRight= (layout.cardLeft + layout.cardWidth - CARD_PADDING).toFloat()
        drawCard(
            canvas    = canvas,
            left      = balCardLeft,
            top       = balCardTop,
            right     = balCardRight,
            bottom    = balCardBot,
            color     = colTertiary,
            cornerPx  = BAL_CARD_CORNER.toFloat(),
            elevation = 0f
        )
        // "Balance after transaction" label
        val balLabelPaint = basePaint(withAlpha(colOnTertiary, 0.8f), tsLabelMedium)
        drawCentredText(
            canvas, "Balance after transaction",
            cx, balCardTop + md + textHeight(tsLabelMedium),
            balLabelPaint
        )
        // Balance amount
        val balAmtPaint = basePaint(colOnTertiary, tsHeadlineSmall, bold = true)
        drawCentredText(
            canvas, balanceText,
            cx, balCardTop + md + textHeight(tsLabelMedium) + md + textHeight(tsHeadlineSmall),
            balAmtPaint
        )
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Section drawers
    // ════════════════════════════════════════════════════════════════════════════

    private fun drawCircularAvatar(canvas: Canvas, bitmap: Bitmap, cx: Float, top: Int) {
        val size   = AVATAR_SIZE
        val left   = (cx - size / 2f).toInt()
        val scaled = Bitmap.createScaledBitmap(bitmap, size, size, true)

        val paint  = Paint(Paint.ANTI_ALIAS_FLAG)
        val shader = BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.shader = shader

        canvas.drawCircle(cx, top + size / 2f, size / 2f, paint)
    }

    private fun drawBankSection(canvas: Canvas, logo: Bitmap?, cx: Float, top: Int) {
        val bankNamePaint = basePaint(colPrimary, tsDisplaySmall, bold = true)
        val bankName      = "Vangi"
        val nameW         = bankNamePaint.measureText(bankName)
        val spacing       = sm.toFloat()
        val logoSize      = BANK_LOGO_SIZE

        val totalW = (if (logo != null) logoSize + spacing else 0f) + nameW
        val startX = cx - totalW / 2f

        if (logo != null) {
            val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)
            val logoTop    = top + (textHeight(tsDisplaySmall) - logoSize) / 2f
            canvas.drawBitmap(scaledLogo, startX, logoTop, Paint(Paint.ANTI_ALIAS_FLAG))
        }

        val textX = startX + (if (logo != null) logoSize + spacing else 0f)
        val textY = top + textHeight(tsDisplaySmall)
        canvas.drawText(bankName, textX, textY, bankNamePaint)
    }

    private fun drawDetailRow(
        canvas: Canvas, left: Float, right: Float, top: Int,
        label: String, value: String
    ) {
        val labelPaint = basePaint(colOnSurfaceVariant, tsLabelLarge)
        val valuePaint = basePaint(colOnSurface, tsBodyLarge, bold = true)
        val baseline   = top + textHeight(tsBodyLarge).toInt()
        canvas.drawText(label, left, baseline.toFloat(), labelPaint)
        val valueW = valuePaint.measureText(value)
        canvas.drawText(value, right - valueW, baseline.toFloat(), valuePaint)
    }

    private fun drawDetailRowWithSubtext(
        canvas: Canvas, left: Float, right: Float, top: Int,
        label: String, name: String, accNo: String
    ) {
        val labelPaint   = basePaint(colOnSurfaceVariant, tsLabelLarge)
        val namePaint    = basePaint(colOnSurface, tsBodyLarge, bold = true)
        val subTextPaint = basePaint(colOnSurfaceVariant, tsBodyMedium)

        val nameW    = namePaint.measureText(name)
        val subW     = subTextPaint.measureText("Acc. $accNo")

        val baseline1 = top + textHeight(tsBodyLarge).toInt()
        val baseline2 = baseline1 + xs + textHeight(tsBodyMedium).toInt()

        canvas.drawText(label, left, baseline1.toFloat(), labelPaint)
        canvas.drawText(name,  right - nameW, baseline1.toFloat(), namePaint)
        canvas.drawText("Acc. $accNo", right - subW, baseline2.toFloat(), subTextPaint)
    }

    private fun drawCard(
        canvas: Canvas,
        left: Float, top: Float, right: Float, bottom: Float,
        color: Int, cornerPx: Float, elevation: Float
    ) {
        if (elevation > 0) {
            val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color   = Color.parseColor("#33000000")
                maskFilter   = BlurMaskFilter(elevation, BlurMaskFilter.Blur.NORMAL)
            }
            canvas.drawRoundRect(
                RectF(left + 2, top + elevation, right - 2, bottom + elevation),
                cornerPx, cornerPx, shadowPaint
            )
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        canvas.drawRoundRect(RectF(left, top, right, bottom), cornerPx, cornerPx, fillPaint)
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ════════════════════════════════════════════════════════════════════════════

    private fun drawCentredText(canvas: Canvas, text: String, cx: Float, baselineY: Float, paint: Paint) {
        val w = paint.measureText(text)
        canvas.drawText(text, cx - w / 2f, baselineY, paint)
    }

    private fun drawCentredText(canvas: Canvas, text: String, cx: Float, baselineY: Int, paint: Paint) =
        drawCentredText(canvas, text, cx, baselineY.toFloat(), paint)

    /** Height of a single line of text at a given sp size (cap height approx). */
    private fun textHeight(spSize: Float): Float = spSize * 1.2f
    private fun textHeight(spSize: Int):   Float = spSize * 1.2f

    /** Height of a standard detail row (one line of text + vertical padding). */
    private fun detailRowHeight(): Int = textHeight(tsBodyLarge).toInt() + sm * 2

    /** Height of a detail row that has name + masked acc sub-text. */
    private fun detailRowWithSubtextHeight(): Int =
        textHeight(tsBodyLarge).toInt() + xs + textHeight(tsBodyMedium).toInt() + sm * 2

    /** Fit text size so it never overflows maxWidth. Returns sp size as float. */
    private fun fitTextSize(text: String, maxWidth: Float, startSize: Float, minSize: Float): Float {
        var size = startSize
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = size }
        while (paint.measureText(text) > maxWidth && size > minSize) {
            size -= 1f
            paint.textSize = size
        }
        return size
    }

    private fun withAlpha(color: Int, alpha: Float): Int {
        val a = (alpha * 255).toInt().coerceIn(0, 255)
        return (color and 0x00FFFFFF) or (a shl 24)
    }

    // ════════════════════════════════════════════════════════════════════════════
    //  Asset loading
    // ════════════════════════════════════════════════════════════════════════════

//    private suspend fun fetchAvatar(url: String?, fallbackName: String): Bitmap {
//        if (!url.isNullOrBlank()) {
//            try {
//                val loader  = ImageLoader(context)
//                val request = ImageRequest.Builder(context).data(url).allowHardware(false).build()
//                val result  = loader.execute(request)
//                if (result is SuccessResult) {
//                    val drawable = result.drawable
//                    return when (drawable) {
//                        is BitmapDrawable -> drawable.bitmap
//                        else              -> drawable.toBitmap(AVATAR_SIZE, AVATAR_SIZE)
//                    }
//                }
//            } catch (_: Exception) { }
//        }
//        return makeInitialsBitmap(fallbackName)
//    }

    /** Creates a circular initials avatar matching UserAvatar's fallback style. */
    private fun makeInitialsBitmap(name: String): Bitmap {
        val size    = AVATAR_SIZE
        val bmp     = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas  = Canvas(bmp)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colPrimary }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint)

        val initials = name.trim().split(" ")
            .take(2).joinToString("") { it.firstOrNull()?.uppercaseChar()?.toString() ?: "" }

        val textPaint = basePaint(colOnPrimary(), sp(18f), bold = true)
        val tw        = textPaint.measureText(initials)
        canvas.drawText(initials, size / 2f - tw / 2f, size / 2f + sp(7f), textPaint)
        return bmp
    }

    private fun colOnPrimary() = Color.parseColor("#0B1220")

    private fun getBankLogo(): Bitmap? {
        return try {
            val drawable = ContextCompat.getDrawable(context, R.drawable.bank_logo)
            drawable?.toBitmap(BANK_LOGO_SIZE, BANK_LOGO_SIZE)
        } catch (_: Exception) { null }
    }
}
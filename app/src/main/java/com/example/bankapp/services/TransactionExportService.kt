package com.example.bankapp.services

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import com.example.bankapp.entities.dtos.ExportMetadata
import com.example.bankapp.entities.dtos.TransactionExportDto
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.io.File
import kotlin.collections.take
import java.io.FileOutputStream
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionExportService(private val context: Context) {
    fun createCsvFile(
        metadata: ExportMetadata,
        transactions: List<TransactionExportDto>,
        fileName: String
    ): File {
        val file = File(context.cacheDir, "$fileName.csv")

        val limitedTransactions = transactions.take(10000)

        csvWriter().open(file) {
            writeRow("BANK STATEMENT")
            writeRow("Account Holder:", metadata.userName)
            writeRow("Account Number:", metadata.accountNo)
            writeRow("Generated On:", metadata.dateGenerated)
            writeRow("")
            writeRow("")


            writeRow(
                "Date",
                "Description",
                "Type",
                "De/Cr",
                "Amount",
                "Balance After",
                "Status",
                "Reference"
            )

            limitedTransactions.forEach { item ->
                writeRow(
                    item.date,
                    item.description,
                    item.type,
                    item.direction,
                    item.amount,
                    item.balanceAfter,
                    item.status,
                    item.reference
                )
            }
        }
        return file
    }

    fun createPdfFile(
        metadata: ExportMetadata,
        transactions: List<TransactionExportDto>,
        fileName: String
    ): File {
        val file = File(context.cacheDir, "$fileName.pdf")
        val pdfDocument = PdfDocument()

        val pageHeight = 1120
        val pageWidth = 792
        var pageNumber = 1

        val colWidths = floatArrayOf(80f, 180f, 70f, 50f, 80f, 80f, 70f, 80f)
        val startX = 50f
        val colsX = FloatArray(colWidths.size)
        var currentX = startX
        for (i in colWidths.indices) {
            colsX[i] = currentX
            currentX += colWidths[i]
        }

        val titlePaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 20f
            color = Color.BLACK
        }
        val headerPaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 10f
        }
        val textPaint = TextPaint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 9f
        }

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var yPos = 50f

        // Header Info
        canvas.drawText("BANK STATEMENT", 50f, yPos, titlePaint)
        yPos += 30f
        canvas.drawText("Account Holder: ${metadata.userName}", 50f, yPos, textPaint)
        yPos += 15f
        canvas.drawText("Account Number: ${metadata.accountNo}", 50f, yPos, textPaint)
        yPos += 15f
        canvas.drawText("Generated On: ${metadata.dateGenerated}", 50f, yPos, textPaint)
        yPos += 40f

        // Table Headers
        val headers = listOf("Date", "Description", "Type", "De/Cr", "Amount", "Balance", "Status", "Ref")
        val headerRectPaint = Paint().apply { color = Color.parseColor("#F2F2F2") }
        canvas.drawRect(startX - 5f, yPos - 15f, pageWidth - 40f, yPos + 10f, headerRectPaint)

        headers.forEachIndexed { i, s ->
            canvas.drawText(s, colsX[i], yPos, headerPaint)
        }
        yPos += 25f

        // 2. Dynamic Row Generation
        transactions.forEach { item ->
            val rowData = listOf(
                item.date,
                item.description, // No more truncation!
                item.type,
                item.direction,
                item.amount,
                item.balanceAfter,
                item.status,
                item.reference
            )

            // Create layouts for each cell to determine the max height needed for this row
            val layouts = rowData.mapIndexed { i, text ->
                StaticLayout.Builder.obtain(text, 0, text.length, textPaint, colWidths[i].toInt() - 10)
                    .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(0f, 1f)
                    .setIncludePad(false)
                    .build()
            }

            val maxHeight = layouts.maxOf { it.height }.toFloat() + 10f // Padding

            // Check for Page Break
            if (yPos + maxHeight > pageHeight - 50f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f

                // Re-draw headers on new page
                canvas.drawRect(startX - 5f, yPos - 15f, pageWidth - 40f, yPos + 10f, headerRectPaint)
                headers.forEachIndexed { i, s -> canvas.drawText(s, colsX[i], yPos, headerPaint) }
                yPos += 25f
            }

            // Draw each cell using the calculated layouts
            layouts.forEachIndexed { i, layout ->
                canvas.save()
                canvas.translate(colsX[i], yPos - 10f) // Adjust for baseline
                layout.draw(canvas)
                canvas.restore()
            }

            // Draw a subtle separator line
            val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 0.5f }
            canvas.drawLine(startX - 5f, yPos + maxHeight - 5f, pageWidth - 40f, yPos + maxHeight - 5f, linePaint)

            yPos += maxHeight
        }

        pdfDocument.finishPage(page)
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.close()

        return file
    }




    suspend fun shareBitmap(bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val cachePath = File(context.cacheDir, "shared_images")
                cachePath.mkdirs()
                val file = File(cachePath, "transaction_receipt.png")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )

                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Share Receipt"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}


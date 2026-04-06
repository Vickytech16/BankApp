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

        val paint = Paint()
        val titlePaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 20f
        }
        val headerPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 12f
        }
        val textPaint = Paint().apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            textSize = 10f
        }

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas

        var yPos = 50f
        canvas.drawText("BANK STATEMENT", 50f, yPos, titlePaint)
        yPos += 30f
        canvas.drawText("Account Holder: ${metadata.userName}", 50f, yPos, textPaint)
        yPos += 20f
        canvas.drawText("Account Number: ${metadata.accountNo}", 50f, yPos, textPaint)
        yPos += 20f
        canvas.drawText("Generated On: ${metadata.dateGenerated}", 50f, yPos, textPaint)
        yPos += 40f

        val cols = floatArrayOf(50f, 130f, 280f, 350f, 410f, 480f, 560f, 650f)
        val headers = listOf("Date", "Description", "Type", "De/Cr", "Amount", "Balance", "Status", "Ref")

        paint.color = Color.LTGRAY
        canvas.drawRect(45f, yPos - 15f, 750f, yPos + 10f, paint)

        headers.forEachIndexed { index, s ->
            canvas.drawText(s, cols[index], yPos, headerPaint)
        }
        yPos += 30f

        transactions.take(10000).forEach { item ->
            if (yPos > pageHeight - 50f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPos = 50f
            }

            canvas.drawText(item.date, cols[0], yPos, textPaint)
            canvas.drawText(item.description.take(20), cols[1], yPos, textPaint) // Truncate to fit
            canvas.drawText(item.type, cols[2], yPos, textPaint)
            canvas.drawText(item.direction, cols[3], yPos, textPaint)
            canvas.drawText(item.amount, cols[4], yPos, textPaint)
            canvas.drawText(item.balanceAfter, cols[5], yPos, textPaint)
            canvas.drawText(item.status, cols[6], yPos, textPaint)
            canvas.drawText(item.reference.take(12), cols[7], yPos, textPaint)

            yPos += 25f
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


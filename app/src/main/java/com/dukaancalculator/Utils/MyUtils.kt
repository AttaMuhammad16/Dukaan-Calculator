package com.dukaancalculator.Utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.atta.dukaancalculator.R
import com.dukaancalculator.ui.models.commonmodels.ProductModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.BorderRadius
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.VerticalAlignment
import com.lymors.lycommons.utils.MyExtensions.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.reflect.full.memberProperties


object MyUtils {


    fun String.containsAny(list:List<String> , ignoreCase:Boolean = true):Boolean{
        list.forEach {
            if (this.contains(it , ignoreCase)){
                return true
            }
        }
        return false
    }

    fun String.containsAll(list: List<String>, ignoreCase: Boolean = true): Boolean {
        val searchString = if (ignoreCase) this.lowercase() else this
        return list.all { searchString.contains(it, ignoreCase) }
    }


    fun Any.logT(append:String , tag:String = "TAG"){
        android.util.Log.i(tag,append+this.toString())
    }

    fun Activity.statusBarColor(color:Int= R.color.tool_bar_color){
        this.window.statusBarColor= ContextCompat.getColor(this,color)
    }

    fun Activity.systemBottomNavigationColor(context: Context, color: Int=R.color.app_color) {
        this.window.navigationBarColor = ContextCompat.getColor(context, color)
    }

    fun showProgressDialog(context: Context, message: String): Dialog {
        val progressDialog = Dialog(context)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)

        val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val messageTextView = view.findViewById<TextView>(R.id.messageTextView)
        messageTextView.text = message
        progressDialog.setContentView(view)
        progressDialog.show()
        return progressDialog
    }

    fun getFormattedTime(currentTimeMillis: String): String {
        if (currentTimeMillis.isEmpty()) {
            return "Invalid timestamp"
        }
        return try {
            val timestamp = currentTimeMillis.toLong()
            val dateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val date = Date(timestamp)
            dateFormat.format(date)
        } catch (e: NumberFormatException) {
            "Invalid timestamp"
        }
    }


    fun getFormattedDateAndTime(currentTimeMillis: Long,pattern:String): String {
        return try {
            val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            val date = Date(currentTimeMillis)
            dateFormat.format(date)
        } catch (e: NumberFormatException) {
            "Invalid timestamp"
        }
    }





    inline fun <T, VB : ViewBinding> RecyclerView.setData(items: List<T>, crossinline bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> VB, crossinline bindHolder: (binding: VB, item: T, position: Int) -> Unit) {
        val adapter = object : RecyclerView.Adapter<DataViewHolder<VB>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder<VB> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = bindingInflater(layoutInflater, parent, false)
                return DataViewHolder(binding)
            }
            override fun onBindViewHolder(holder: DataViewHolder<VB>, position: Int) {
                bindHolder(holder.binding, items[position], position)
            }
            override fun getItemCount(): Int {
                return  items.size
            }
        }
        this.adapter = adapter
    }

    class DataViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root)



    fun showKeyBoard(context: Activity,view: View){
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    fun hideKeyboard(context: Activity,view: View){
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    fun pickImage(requestCode: Int,context: Activity){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        context.startActivityForResult(intent, requestCode)
    }

    @SuppressLint("IntentReset")
    fun pickVideo(requestCode: Int, context: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/*"
        context.startActivityForResult(intent, requestCode)
    }

    fun pickDocument(requestCode: Int, context: Activity) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/*"
        context.startActivityForResult(intent, requestCode)
    }


    fun Any.shrink(): Map<String, Any> {
        val propertiesMap = mutableMapOf<String, Any>()
        this::class.memberProperties.forEach { prop ->
            val value = prop.getter.call(this)
            when (value) {
                is Boolean -> if (value) propertiesMap[prop.name] = value
                is Int -> if (value != 0) propertiesMap[prop.name] = value
                is Double -> if (value != 0.0) propertiesMap[prop.name] = value
                is Float -> if (value != 0.0f) propertiesMap[prop.name] = value
                is Long -> if (value != 0L) propertiesMap[prop.name] = value
                is String -> if (value.isNotEmpty()) propertiesMap[prop.name] = value
                is List<*> -> if (value.isNotEmpty()) propertiesMap[prop.name] = value
                is Short -> if (value != 0.toShort()) propertiesMap[prop.name] = value
                is Byte -> if (value != 0.toByte()) propertiesMap[prop.name] = value
                is Char -> if (value != '\u0000') propertiesMap[prop.name] = value // '\u0000' is the null char
                is Set<*> -> if (value.isNotEmpty()) propertiesMap[prop.name] = value
                is Map<*, *> -> if (value.isNotEmpty()) propertiesMap[prop.name] = value
                is Date -> propertiesMap[prop.name] = value
                is Any -> if (value::class.isData) propertiesMap[prop.name] = value.shrink()
            }
        }
        return propertiesMap
    }

    fun signInWithGoogle(context: Activity,GOOGLEREQUESTCODE:Int,mGoogleSignInClient:GoogleSignInClient) {
        val signInIntent = mGoogleSignInClient.signInIntent
        context.startActivityForResult(signInIntent,GOOGLEREQUESTCODE)
    }

    suspend fun signInWithGoogleFirebase(context: Activity,idToken:String,auth: FirebaseAuth):MyResult<String>{
        val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
        return try {
            auth.signInWithCredential(firebaseCredential).await()
            MyResult.Success("Successfully SignIn")
        }catch (e:Exception){
            MyResult.Error(e.message.toString())
        }
    }

     fun sendMessageToWhatsApp(context: Context,phoneNumber:String,message: String){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("https://wa.me/$phoneNumber/?text=${Uri.encode(message)}")
        context.startActivity(intent)
    }

    fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.ENGLISH)
        return dateFormat.format(Date())
    }

    fun generateReceiptNumber(): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val prefix = "${alphabet.random()}${alphabet.random()}"
        val suffix = (10..99).random()
        return "Receipt# $prefix-$suffix"
    }


    fun generateFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "dukaancalculator_receipt_$timeStamp.png"
    }

    fun requestWritePermission(activity: Activity, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE), requestCode)
        } else {
            // Permission already granted
        }
    }

    fun smsPermission(activity: Activity, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS), requestCode)
        } else {
            // Permission already granted
        }
    }


    fun checkProductNamesEdt(productModelList: List<ProductModel>, listOfEditText: List<EditText>, context: Activity): Boolean {
        for ((i, model) in productModelList.withIndex()) {
            if (model.productName.isEmpty()) {
                val edt = listOfEditText[i]
                edt.requestFocus()
                context.showToast("Enter Product Name")
                return true
            }
        }
        return false
    }


    fun shareFileWithOthersViaUri(context: Context, uri:Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share File")
        context.startActivity(chooserIntent)
    }


    fun calculateAmount(totalAmount: Float, percentage: Int): Float {
        return (percentage.toFloat() / 100) * totalAmount
    }
    fun calculatePercentageChange(totalAmount: Float, amount: Float): Float {
        return (amount / totalAmount) * 100
    }

    @SuppressLint("NewApi")
    fun getCurrentDate(pattern: String="dd MM yyyy"):String{
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        val currentDate = LocalDate.now().format(formatter)
        return currentDate
    }


//    fun Context.sendSMS(phoneNumber: String, message: String) {
//        try {
//            val smsManager: SmsManager = SmsManager.getDefault()
//            val parts = smsManager.divideMessage(message)
//            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
//            Toast.makeText(this, "SMS sent successfully", Toast.LENGTH_SHORT).show()
//        } catch (e: Exception) {
//            Toast.makeText(this, "SMS failed to send", Toast.LENGTH_SHORT).show()
//            e.printStackTrace()
//            Log.i("TAG", "sendSMS:${e.message}")
//        }
//    }

    fun Context.sendSMS(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:$phoneNumber") // This ensures only SMS apps respond
            putExtra("sms_body", message)
        }
        startActivity(intent)
    }


//    fun Context.makeACall(phoneNumber: String){
//        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
//        startActivity(intent)
//    }

    fun Context.makeACall(phoneNumber: String){
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }



    fun Activity.showSmsDialog(number:String,text: String=""){

        val dialog= AlertDialog.Builder(this).setView(R.layout.send_sms_dialog).show()
        val phoneNumberEdt=dialog.findViewById<EditText>(R.id.phoneNumberEdt)
        val messageEdt=dialog.findViewById<EditText>(R.id.messageEdt)
        val cancelBtn=dialog.findViewById<Button>(R.id.cancelBtn)
        val sendBtn=dialog.findViewById<Button>(R.id.sendBtn)
        phoneNumberEdt.setText(number)

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        messageEdt.setText(text)

        sendBtn.setOnClickListener {
            val phoneNumber=phoneNumberEdt.text.toString().trim()
            val message=messageEdt.text.toString()
            if (phoneNumber.isEmpty()){
                showToast("Enter PhoneNumber")
            }else if (message.isEmpty()){
                showToast("Enter your message")
            }else{
                sendSMS(phoneNumber, message)
                dialog.dismiss()
            }
        }
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }






    fun getColor(context: Activity, color: Int): Int {
        return when (color) {
            1 -> ContextCompat.getColor(context, R.color.item1)
            2 -> ContextCompat.getColor(context, R.color.item2)
            3 -> ContextCompat.getColor(context, R.color.item3)
            4 -> ContextCompat.getColor(context, R.color.item4)
            5 -> ContextCompat.getColor(context, R.color.item5)
            6 -> ContextCompat.getColor(context, R.color.item6)
            7 -> ContextCompat.getColor(context, R.color.item7)
            8 -> ContextCompat.getColor(context, R.color.item8)
            else -> ContextCompat.getColor(context, R.color.white)
        }
    }

    fun increaseAndDecreaseDay(currentDateStr: String, daysToAdd: Int): String {
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val currentDate = dateFormat.parse(currentDateStr)
        val calendar = Calendar.getInstance().apply {
            time = currentDate ?: Date()
            add(Calendar.DAY_OF_YEAR, daysToAdd)
        }
        return dateFormat.format(calendar.time)
    }

    fun convertDateToDayMonthYearFormat(dateStr: String): String {
        val inputDateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        val date = inputDateFormat.parse(dateStr)
        return outputDateFormat.format(date ?: Date())
    }


    fun showDatePickerDialog(context: Activity, onDateSelected: (String) -> Unit) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val dateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            onDateSelected(formattedDate)
        }, year, month, day)

        dpd.show()
    }

    fun convertToYearMonthDayFormate(dateStr: String): String {
        val inputFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

        val date: Date = inputFormat.parse(dateStr) ?: return ""
        return outputFormat.format(date)
    }







    @SuppressLint("ResourceType")
    suspend fun createPDFReceipt(
        context: Context,
        receiptName:String,
        number:String,
        receiptNumber:String,
        currentDate:String,
        fromCashOrUdhhar:String,
        cashOrUdhharAmount:String,
        subTotalAmount:String="",
        grandTotalAmount:String,
        list: List<ProductModel>,
        distributorOrCompanyName:String="",
        supplierName:String="",
        supplierNumber:String=""
    ):Uri? {

        val environment= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val outputFile= File(environment, "${generateFileName()}.pdf")

        val pdfWriter = PdfWriter(FileOutputStream(outputFile.path))
        val pdfDocument = com.itextpdf.kernel.pdf.PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A2, true)


        // Create a Table for Name
        val name = Table(1).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setHorizontalAlignment(HorizontalAlignment.CENTER)
        }
        val schoolName = Paragraph(receiptName).apply {
            setTextAlignment(TextAlignment.CENTER)
            setFontSize(37f)
            setBold()
            setFontColor(ColorConstants.BLACK)
            setMarginBottom(10f)
            setMarginTop(10f)
            width = UnitValue.createPercentValue(100f)
        }
        val schoolNameCell = Cell().apply {
            add(schoolName)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setBorder(null)
            setMarginTop(10f)
        }
        name.addCell(schoolNameCell)
        document.add(name)


        // Create a Table for number
        simpleCenteredText(number,document)


        // lineSeparator
        lineSeparator(document)


        // receipt Number
        simpleCenteredText(receiptNumber,document)

//        Current Date
        simpleCenteredText(currentDate,document)


        // lineSeparator
//        lineSeparator(document)


        if (distributorOrCompanyName.isNotEmpty()){

            // distributorOrCompanyName
            twoHorizontalText("Distributor/CompanyName",distributorOrCompanyName,document)
            // lineSeparator
//            lineSeparator(document)

            // supplierName
            twoHorizontalText("Supplier Name",supplierName,document)
            // lineSeparator
//            lineSeparator(document)

            // supplierNumber
            twoHorizontalText("Supplier Number",supplierNumber,document)
            // lineSeparator
//            lineSeparator(document)
        }







        // name ,price, qty,total text
        namePriceQtyTotal(true,document)

        for ((i,model) in list.withIndex()){
            namePriceQtyTotal(false,document,model.productName,model.productPrice,model.productQuantity,model.total)
        }

        if (subTotalAmount.isNotEmpty()){
            // Sub total
            twoHorizontalText("Sub Total",subTotalAmount,document)
        }

        // udhhar , cash and amount
        twoHorizontalText(fromCashOrUdhhar,cashOrUdhharAmount,document)

        // grandTotal
        twoHorizontalText("Grand Total",grandTotalAmount,document)

        // lineSeparator
        lineSeparator(document)

        //greeting last text
        simpleCenteredText("Thank You,Visit Again",document)


        val pdfCanvas = PdfCanvas(pdfDocument.firstPage)
        val lastCanvas = PdfCanvas(pdfDocument.lastPage)

        pdfCanvas.setLineWidth(1f)
        pdfCanvas.setFillColor(DeviceRgb(255, 0, 0))
        pdfCanvas.rectangle(36.0, 36.0, (pdfDocument.defaultPageSize.width - 72f).toDouble(), (pdfDocument.defaultPageSize.height - 72f).toDouble())
        pdfCanvas.stroke()

        lastCanvas.setLineWidth(1f)
        lastCanvas.setFillColor(DeviceRgb(255, 0, 0))
        lastCanvas.rectangle(36.0, 36.0, (pdfDocument.defaultPageSize.width - 72f).toDouble(), (pdfDocument.defaultPageSize.height - 72f).toDouble())
        lastCanvas.stroke()
        document.close()
        val fileUri = if (outputFile.exists()) {
            FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                outputFile
            )
        } else {
            null
        }
        withContext(Dispatchers.Main){
            Toast.makeText(context, "receipt is Created ", Toast.LENGTH_SHORT).show()
        }

        return fileUri
    }
    fun twoHorizontalText(text:String, amount:String, document: Document){
        val textSize=40f
        val cashPara = Paragraph(text).apply {
            setTextAlignment(TextAlignment.LEFT)
            setFontSize(textSize)
            setBold()
            setMarginLeft(40f)
            setFontColor(ColorConstants.BLACK)
        }

        val cashCell = Cell().apply {
            add(cashPara)
            setBorder(null)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
        }

        val amountPara = Paragraph(amount).apply {
            setTextAlignment(TextAlignment.RIGHT)
            setFontSize(textSize)
            setBold()
            setFontColor(ColorConstants.BLACK)
            setMarginTop(5f)
            setMarginRight(40f)
            setBorderRadius(BorderRadius(5f))
        }

        val amountCell = Cell().apply {
            add(amountPara)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setBorder(null)
        }

        val table = Table(2).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setHorizontalAlignment(HorizontalAlignment.CENTER)
        }

        table.addCell(cashCell)
        table.addCell(amountCell)
        document.add(table)
    }

    fun lineSeparator(document: Document){
        val line = Table(1).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setHorizontalAlignment(HorizontalAlignment.CENTER)
        }

        val lineSeparator = Paragraph("______________________________________________").apply {
            setTextAlignment(TextAlignment.CENTER)
            setFontColor(ColorConstants.BLACK)
            setFontSize(40f)
            width = UnitValue.createPercentValue(100f)
        }
        val separatorCell = Cell().apply {
            add(lineSeparator)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setBorder(null)
        }
        line.addCell(separatorCell)
        document.add(line)
    }


    fun namePriceQtyTotal(isBold:Boolean, document: Document, name:String="Name", price:String="Price", qty:String="Qty", total:String="Total"){
        // name
        val textSize=40f
        val nameT = Paragraph(name).apply {
            setTextAlignment(TextAlignment.LEFT)
            setFontSize(textSize)
            if (isBold){
                setBold()
            }
            setMarginLeft(20f)
            setFontColor(ColorConstants.BLACK)
        }
        val nameCell = Cell().apply {
            add(nameT)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setWidth(UnitValue.createPercentValue(20f))
        }


        // price
        val pricePara = Paragraph(price).apply {
            setTextAlignment(TextAlignment.CENTER)
            setFontSize(textSize)
            if (isBold){
                setBold()
            }
            setFontColor(ColorConstants.BLACK)
            width = UnitValue.createPercentValue(100f)
        }
        val priceCell = Cell().apply {
            add(pricePara)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setWidth(UnitValue.createPercentValue(20f))
        }

//      qty
        val qtyPara = Paragraph(qty).apply {
            setTextAlignment(TextAlignment.CENTER)
            setFontSize(textSize)
            if (isBold){
                setBold()
            }
            setFontColor(ColorConstants.BLACK)
        }
        val qtyCell = Cell().apply {
            add(qtyPara)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setWidth(UnitValue.createPercentValue(20f))
        }




//        total
        val totalPara = Paragraph(total).apply {
            setTextAlignment(TextAlignment.CENTER)
            setFontSize(textSize)
            if (isBold){
                setBold()
            }
            setFontColor(ColorConstants.BLACK)
            setMarginBottom(5f)
        }
        val totalCell = Cell().apply {
            add(totalPara)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setWidth(UnitValue.createPercentValue(20f))
        }


        val tablepricenameqty = Table(4).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setHorizontalAlignment(HorizontalAlignment.CENTER)
            setMarginLeft(30f)
            setMarginRight(30f)
        }

        tablepricenameqty.addCell(nameCell)
        tablepricenameqty.addCell(priceCell)
        tablepricenameqty.addCell(qtyCell)
        tablepricenameqty.addCell(totalCell)
        document.add(tablepricenameqty)
    }
    fun simpleCenteredText(text:String,document: Document){
        val table = Table(1).apply {
            setWidth(UnitValue.createPercentValue(100f))
            setHorizontalAlignment(HorizontalAlignment.CENTER)
        }
        val text = Paragraph(text).apply {
            setTextAlignment(TextAlignment.CENTER)
            setFontSize(40f)
            setFontColor(ColorConstants.BLACK)
            setMarginBottom(5f)
            setMarginTop(2f)
            width = UnitValue.createPercentValue(100f)
        }
        val dateCell = Cell().apply {
            add(text)
            setVerticalAlignment(VerticalAlignment.MIDDLE)
            setBorder(null)
            setMarginTop(2f)
        }
        table.addCell(dateCell)
        document.add(table)
    }




























}
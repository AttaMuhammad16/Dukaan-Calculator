package com.dukaancalculator.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.databinding.FragmentSalesBinding
import com.dukaancalculator.Utils.MyUtils.logT
import com.dukaancalculator.ui.activities.salemodule.TotalSaleProductActivity
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.dukaancalculator.ui.viewmodel.SaleViewModel
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.MyExtensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.mariuszgromada.math.mxparser.Expression
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject


@AndroidEntryPoint
class SalesFragment : Fragment() {
    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var saleViewModel: SaleViewModel

    private val binding by viewBinding(FragmentSalesBinding::inflate)

    var data = StringBuilder()
    var exp = ArrayList<String>()
    lateinit var list:ArrayList<String>
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saleViewModel.setCalculatorTopData("")

        list= arrayListOf()
        saleViewModel.exp= list

        lifecycleScope.launch {
            saleViewModel.calculatorData.collect {
                binding.main.text = it
                it.logT("fromViewmodel data:")
            }
        }

        lifecycleScope.launch {
            saleViewModel.topFlow.collect {
                binding.total.text = it
                it.logT("fromViewmodel top:")
            }
        }

        binding.one.setOnClickListener { send(it)}
        binding.two.setOnClickListener { send(it) }
        binding.three.setOnClickListener { send(it) }
        binding.four.setOnClickListener { send(it) }
        binding.five.setOnClickListener { send(it) }
        binding.six.setOnClickListener { send(it) }
        binding.seven.setOnClickListener { send(it) }
        binding.eight.setOnClickListener { send(it) }
        binding.nine.setOnClickListener { send(it) }
        binding.zero.setOnClickListener { send(it) }
        binding.plus.setOnClickListener { if (data.isNotEmpty() && !endsWithSymbol(data.toString())){send(it) } }
        binding.divide.setOnClickListener { if (data.isNotEmpty() && !endsWithSymbol(data.toString())){send(it) } }
        binding.multiply.setOnClickListener { if (data.isNotEmpty() && !endsWithSymbol(data.toString())){send(it) } }
        binding.minus.setOnClickListener  { if (data.isNotEmpty() && !endsWithSymbol(data.toString())){send(it) } }
//        binding.percentage.setOnClickListener  {  }
        binding.dot.setOnClickListener  { if (data.isNotEmpty() && !endsWithSymbol(data.toString())){send(it) } }

        binding.receipt.setOnClickListener {
            if (exp.isNotEmpty()){
                saleViewModel.exp = exp
                saleViewModel.setGrandTotalAmount("")
                saleViewModel.setDiscountAmount("")
                saleViewModel.setCalculatorData("")
                val intent = Intent(requireActivity(), TotalSaleProductActivity::class.java)
                startActivity(intent)
            }else{
                showToast("Enter your data")
            }
        }

        binding.clear.setOnClickListener {
            if (data.isNotEmpty()) {
                data.deleteCharAt(data.length - 1)
                saleViewModel.setCalculatorData(data.toString())
            }
        }

        binding.clear.setOnLongClickListener {
            saleViewModel.setGrandTotalAmount("")
            saleViewModel.setDiscountAmount("")
            saleViewModel.setCalculatorData("")
            saleViewModel.setCalculatorTopData("")
            list.clear()
            exp.clear()
            saleViewModel.exp.clear()

            if (data.isEmpty()){
                saleViewModel.setCalculatorTopData("")
            }
            data.clear()
            true
        }


        binding.nextTv.setOnClickListener{
            if (endsWithSymbol(data.toString())){
                return@setOnClickListener
            }
            if (data.toString().split("×").size == 2 && !data.contains("+")&& !data.contains("-")&& !data.contains("÷")){
                exp.add(data.toString())
                if (saleViewModel.topFlow.value.isNotEmpty()){
                    saleViewModel.setCalculatorTopData( saleViewModel.topFlow.value + " | "+data.toString())
                }else{
                    saleViewModel.setCalculatorTopData(data.toString())
                }
                data.clear()
                saleViewModel.setCalculatorData("")
                return@setOnClickListener
            }
            val result = Expression(data.toString()).calculate()
            if (!result.isNaN()){
                exp.add(getRoundedNumber(result))
                data.clear()
                saleViewModel.setCalculatorData(data.toString())
                if (saleViewModel.topFlow.value.isNotEmpty()){
                    saleViewModel.setCalculatorTopData( saleViewModel.topFlow.value + " | "+getRoundedNumber(result))
                }else{
                    saleViewModel.setCalculatorTopData(getRoundedNumber(result))
                }
            }
        }

    }


    private fun send(view: View){
        val t = view as TextView
        data.append(t.text.toString())
        saleViewModel.setCalculatorData(data.toString())
    }

    private fun endsWithSymbol(str:String):Boolean{
        return str.endsWith("÷") || str.endsWith("×") || str.endsWith("-") || str.endsWith("%")

    }

    // Extension function to check if a String is numeric
    fun String.isNumeric(): Boolean {
        return try {
            this.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun getRoundedNumber(number: Double): String{
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number)
    }

}
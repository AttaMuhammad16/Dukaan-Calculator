package com.dukaancalculator.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.atta.dukaancalculator.databinding.FragmentMaalBinding
import com.dukaancalculator.Utils.MyUtils.logT
import com.dukaancalculator.ui.activities.maalmodule.TotalMaalActivity
import com.dukaancalculator.ui.viewmodel.MaalViewModel
import com.dukaancalculator.ui.viewmodel.MainViewModel
import com.lymors.lycommons.utils.MyExtensions.showToast
import com.lymors.lycommons.utils.MyExtensions.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.mariuszgromada.math.mxparser.Expression
import java.math.RoundingMode
import java.text.DecimalFormat
import javax.inject.Inject

@AndroidEntryPoint
class MaalFragment : Fragment() {

    @Inject
    lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var maalViewModel: MaalViewModel

    private val binding by viewBinding(FragmentMaalBinding::inflate)

    var data = StringBuilder()
    var exp = ArrayList<String>()
    lateinit var list:ArrayList<String>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        maalViewModel.setCalculatorTopData("")

        list= arrayListOf()
        maalViewModel.exp= list

        lifecycleScope.launch {
            maalViewModel.calculatorData.collect {
                binding.main.text = it
                it.logT("fromViewmodel data:")
            }
        }
        lifecycleScope.launch {
            maalViewModel.topFlow.collect {
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

        binding.inVoice.setOnClickListener {
            if (exp.isNotEmpty()){
                maalViewModel.exp = exp
                maalViewModel.setGrandTotalAmount("")
                maalViewModel.setCalculatorData("")
                val intent = Intent(requireActivity(), TotalMaalActivity::class.java)
                startActivity(intent)
            }else{
                showToast("Enter your data")
            }
        }

        binding.clear.setOnClickListener {
            if (data.isNotEmpty()) {
                data.deleteCharAt(data.length - 1)
                maalViewModel.setCalculatorData(data.toString())
            }
        }

        binding.clear.setOnLongClickListener {
            maalViewModel.setGrandTotalAmount("")
            maalViewModel.setCalculatorData("")
            maalViewModel.setCalculatorTopData("")
            list.clear()
            exp.clear()
            maalViewModel.exp.clear()

            if (data.isEmpty()){
                maalViewModel.setCalculatorTopData("")
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
                if (maalViewModel.topFlow.value.isNotEmpty()){
                    maalViewModel.setCalculatorTopData( maalViewModel.topFlow.value + " | "+data.toString())
                }else{
                    maalViewModel.setCalculatorTopData(data.toString())
                }
                data.clear()
                maalViewModel.setCalculatorData("")
                return@setOnClickListener
            }
            val result = Expression(data.toString()).calculate()
            if (!result.isNaN()){
                exp.add(getRoundedNumber(result))
                data.clear()
                maalViewModel.setCalculatorData(data.toString())
                if (maalViewModel.topFlow.value.isNotEmpty()){
                    maalViewModel.setCalculatorTopData( maalViewModel.topFlow.value + " | "+getRoundedNumber(result))
                }else{
                    maalViewModel.setCalculatorTopData(getRoundedNumber(result))
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentManager?.popBackStack()
    }

    private fun send(view: View){
        val t = view as TextView
        data.append(t.text.toString())
        maalViewModel.setCalculatorData(data.toString())
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
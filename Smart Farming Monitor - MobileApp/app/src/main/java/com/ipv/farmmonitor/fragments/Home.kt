package com.ipv.farmmonitor.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ipv.farmmonitor.*
import com.ipv.farmmonitor.databinding.FragmentHomeBinding
import com.ipv.farmmonitor.viewmodel.MainViewModel
import com.ipv.farmmonitor.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_bottom_sheet.view.*
import javax.inject.Inject


open class Home : Fragment() {

    @Inject
    lateinit var smartFarmingFactory: ViewModelFactory<MainViewModel>
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(activity,smartFarmingFactory)[MainViewModel::class.java]
    }

    protected val activity get() = context as MainActivity
    private val component get() = activity.component
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.plantationsList.layoutManager = LinearLayoutManager(activity)
        binding.plantationsList.setHasFixedSize(true)

        activity.supportActionBar?.title = getString(R.string.app_name)
        activity.supportActionBar?.subtitle = ""
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        showBottomBar()

        //observe
        viewModel.plantationsAdapter.observe(viewLifecycleOwner){ plantationsAdapter ->
            binding.plantationsList.adapter = plantationsAdapter

            if (plantationsAdapter.itemCount == 0){
                binding.noPlantations.visibility = View.VISIBLE
                binding.addPlant.setOnClickListener{
                    val bottomSheetDialogFragment = BottomSheetFragment()
                    bottomSheetDialogFragment.show(activity.supportFragmentManager,bottomSheetDialogFragment.tag)
                }
            }else{
                binding.noPlantations.visibility = View.GONE
            }
        }
    }


    private fun showBottomBar(){
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val bottomAppBar = requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)

        if(bottomNavigationView.visibility == View.GONE){
            bottomNavigationView.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomNavigationView.visibility = View.VISIBLE
        }
        if(bottomAppBar.visibility == View.GONE){
            bottomAppBar.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            bottomAppBar.visibility = View.VISIBLE
        }
        if(fab.visibility == View.GONE){
            fab.animate()
                .translationY(0f)
                .alpha(1.0f)
                .duration = 300
            fab.visibility = View.VISIBLE
        }
    }


}
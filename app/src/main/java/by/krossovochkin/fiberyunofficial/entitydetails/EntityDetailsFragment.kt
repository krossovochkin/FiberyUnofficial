package by.krossovochkin.fiberyunofficial.entitydetails


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.krossovochkin.fiberyunofficial.R

/**
 * A simple [Fragment] subclass.
 */
class EntityDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entity_details, container, false)
    }


}

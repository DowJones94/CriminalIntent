package ru.boxtoyou.android.criminalintent

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*


private const val TAG = "CrimeListFragment"
private const val ORDINARY_CRIME = 0
private const val SERIOUS_CRIME = 1

class CrimeListFragment : Fragment() {

    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
        //В оригинале было так: ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
        //Но это Deprecated, поэтому переделал, по рекомендациям из гугла :)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)


        // TODO: Запилить импорт из файла (тут добавить кнопку импорта)

//        val tvImport = view.findViewById<TextView>(R.id.tvImport)
//        tvImport.setOnClickListener {
//            crimeListViewModel.onImportClicked()
//        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes -> crimes?.let{
                    Log.i(TAG, "Получены преступления: ${crimes.size} шт")
                    updateUI(crimes)
                }
            }
        )

//        crimeRecyclerView.onCrimesImported.observe()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private fun updateUI(crimes: List<Crime>) {
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }

    private inner class CrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class SeriousCrimeHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val policeButton: Button = itemView.findViewById(R.id.crime_requires_police_button)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        init {
            itemView.setOnClickListener(this)
            policeButton.setOnClickListener {
                Toast.makeText(context, "Виу-виу-виу! Полиция выехала, на расследование " +
                        "преступления №$layoutPosition", Toast.LENGTH_LONG).show()
            }

        }

        fun bind(crime: Crime){
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
//            solvedImageView.isVisible = crime.isSolved
            policeButton.isEnabled = !crime.isSolved
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} было нажато", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            when (viewType) {
                ORDINARY_CRIME -> CrimeHolder(layoutInflater.inflate(R.layout.list_item_crime, parent, false))
                SERIOUS_CRIME -> SeriousCrimeHolder(layoutInflater.inflate(R.layout.list_item_serious_crime, parent, false))
                else -> throw IllegalArgumentException("CrimeListFragment.CrimeAdapter: Unknown viewType on call onCreateViewHolder()")
            }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val crime = crimes[position]

//            if (crimes[position].requiresPolice) {
//                (holder as SeriousCrimeHolder).bind(crime)
//            } else {
//                (holder as CrimeHolder).bind(crime)
//            }

            //Удали код ниже, если раскоментишь то, что на верху
            (holder as CrimeHolder).bind(crime)


        }

        override fun getItemCount() = crimes.size

//        override fun getItemViewType(position: Int): Int {
//            return if (crimes[position].requiresPolice) SERIOUS_CRIME else ORDINARY_CRIME
//        }

    }
}
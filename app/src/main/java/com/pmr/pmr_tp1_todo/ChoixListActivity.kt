package com.pmr.pmr_tp1_todo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.pmr_tp1_todo.model.ListeToDo
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import fr.ec.app.main.adapter.ListAdapter
import kotlinx.coroutines.*
import java.lang.reflect.Type


class ChoixListActivity : AppCompatActivity(), ListAdapter.ActionListener,View.OnClickListener  { // action listener pour le recyclerview et onclick pour le bouton normal
    /* Initialisations */
    var refBtnListOk: Button? = null
    var refAreaList: EditText? = null // pour stocker le texte
    var refProgress: ProgressBar? = null

    /* Gestion des recyclerview */
    private val adapter = newAdapter()

    /* Gestion du lien avec l'API */
    var linkWithAPI = false

    private val activityScope = CoroutineScope(
        SupervisorJob()
                + Dispatchers.Main
//Pas de gestion des erreurs, on fera donc attention à avoir un try catch à chaque coroutine, mais on peut gérer séparément les erreurs
    )
    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG,"OnCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        /* On récupère les views */
        refBtnListOk = findViewById(R.id.btn_list_OK) //id dans la classe R
        refAreaList = findViewById(R.id.area_list)
        refProgress = findViewById(R.id.progressbar)

        /* Listeners sur les boutons */
        refBtnListOk?.setOnClickListener(this)

        /* Listeners sur le champ texte pour les log */
        refAreaList?.setOnClickListener(this)

        //refBtnListOk?.isEnabled=false//désactivé pour la V2, on mettra quand même un toast pour prévenir l'utilisateur
        refAreaList?.isEnabled=false //désactivé pour la V2

        //va initialiser la valeur de la var globale linkWithAPI puis refresh le recyclerview
        loadAPIConnexion()

    }

    override fun onResume() {
        super.onResume()
        loadAPIConnexion()

    }


    /* Création de al fonction pour pouvoir l'appeler à la suite de la création d'une nouvelle liste */
    fun refreshRecyclerView(){
        Log.i(TAG,"fonction refresh du recyclerview appelée")


        /* On trouve la Recycler view */
        val list: RecyclerView = findViewById(R.id.list)

        /* Affichage de toutes les listes pour le pseudo actuel */
        val dataSet = mutableListOf<ListeToDo>()

        if (linkWithAPI){ //si on a le lien avc l'API

            var hash = getHashFromPrefs()

            var context = this

            if (hash!=""){ //évite de déclencher des erreurs de requête
                // avoir les listes de l'utilisateur en question
                activityScope.launch{
                    try{
                        //UX on affiche un chargement quand on demande des données à l'API
                        refProgress?.visibility = View.VISIBLE
                        //UX on cache le reste parce qu'on charge les listes, elles ne sont pas encore là
                        list.visibility = View.GONE
                        var lists = DataProvider.getListsFromApi(hash)
                        Log.i(TAG, "imported from API ${lists}")
                        for(list in lists){
                            dataSet.add(list)
                        }
                        // Pour affichage de la recyclerview /
                        list.adapter = adapter
                        list.layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)

                        adapter.showData(dataSet)
                        refProgress?.visibility = View.GONE
                        list.visibility = View.VISIBLE
                    }
                    catch(e:Exception) {
                        Toast.makeText(context,"Cannot load lists from API", Toast.LENGTH_SHORT).show()
                        Log.e(TAG,e.message)//reporte l'erreur
                    }

                }

            }

        }
        else adapter.showData(dataSet) //sinon on n'affiche rien
    }

    /* Pour recyclerview */
    private fun newAdapter(): ListAdapter {

        val adapter = ListAdapter(
        this
        )
        return adapter
    }

    override fun onListClicked(listeToDo: ListeToDo) {
        Log.d("ChoixListActivity", "onListClicked $listeToDo")
        Toast.makeText(this,"click enregistré sur ${listeToDo.label}",Toast.LENGTH_LONG).show()


        // Passage de la liste cliquée en Bundle //
        val versShowList = Intent(this, ShowListActivity::class.java)
        val b = Bundle()

        Log.i(TAG,"bundle enregistré liste : "+listeToDo.id.toString())

        b.putString("idList", listeToDo.id.toString())
        versShowList.putExtras(b)

        /* On change d'activité */
        Log.i(TAG,"Starting ShowListActivity")
        startActivity(versShowList)

    }


    /* Gestion des champs (bouton+textarea)*/
    override fun onClick(v: View) {
        Log.i(TAG,"OnClick ${v.id}") // v is the clicked view
        when (v.id) {
            R.id.btn_list_OK -> {
                Toast.makeText(this, "Bouton désactivé pour la Séquence 2", Toast.LENGTH_SHORT).show()
            }
        }

    }
//
    /* Gestion des menus */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu);        // maybe change here to change the name in the menu
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        return if (id == R.id.settings){
            Log.i(TAG,"Settings menu option clicked")

            /* Éventuellement toast */
            //Toast.makeText(this, "item settings Clicked", Toast.LENGTH_SHORT).show()

            /* Creation de l'intent pour switcher d'activité */
            val intent = Intent(this,SettingsActivity::class.java)


            /* On change l'activité */
            Log.i(TAG,"Starting SettingsActivity")
            startActivity(intent)
            true
        }
        /* Code si besoin d'autres menus */
        else{

            /* Éventuellement toast */
            // Toast.makeText(this,"elsewhere Clicked",Toast.LENGTH_SHORT).show()

            true
        }
    }

    /* Objet TAG pour pouvoir relever les traces d'exécution */

    companion object {
        private const val TAG = "TRACES_ChoixList"
    }
    // Gestion des enregistrements dans les préférences de l'app //
    fun loadPrefs(): SharedPreferences {
        Log.i(TAG,"Récupération des données de l'application")
        // récupération des données //
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs
    }

    fun getHashFromPrefs(): String {
        Log.i(TAG, "Récupération du hash dans les préférences")
        // Récupération des profils //
        val gson = Gson()
        val json: String? = loadPrefs().getString("hash", "") //lecture de la valeur

        var hash = ""

        if (json != "") {

            val collectionType: Type = object : //surement pas nécessaire
                TypeToken<String>() {}.type

            hash = gson.fromJson(json, collectionType)

        } else {
            Log.i(TAG, "hash NOT found")
        }
        return hash //vide si non trouvé
    }


    private fun loadAPIConnexion(){
        var context = this

        activityScope.launch{

            // Vérification d'accès au réseau //
            //vérification de la présence du hash par exemple//
            try{
                Log.i(TAG, "baseurl : " +DataProvider.BASE_URL)
                var hashtext = DataProvider.getHashFromApi("tom","web")
                Log.i(TAG, "connexion with API hash : ${hashtext}")
                activateBtn(true)
                linkWithAPI=true
                /*Affichage de la recyclerView et initialisation user et appel a loadprefs */
                refreshRecyclerView() //on le met ici pour que ça s'exécute après que le linkwithAPi ait ét changé
            }
            catch(e:Exception) {
                Toast.makeText(context, "Cannot load API Connexion", Toast.LENGTH_SHORT).show()
                activateBtn(false)
                linkWithAPI=false
                /*Affichage de la recyclerView et initialisation user et appel a loadprefs */
                refreshRecyclerView()
                //throw(e)
            }

        }
    }
    private fun activateBtn(bool:Boolean){
        //Puis activation du bouton ou non
        refBtnListOk?.isEnabled = bool
    }


}

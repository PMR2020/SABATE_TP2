package com.pmr.pmr_tp1_todo

import android.content.Context
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
import com.pmr.pmr_tp1_todo.DataProvider.checkItemFromApi
import com.pmr.pmr_tp1_todo.DataProvider.createItemFromApi
import com.pmr.pmr_tp1_todo.DataProvider.getItemsFromApi
import com.pmr.pmr_tp1_todo.adapter.ItemAdapter
import com.pmr.pmr_tp1_todo.model.ItemToDo
import com.pmr.pmr_tp1_todo.model.ListeToDo
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import kotlinx.coroutines.*
import java.lang.Integer.parseInt
import java.lang.Math.abs
import java.lang.reflect.Type

class ShowListActivity : AppCompatActivity(), ItemAdapter.ActionListenerItem, View.OnClickListener {
    var refBtnItemOk: Button? = null
    var refAreaItem: EditText? = null
    var refProgress: ProgressBar? = null

    /* Gestion des recyclerview */
    private val adapter = newAdapter()

    /* Lien avec API */
    var linkWithAPI = false

    var idList:Int?=null


    private val activityScope = CoroutineScope(
        SupervisorJob()
                + Dispatchers.Main

    )
    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "OnCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_list)

        /* On récupère les views */
        refBtnItemOk = findViewById(R.id.btn_item_OK) //id dans la classe R
        refAreaItem = findViewById(R.id.area_item)
        refProgress = findViewById(R.id.progressbar)

        /* Listeners sur les boutons */
        refBtnItemOk?.setOnClickListener(this)

        /* Listeners sur le champ texte pour les log */
        refAreaItem?.setOnClickListener(this)

        val b = this.intent.extras
        idList = b?.getString("idList")?.let { parseInt(it) } //on ne fait le parseInt que si l'objet n'est pas null

        loadAPIConnexion() //inclut refreshRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        loadAPIConnexion() //inclut refreshRecyclerView()
    }


    /* Création de la fonction pour pouvoir l'appeler à la suite de la création d'une nouvelle liste */
    fun refreshRecyclerView() {
        Log.i(TAG,"fonction refresh du recyclerview appelée")
        /* On trouve la Recycler view */
        val item_view: RecyclerView = findViewById(R.id.item)

        /* Affichage de toutes les listes pour le pseudo actuel */
        val dataSet = mutableListOf<ItemToDo>()

        if (linkWithAPI) { //si on a le lien avc l'API
            var hash = getHashFromPrefs()

            var context = this

            if (hash != "") {
                // avoir les items de l'utilisateur en question
                activityScope.launch {
                    try {
                        //UX on affiche un chargement quand on demande des données à l'API
                        refProgress?.visibility = View.VISIBLE
                        //UX on ne cache pas les items à chaque click, l'app donne l'impression de ramer sinon
//                    item_view.visibility = View.GONE
                        var requete = ""

                        if (idList != null) {
                            requete = "lists/$idList/items"
                        }

                        Log.i(TAG, "requete=" + requete)
                        var items = getItemsFromApi(requete, hash)

                        for (item in items) { //si pas d'items on n'entre juste pas dans la boucle , on n'affichera donc rien
                            dataSet.add(item)
                        }
                        // Pour affichage de la recyclerview /
                        item_view.adapter = adapter
                        item_view.layoutManager =
                            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

                        adapter.showData(dataSet)
                        refProgress?.visibility = View.GONE
//                    item_view.visibility = View.VISIBLE
                    }

                    catch(e:Exception) {
                        Toast.makeText(context,"Cannot load lists from API", Toast.LENGTH_SHORT).show()
                        Log.e(TAG,"Cannot load lists from API")
                    }

                }
            }
        }
        else adapter.showData(dataSet) //sinon on n'affiche rien
    }

//    fun addItemToActiveList(item: ItemToDo) {
//
//        Log.i(TAG, "additemtoactivelist called")
//
//        // On recharge les donées qui ont peut etre changé //
//        // Chargement des profils //
//        //loadPrefs()
//        var profils = getProfils()
//
//        // MAJ du profil du user en question //
//        var user = profils.filter { it.active }[0]
//        var list = user!!.mesListToDo.filter { it.active }[0].listItemsToDo
//
//        for (itemliste in list) {
//            if (itemliste.description == item.description) {
//                Log.i(TAG, "Item non ajouté car déjà présent")
//                Toast.makeText(
//                    this,
//                    "Item non ajouté car déjà présent, veuillez saisir une autre description",
//                    Toast.LENGTH_LONG
//                ).show()
//                return //on sort de la fonction avant de l'ajouter
//            }
//        }
//        list.add(
//            item
//        ) //pour que ça l'ajoute directement ddans la bonne liste du bon user, on repart à chaque fois de profils que l'on sauvegarde
//
//
//        editPrefs(profils, "profils")
//    }

    /* Pour recyclerview */
    private fun newAdapter(): ItemAdapter {

        val adapter = ItemAdapter(
            this
        )
        return adapter
    }


    override fun onItemClicked(itemToDo: ItemToDo) {// on va chercher le bon item et l'enregistrer comme étant coché
        var hash = getHashFromPrefs()

        var context = this

        if (hash!=""){
            // avoir les listes de l'utilisateur en question
            activityScope.launch{
                try{
                    var requete = ""

                    if (idList!=null) {

                        requete = "lists/$idList/items/${itemToDo.id}"
                        Log.i(TAG,"requete="+requete)
                        var item = checkItemFromApi(requete,abs(itemToDo.checked-1), hash) //On inverse la valeur du check
                        Log.i(TAG,"item changed : "+item.toString())


                        refreshRecyclerView() //MAJ de l'affichage
                    }
                }
                catch(e:Exception){
                    Log.e(TAG,e.message) // on reporte l'erreur
                    Toast.makeText(context, "Cannot change item on API", Toast.LENGTH_SHORT).show()
                }



            }
        }
    }

    override fun onClick(v: View) {
        Log.i(TAG,"OnClick ${v.id}") // v is the clicked view
        when (v.id) {
            R.id.btn_item_OK -> {

                var hash = getHashFromPrefs()
                var context = this

                if (hash!=""){
                    // avoir les listes de l'utilisateur en question
                    activityScope.launch{
                        try{
                            var requete = ""

                            if (idList!=null) {

                                var label=refAreaItem?.text.toString()

                                if(label=="")Toast.makeText(context, "Saisissez un titre pour l'item", Toast.LENGTH_SHORT).show() //vérification que l'on a un label, sinon la requête ne fonctionnera pas
                                else {
                                    requete = "lists/$idList/items"
                                    Log.i(TAG, "requete=" + requete)
                                    var item = createItemFromApi(
                                        requete,
                                        label,
                                        hash
                                    ) //On inverse la valeur du check
                                    Log.i(TAG, "item changed : " + item.toString())

                                    refreshRecyclerView() //MAJ de l'affichage
                                }
                            }
                        }
                        catch(e:Exception){
                            Log.e(TAG,e.message) // on reporte l'erreur
                            Toast.makeText(context, "Cannot add item on API", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    /* Gestion des menus */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu,
            menu
        );        // maybe change here to change the name in the menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        return if (id == R.id.settings) {
            Log.i(TAG, "Settings menu option clicked")

            /* Creation de l'intent pour switcher d'activité */
            val intent = Intent(this, SettingsActivity::class.java)

            /* On change l'activité */
            Log.i(TAG, "Starting SettingsActivity")
            startActivity(intent)
            true
        }
        /* Code si besoin d'autres menus */
        else {
            true
        }

    }

    /* Objet TAG pour pouvoir relever les traces d'exécution */
    companion object {
        private const val TAG = "TRACES_ShowList"
    }

    // Gestion des enregistrements dans les préférences de l'app //
    fun loadPrefs(): SharedPreferences {
        Log.i(TAG,"Récupération des données de l'application")
        // récupération des données //
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs
    }

    fun getProfils(): MutableList<ProfilListeToDo> {
        Log.i(TAG,"Récupération des profils")
        // Récupération des profils //
        val gson = Gson()
        val json: String? = loadPrefs().getString("profils", "") //lecture de la valeur

        var profils = mutableListOf<ProfilListeToDo>()

        if (json != "") {

            /* Solution pour enregistrer une liste de pseudos en json */

            val collectionType: Type = object :
                TypeToken<MutableList<ProfilListeToDo>>() {}.type
            profils = gson.fromJson(json, collectionType)
            //val profils: ProfilListeToDo = gson.fromJson<ProfilListeToDo>(json, ProfilListeToDo::class.java)
            Log.i(TAG, "pseudo_list found : " + json)


        } else {
            Log.i(TAG, "pseudo_list NOT found")
        }
        return profils //vide si non trouvé

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
            Log.i(TAG, "connexion with API hash : ${hashtext}") //fonction qui marche ou pas, j'aurais voulu récupérer "version" mais ça ne fonctionne pas
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
        refBtnItemOk?.isEnabled = bool
        refAreaItem?.isEnabled = bool
    }


}
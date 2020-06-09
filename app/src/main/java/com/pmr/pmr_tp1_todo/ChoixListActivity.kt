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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.pmr_tp1_todo.model.ListeToDo
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import fr.ec.app.main.adapter.ListAdapter
import java.lang.reflect.Type


class ChoixListActivity : AppCompatActivity(), ListAdapter.ActionListener,View.OnClickListener  { // action listener pour le recyclerview et onclick pour le bouton normal
    /* Initialisations */
    var refBtnListOk: Button? = null
    var refAreaList: EditText? = null // pour stocker le texte

    /* Gestion des recyclerview */
    private val adapter = newAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(ChoixListActivity.TAG,"OnCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choix_list)

        /* On récupère les views */
        refBtnListOk = findViewById(R.id.btn_list_OK) //id dans la classe R
        refAreaList = findViewById(R.id.area_list)

        /* Listeners sur les boutons */
        /* On pourrait passer ici directement l'action mais on doit enregistrer le texte donc on préférera overrider onclick plus tard */
        refBtnListOk?.setOnClickListener(this)

        /* Listeners sur le champ texte pour les log */
        refAreaList?.setOnClickListener(this)

        /*Affichage de la recyclerView et initialisation user et appel a loadprefs */
        refreshRecyclerView()

    }


    /* Création de al fonction pour pouvoir l'appeler à la suite de la création d'une nouvelle liste */
    fun refreshRecyclerView(){
        Log.i(TAG,"fonction refresh du recyclerview appelée")
        /* On trouve la Recycler view */
        val list: RecyclerView = findViewById(R.id.list)

        /* Affichage de toutes les listes pour le pseudo actuel */
        val dataSet = mutableListOf<ListeToDo>()

        //il vaut mieux recharger le user à chaque fois qu'on appelle refresh pour être à jour sur ses listes et ses items plutot que de ne le charger //
        //cela se passe dans getProfils
        var user = getProfils().filter{it.active}[0]


        if (user!!.mesListToDo.size !=0) { //pas null car on passe à cette activité via la mainactivity
            for (list in user!!.mesListToDo) { //parcours des listes d'un utilisateur
                dataSet.add(list)
            }
        }

        // Pour affichage de la recyclerview //
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)

        adapter.showData(dataSet)

    }

    fun addListToLoggedUser(liste: ListeToDo) {
        Log.i(TAG,"fonction addListToLoggedUser appelée")
        // Chargement des profils //
        var profils = getProfils()

        // MAJ du profil du user en question //
        var user = profils.filter{it.active}[0]
        // MAJ de ses listes
        for (listuser in user!!.mesListToDo){
            if (listuser.titreListeToDo==liste.titreListeToDo){
                Log.i(TAG,"Liste non ajoutée car déjà présente")
                Toast.makeText(this,"Liste non ajoutée car déjà présente, veuillez saisir un autre titre", Toast.LENGTH_LONG).show()
                return //on sort de la fonction avant de l'ajouter
            }
        }
        user!!.mesListToDo.add(liste) ///pour que ça l'ajoute directement dans les profils

        // Sauvegarde des profils //
        editPrefs(profils,"profils")

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
        //Toast.makeText(this,"click enregistré sur ${listeToDo.titreListeToDo}",Toast.LENGTH_LONG).show()

        /* On active la liste cliquée */
        /* On désactive les autres listes */

        // Chargement des profils //
        var profils = getProfils()

        // MAJ du profil du user que l'on utilise //
        var user = profils.filter{it.active}[0]

        if (user!!.mesListToDo.size !=0) {
            user!!.mesListToDo.forEach { it.active = false }//parcours des listes d'un utilisateur pour tout mettre à false

            for (list in user!!.mesListToDo){
                Log.i(TAG, (list==listeToDo).toString()) //aucune idée de pourquoi nous n'avons pas exactement le même objet
                // TODO : détailler dans le doc la mthode mise en place on n'autorisera donc pas 2 listes à avoir le même nom

                if (list.titreListeToDo == listeToDo.titreListeToDo){
                    list.active=true //on ne met que la liste sélectionnée en active, elle sera donc la seule, au moins pour cet utilisateur
                    Log.i(TAG,list.titreListeToDo)
                }
            }
            //user!!.mesListToDo.filter{it == listeToDo}[0].active = true
        }

        /* On réenregistre les données */
        editPrefs(profils,"profils")

        val versShowList = Intent(this, ShowListActivity::class.java)

        /* On change d'activité */
        Log.i(TAG,"Starting ShowListActivity")
        startActivity(versShowList)

    }

    /* Gestion des champs (bouton+textarea)*/
    override fun onClick(v: View) {

        Log.i(TAG,"OnClick ${v.id}") // v is the clicked view

        when (v.id) {

            R.id.btn_list_OK -> {


                /* Contenu champ texte */
                val s = refAreaList?.text.toString()

                /* Éventuellement toast */
                //val t = Toast.makeText(this, "Création de la nouvelle liste", Toast.LENGTH_SHORT) //notif utilisateur
                //t.show()

                /* Création de nouvelle liste */

                var listeToDo = ListeToDo(refAreaList?.text.toString())

                /* Enregistrer dans les préférences (donc dans les listes du user) le nom de la liste */

                addListToLoggedUser(listeToDo)

                /* Afficher le résultat avec notre fonction de refresh */
                refreshRecyclerView()

            }

            R.id.area_list -> {
                /* Log fait avant le when */
            }
        }
    }

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

    fun editPrefs(
        item: MutableList<ProfilListeToDo>,
        name: String
    ) { //fonctionne seulement avec les profils mais on n'a besoin que de cela
        Log.i(TAG,"Enregistrement des profils dans les préférences de l'applicaiton")
        val gson_set = Gson()
        val json_set: String = gson_set.toJson(item) //écriture de la valeur

        var prefs = loadPrefs() //on charge les dernières préférences
        var editor = prefs.edit()
        editor?.putString(name, json_set)
        editor?.apply()
    }

}

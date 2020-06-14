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
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Thread.sleep
import java.lang.reflect.Type
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), View.OnClickListener {

    var refBtnPseudoOk: Button? = null
    var refAreaPseudo: EditText? = null // pour stocker le texte
    var refAreaPassword: EditText? = null

    var hash:String?=null

    private val activityScope = CoroutineScope(
        SupervisorJob()
            + Dispatchers.Main

// Désactivé car on vuet gérer les erreurs d'appel à l'API de façon différente pour chaque coroutine //
            //+ CoroutineExceptionHandler { _,throwable ->
            //Log.e(TAG,"CoroutineExceptionHandler : ${throwable.message}")
            //Toast.makeText(this, "Non registered user", Toast.LENGTH_SHORT).show()
        //activateBtn(false)
        //}
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "OnCreate")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* On récupère les views */
        refBtnPseudoOk = findViewById(R.id.btn_pseudo_OK) //id dans la classe R
        refAreaPseudo = findViewById(R.id.area_pseudo)
        refAreaPassword = findViewById(R.id.area_password)

        /* Listeners sur les boutons */
        /* On pourrait passer ici directement l'action mais on doit enregistrer le texte donc on préférera overrider onclick plus tard */
        refBtnPseudoOk?.setOnClickListener(this)

        /* Listeners sur le champ texte pour les log */
        refAreaPseudo?.setOnClickListener(this)

        // Vérification d'accès au réseau //
        // Activation/Désactivation du bouton OK en conséquence //
        loadAPIConnexion()

    }
    override fun onDestroy() {
        activityScope.cancel()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        // On fait attention si l'utilisateur à changé l'URL

        // Vérification d'accès au réseau //
        loadAPIConnexion()
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
            }
            catch(e:Exception) {
                Toast.makeText(context, "Cannot load API Connexion", Toast.LENGTH_SHORT).show()
                activateBtn(false)
            }

        }
    }

    private fun activateBtn(bool:Boolean){
        //Pour activation du bouton ou non
        refBtnPseudoOk?.isEnabled = bool
    }

    /* Gestion du bouton pour aller à l'activité ChoixListActivity */

    override fun onClick(v: View) {

        Log.i(TAG, "OnClick ${v.id}") // v est la vue cluquée
        var context = this //pour erreur reporting
        when (v.id) {

            R.id.btn_pseudo_OK -> {

                activityScope.launch{
                    try {
                        // Vérification d'accès au réseau //
                        //vérification de la présence du hash par exemple//
                        var pseudo = refAreaPseudo?.text.toString()
                        var password = refAreaPassword?.text.toString()

                        hash = DataProvider.getHashFromApi(
                            pseudo,
                            password
                        ) //si on n'a pas l'user la coroutine va s'arrêter et userHash ne sera pas initialisé, c'est notre façon de vérifier que le user existe
                        Log.i(
                            TAG,
                            "connexion of user ${pseudo} : ${hash}"
                        ) //fonction qui marche ou pas, j'aurais voulu récupérer "version" mais ça ne fonctionne pas

                        if (hash != null) {

                            editHash(
                                hash!!,
                                pseudo
                            ) //stockage dans les préférences de l'app, avec le last peudo

                            // Passage à l'activité suivante si on a vérifié les id
                            val versChoixList =
                                Intent(this@MainActivity, ChoixListActivity::class.java)
                            /* On change d'activité */
                            Log.i(TAG, "Starting ChoixListActivity")
                            startActivity(versChoixList)
                        }
                    }
                    catch(e:Exception){
                        Log.e(TAG,e.message) // on reporte l'erreur
                        Toast.makeText(context, "Bad id or password, please try again", Toast.LENGTH_SHORT).show() //UX : on informe l'utilisateur que la requête avec les pseudos et mdp fournis ne fonctionne pas
                    }
                }
            }

            // Lors du click sur le texte, on sélectionne le champ et on préremplis avec le dernier utilisateur //
            R.id.area_pseudo -> {
                var lastUser = getLastUserFromPrefs()
                if (lastUser != "") refAreaPseudo?.setText(lastUser) //si on a déjà renseigné un login, c'est l'utilisateur loggé, on remplis donc le texte losr du click
                refAreaPseudo?.selectAll() //pour UX, on sélectionne le champ
            }
        }
    }


    /* Gestion des menus */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu,
            menu
        );
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
        private const val TAG = "TRACES_MAIN"
    }

    // Gestion des enregistrements dans les préférences de l'app //
    fun loadPrefs(): SharedPreferences {
        Log.i(TAG,"Récupération des données de l'application")
        // récupération des données //
        var prefs = PreferenceManager.getDefaultSharedPreferences(this)
        return prefs
    }

    fun getLastUserFromPrefs(): String {
        Log.i(TAG, "Récupération des profils")
        // Récupération des profils //
        val gson = Gson()
        val json: String? = loadPrefs().getString("lastUser", "") //lecture de la valeur

        var lastUser = ""

        if (json != "") {

            val collectionType: Type = object : //surement pas nécessaire
                TypeToken<String>() {}.type

            lastUser = gson.fromJson(json, collectionType)

        } else {
            Log.i(TAG, "lastUser NOT found")
        }
        return lastUser //vide si non trouvé
    }

    fun editHash(
        hash: String,lastUser:String
    ) { //fonctionne seulement avec les profils mais on n'a besoin que de cela
        Log.i(TAG,"Enregistrement des profils dans les préférences de l'applicaiton")
        val gson_set = Gson()
        val json_set: String = gson_set.toJson(hash) //écriture de la valeur
        val json_set2: String = gson_set.toJson(lastUser)

        var prefs = loadPrefs() //on charge les dernières préférences
        var editor = prefs.edit()
        editor?.putString("hash", json_set)
        editor?.putString("lastUser", json_set2)
        editor?.apply()
    }

}


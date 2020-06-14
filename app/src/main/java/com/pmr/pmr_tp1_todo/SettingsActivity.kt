package com.pmr.pmr_tp1_todo

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceActivity
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pmr.pmr_tp1_todo.model.ListeToDo
import com.pmr.pmr_tp1_todo.model.ProfilListeToDo
import java.lang.reflect.Type

class SettingsActivity : AppCompatActivity(), View.OnClickListener {

    var ref_txt_pseudo:TextView? = null;
    var ref_txt_url: EditText? = null;
    var ref_btn_save: Button? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        ref_txt_pseudo = findViewById(R.id.txt_pseudo_print_settings)
        ref_txt_url = findViewById(R.id.txt_url_settings_edit)
        ref_btn_save = findViewById(R.id.btn_sauvegarder)

        ref_btn_save?.setOnClickListener(this)

        // Chargement de l'URL de base //
        ref_txt_url?.setText(DataProvider.BASE_URL)

        // Récupération du pseudo //
        /* Chargement via les données stockées dans les préférences */
        var profils = getProfils()
        if (profils.isEmpty()){
            ref_txt_pseudo?.text = "Pas d'utilisateur connecté pour l'instant"
        }
        else {
            var user =
                getProfils().filter { it.active }[0] // on n'a toujours qu'un utilisateur actif
            ref_txt_pseudo?.text = user.login
        }

    }
    companion object {
        private const val TAG = "TRACES_SETTINGS"
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

    /* Gestion du bouton sauvegarder */

    override fun onClick(v: View) {

        Log.i(TAG, "OnClick ${v.id}") // v is the clicked view

        when (v.id) {

            R.id.btn_sauvegarder -> {

                /* Contenu champ texte */

                val contenu = ref_txt_url?.text.toString()
                DataProvider.BASE_URL=contenu
                DataProvider.reStartService()
                Toast.makeText(this, "Base URL Changed to $contenu", Toast.LENGTH_SHORT).show()
                //editBASEURL(contenu) // On sauvegarde directement dans la classe l'url à utiliser
                // Passage à l'activité suivante si on a vérifieé les id
//                val versMainActivity = Intent(this, MainActivity::class.java)
                /* On change d'activité */
//                Log.i(TAG, "Starting MainActivity")
//                startActivity(versMainActivity)

            }


        }
    }
//    fun editBASEURL(
//        baseURL:String
//    ) { //fonctionne seulement avec les profils mais on n'a besoin que de cela
//        Log.i(TAG,"Enregistrement des profils dans les préférences de l'applicaiton")
//        val gson_set = Gson()
//        val json_set: String = gson_set.toJson(baseURL) //écriture de la valeur
//
//
//        var prefs = loadPrefs() //on charge les dernières préférences
//        var editor = prefs.edit()
//        editor?.putString("baseURL", json_set)
//        editor?.apply()
//    }
//    fun getBaseURLFromPrefs(): String {
//        Log.i(TAG, "Récupération du hash dans les préférences")
//        // Récupération des profils //
//        val gson = Gson()
//        val json: String? = loadPrefs().getString("baseURL", "") //lecture de la valeur
//
//        var base = ""
//
//        if (json != "") {
//
//            val collectionType: Type = object : //surement pas nécessaire
//                TypeToken<String>() {}.type
//
//            base = gson.fromJson(json, collectionType)
//
//        } else {
//            Log.i(TAG, "baseURL NOT found")
//            base="http://10.0.2.2:8888/todo-api/"
//        }
//        return base //vide si non trouvé
//    }




}

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
        var profils = getLastUserFromPrefs() //On ne charge plus tout à fait l'utilisateur connecté, mais le dernier pseudo entré comme cette notion n'est plus intégrée dans notre bdd qui est maintenant l'API
        if (profils.isEmpty()){
            ref_txt_pseudo?.text = "Pas d'utilisateur connecté pour l'instant"
        }
        else {
            ref_txt_pseudo?.text = profils
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
            }
        }
    }
    fun getLastUserFromPrefs(): String {
        Log.i(TAG, "Récupération des profils")
        // Récupération des profils //
        val gson = Gson()
        val json: String? = loadPrefs().getString("lastUser", "") //lecture de la valeur

        var lastUser = ""

        if (json != "") {

            val collectionType: Type = object : //on prend l'objet type
                TypeToken<String>() {}.type

            lastUser = gson.fromJson(json, collectionType) // on prend le dernier user entré en deserialisant du json en string

        } else {
            Log.i(TAG, "lastUser NOT found")
        }
        return lastUser //vide si non trouvé
    }

}

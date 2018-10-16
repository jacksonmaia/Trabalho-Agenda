package br.ufc.quixada.dadm.variastelas;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufc.quixada.dadm.variastelas.network.DownloadContatos;
import br.ufc.quixada.dadm.variastelas.transactions.Agenda;
import br.ufc.quixada.dadm.variastelas.transactions.Constants;
import br.ufc.quixada.dadm.variastelas.transactions.Contato;

public class MainActivity extends AppCompatActivity {

    int selected;
    ArrayList<Contato> listaContatos;
    ArrayAdapter adapter;
    ListView listViewContatos;

    ProgressBar progressBar;

    DownloadContatos downloadContatos;

    DatabaseReference dados;
    Firebase myFirebase;

    private String personId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dados = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(this);
        myFirebase = new Firebase("https://agenda-676b4.firebaseio.com/");
        personId = dados.push().getKey();




        selected = -1;

        listaContatos = new ArrayList<Contato>();

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listaContatos );

        listViewContatos = ( ListView )findViewById( R.id.listViewContatos );
        listViewContatos.setAdapter( adapter );
        listViewContatos.setSelector( android.R.color.holo_blue_light );

        listViewContatos.setOnItemClickListener( new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Toast.makeText(MainActivity.this, "" + listaContatos.get( position ).toString(), Toast.LENGTH_SHORT).show();
                selected = position;
            }
        } );

        progressBar = findViewById( R.id.progressBar );
        progressBar.setIndeterminate( true );
        progressBar.setVisibility( View.VISIBLE );

        //Baixar a lista de contatos do servidor
        downloadContatos = new DownloadContatos( this, listViewContatos );
        downloadContatos.start();

        readData();

    }

    private void readData(){
        final ArrayList<Contato> contatos = new ArrayList<>();
        dados.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                while((iterator.hasNext())){

                    Contato value = iterator.next().getValue(Contato.class);
                    boolean contains = false;
                    for(Contato c : listaContatos) {
                        if (c.getKey() == value.getKey()) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        listaContatos.add(value);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateListaContatos( Agenda agenda ){
        progressBar.setVisibility( View.INVISIBLE );

        Contato[] lista = agenda.getListaTelefone();
        for( Contato c: lista ) listaContatos.add( c );

        adapter.notifyDataSetChanged();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.menu_main_activity, menu );
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected( MenuItem item ) {

        switch(item.getItemId())
        {
            case R.id.add:
                clicarAdicionar();
                break;
            case R.id.edit:
                clicarEditar();
                break;
            case R.id.delete:
                apagarItemLista();
                break;
            case R.id.settings:
                break;
            case R.id.about:
                break;
        }
        return true;
    }

    private void apagarItemLista(){
        if( listaContatos.size() > 0 ){
            Contato contato = listaContatos.get(selected);
            dados.child(contato.getKey()).removeValue();
            listaContatos.remove(contato);
            adapter.notifyDataSetChanged();
        } else {
            selected = -1;
        }

    }

    public void clicarAdicionar(){
        Intent intent = new Intent( this, ContactActivity.class );
        startActivityForResult( intent, Constants.REQUEST_ADD );
    }

    public void clicarEditar(){

        Intent intent = new Intent( this, ContactActivity.class );
        //Intent intent2 = new Intent( this, "br.ufc.quixada.dadm.variastelas.ContactActivity" );

        Contato contato = listaContatos.get( selected );

        intent.putExtra( "key", contato.getKey() );
        intent.putExtra( "nome", contato.getNome() );
        intent.putExtra( "telefone", contato.getTelefone() );
        intent.putExtra( "endereco", contato.getEndereco() );

        startActivityForResult( intent, Constants.REQUEST_EDIT );
    }


    @Override
    protected void onActivityResult( int requestCode, int resultCode, @Nullable Intent data ) {
        super.onActivityResult(requestCode, resultCode, data);

      if( requestCode == Constants.REQUEST_ADD && resultCode == Constants.RESULT_ADD ){

          String nome = ( String )data.getExtras().get( "nome" );
          String telefone = ( String )data.getExtras().get( "telefone" );
          String endereco = ( String )data.getExtras().get( "endereco" );

          String key = dados.push().getKey();
          Contato contato = new Contato( nome, telefone, endereco );
          contato.setKey(key);
          dados.child(key).setValue(contato);

      } else if( requestCode == Constants.REQUEST_EDIT && resultCode == Constants.RESULT_ADD ){

          String nome = ( String )data.getExtras().get( "nome" );
          String telefone = ( String )data.getExtras().get( "telefone" );
          String endereco = ( String )data.getExtras().get( "endereco" );
          String idEditar = ( String )data.getExtras().get( "key" );
          Contato novo = new Contato(nome, telefone, endereco);
          novo.setKey(idEditar);

          dados.child(idEditar).setValue(novo);

          for( Contato contato: listaContatos ){
              if( contato.getKey().equals(idEditar)){
                  listaContatos.remove(contato);
                  break;
              }
          }
          listaContatos.add(novo);

          adapter.notifyDataSetChanged();

      } //Retorno da tela de contatos com um conteudo para ser adicionado
        //Na segunda tela, o usuario clicou no botão ADD
      else if( resultCode == Constants.RESULT_CANCEL ){
            Toast.makeText( this,"Cancelado",
                    Toast.LENGTH_SHORT).show();
        }

    }








}

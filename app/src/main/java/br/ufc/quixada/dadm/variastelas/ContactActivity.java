package br.ufc.quixada.dadm.variastelas;

import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import br.ufc.quixada.dadm.variastelas.transactions.Constants;

public class ContactActivity extends AppCompatActivity {

    EditText edtNome;
    EditText edtTel;
    EditText edtEnd;

    boolean edit;
    String idContatoEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        edtNome = findViewById( R.id.editTextNome );
        edtTel = findViewById( R.id.editTextTel );
        edtEnd = findViewById( R.id.editTextEnd );

        edit = false;

        if( getIntent().getExtras() != null ){

            String nome = ( String )getIntent().getExtras().get( "nome" );
            String telefone = ( String )getIntent().getExtras().get( "telefone" );
            String endereco = ( String )getIntent().getExtras().get( "endereco" );
            idContatoEditar = (String)getIntent().getExtras().get( "key" );

            edtNome.setText( nome );
            edtTel.setText( telefone );
            edtEnd.setText( endereco );

            edit = true;

        }

    }

    public void cancelar( View view ){
        setResult( Constants.RESULT_CANCEL );
        finish();
    }

    public void adicionar( View view ){

        Intent intent = new Intent();

        String nome = edtNome.getText().toString();
        String telefone = edtTel.getText().toString();
        String endereco = edtEnd.getText().toString();

        intent.putExtra( "nome", nome );
        intent.putExtra( "telefone", telefone );
        intent.putExtra( "endereco", endereco );

        if( edit ) intent.putExtra( "key", idContatoEditar );

        setResult( Constants.RESULT_ADD, intent );
        finish();
    }
}

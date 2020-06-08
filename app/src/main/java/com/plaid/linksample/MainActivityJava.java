/*
 * Copyright (c) 2020 Plaid Technologies, Inc. <support@plaid.com>
 */

package com.plaid.linksample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plaid.link.Plaid;
import com.plaid.linkbase.models.configuration.LinkConfiguration;
import com.plaid.linkbase.models.configuration.PlaidProduct;
import com.plaid.linkbase.models.connection.LinkConnection;
import com.plaid.linkbase.models.connection.PlaidLinkResultHandler;

import kotlin.Unit;

import java.util.ArrayList;

public class MainActivityJava extends AppCompatActivity {

  private static final int LINK_REQUEST_CODE = 1;
  private TextView result;
  private TextView tokenResult;

  private PlaidLinkResultHandler myPlaidResultHandler = new PlaidLinkResultHandler(
      LINK_REQUEST_CODE,
      linkConnection -> {
        LinkConnection.LinkConnectionMetadata metadata = linkConnection.getLinkConnectionMetadata();
        result.setText(getString(
            R.string.content_success));
        tokenResult.setText(getString(
            R.string.public_token_result,
            linkConnection.getPublicToken()));
        return Unit.INSTANCE;
      },
      linkCancellation -> {
        tokenResult.setText("");

        result.setText(getString(
            R.string.content_cancelled,
            linkCancellation.getInstitutionId(),
            linkCancellation.getInstitutionName(),
            linkCancellation.getLinkSessionId(),
            linkCancellation.getStatus()));
        return Unit.INSTANCE;
      },
      plaidApiError -> {
        tokenResult.setText("");
        result.setText(getString(
            R.string.content_exit,
            plaidApiError.getDisplayMessage(),
            plaidApiError.getErrorCode(),
            plaidApiError.getErrorMessage(),
            plaidApiError.getLinkExitMetadata().getInstitutionId(),
            plaidApiError.getLinkExitMetadata().getInstitutionName(),
            plaidApiError.getLinkExitMetadata().getStatus()));
        return Unit.INSTANCE;
      }
  );

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    result = findViewById(R.id.result);
    tokenResult = findViewById(R.id.token_result);

    View button = findViewById(R.id.open_link);
    button.setOnClickListener(view -> {
      setOptionalEventListener();
      openLink();
    });
  }

  /**
   * Optional, set an <a href="https://plaid.com/docs/link/android/#handling-onevent">event listener</a>.
   */
  private void setOptionalEventListener() {
    Plaid.setLinkEventListener(linkEvent -> {
      Log.i("Event", linkEvent.toString());
      return Unit.INSTANCE;
    });
  }

  /**
   * For all Link configuration options, have a look at the
   * <a href="https://plaid.com/docs/link/android/#parameter-reference">parameter reference</>
   */
  private void openLink() {
    ArrayList<PlaidProduct> products = new ArrayList<>();
    products.add(PlaidProduct.TRANSACTIONS);
    Plaid.openLink(
        MainActivityJava.this,
        new LinkConfiguration.Builder("Link demo", products).build(),
        LINK_REQUEST_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (!myPlaidResultHandler.onActivityResult(requestCode, resultCode, data)) {
      Log.i(MainActivityJava.class.getSimpleName(), "Not handled");
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.menu_java, menu);
    return true;
  }

  @SuppressWarnings("SwitchStatementWithTooFewBranches")
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.show_kotlin:
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}

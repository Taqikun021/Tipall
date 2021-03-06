// Generated by view binder compiler. Do not edit!
package xyz.tqydn.tipall.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.tqydn.tipall.R;

public final class AlertErrorBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final ImageView iv;

  @NonNull
  public final TextView tv;

  private AlertErrorBinding(@NonNull CardView rootView, @NonNull ImageView iv,
      @NonNull TextView tv) {
    this.rootView = rootView;
    this.iv = iv;
    this.tv = tv;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static AlertErrorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static AlertErrorBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.alert_error, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static AlertErrorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv;
      ImageView iv = rootView.findViewById(id);
      if (iv == null) {
        break missingId;
      }

      id = R.id.tv;
      TextView tv = rootView.findViewById(id);
      if (tv == null) {
        break missingId;
      }

      return new AlertErrorBinding((CardView) rootView, iv, tv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

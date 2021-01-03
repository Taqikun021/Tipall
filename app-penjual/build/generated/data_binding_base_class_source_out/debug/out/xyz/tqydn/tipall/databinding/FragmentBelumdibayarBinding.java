// Generated by view binder compiler. Do not edit!
package xyz.tqydn.tipall.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;
import xyz.tqydn.tipall.R;

public final class FragmentBelumdibayarBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final ImageView iv;

  @NonNull
  public final ConstraintLayout kosong;

  @NonNull
  public final RecyclerView rv;

  @NonNull
  public final TextView tv;

  private FragmentBelumdibayarBinding(@NonNull FrameLayout rootView, @NonNull ImageView iv,
      @NonNull ConstraintLayout kosong, @NonNull RecyclerView rv, @NonNull TextView tv) {
    this.rootView = rootView;
    this.iv = iv;
    this.kosong = kosong;
    this.rv = rv;
    this.tv = tv;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentBelumdibayarBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentBelumdibayarBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_belumdibayar, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentBelumdibayarBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv;
      ImageView iv = rootView.findViewById(id);
      if (iv == null) {
        break missingId;
      }

      id = R.id.kosong;
      ConstraintLayout kosong = rootView.findViewById(id);
      if (kosong == null) {
        break missingId;
      }

      id = R.id.rv;
      RecyclerView rv = rootView.findViewById(id);
      if (rv == null) {
        break missingId;
      }

      id = R.id.tv;
      TextView tv = rootView.findViewById(id);
      if (tv == null) {
        break missingId;
      }

      return new FragmentBelumdibayarBinding((FrameLayout) rootView, iv, kosong, rv, tv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

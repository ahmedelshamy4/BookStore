package com.eslamelhoseiny.bookstore.util;

import android.content.Context;
import android.content.Intent;

import com.eslamelhoseiny.bookstore.LoginActivity;
import com.eslamelhoseiny.bookstore.MyBooksActivity;
import com.eslamelhoseiny.bookstore.RegisterActivity;

/**
 * Created by Eslam Elhoseiny on 9/29/2017.
 */

public final class ActivityLauncher {
// all intent in the bookStore here........
    public static void openLoginActivity(Context context){
        Intent i = new Intent(context, LoginActivity.class);
        context.startActivity(i);
    }
    public static void openRegistrationActivity(Context context){
        Intent i = new Intent(context, RegisterActivity.class);
        context.startActivity(i);
    }
    public static void openMyBooksActivity(Context context){
        Intent i = new Intent(context, MyBooksActivity.class);
        context.startActivity(i);
    }

}

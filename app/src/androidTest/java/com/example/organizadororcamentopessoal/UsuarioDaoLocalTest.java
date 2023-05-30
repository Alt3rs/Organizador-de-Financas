package com.example.organizadororcamentopessoal;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.organizadororcamentopessoal.datasource.UserDao;
import com.organizadororcamentopessoal.datasource.UserDaoLocal;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UsuarioDaoLocalTest {

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();

    }

    @Test
    public void useAppContext() {
        Context appContext = ApplicationProvider.getApplicationContext();
        assertEquals("com.example.organizadororcamentopessoal", appContext.getPackageName());
    }
}
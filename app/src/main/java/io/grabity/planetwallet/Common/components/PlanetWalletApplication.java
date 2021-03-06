package io.grabity.planetwallet.Common.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import io.grabity.planetwallet.MiniFramework.managers.FontManager;
import io.grabity.planetwallet.MiniFramework.utils.SetTokenImageDownloader;


/**
 * Created by. JcobPark on 2018. 08. 29
 */
@SuppressLint( "Registered" )
public class PlanetWalletApplication extends MultiDexApplication {

    @Override
    public void onCreate( ) {
        super.onCreate( );
        initLoader( );
    }

    protected void attachBaseContext( Context base ) {
        super.attachBaseContext( base );
        MultiDex.install( this );
    }

    public void initLoader( ) {

        if ( ImageLoader.getInstance( ).isInited( ) ) ImageLoader.getInstance( ).destroy( );
        FontManager.Init( this );
        DisplayImageOptions options = new DisplayImageOptions.Builder( )
                .cacheInMemory( true )
                .cacheOnDisk( true )
                .build( );

        ImageLoaderConfiguration config = new
                ImageLoaderConfiguration.Builder( getApplicationContext( ) )
                .threadPoolSize( 5 )
                .defaultDisplayImageOptions( options )
                .threadPriority( Thread.MIN_PRIORITY + 3 )
                .denyCacheImageMultipleSizesInMemory( )
                .diskCacheFileNameGenerator( new Md5FileNameGenerator( ) )
                .memoryCache( new LruMemoryCache( 2 * 1024 * 1024 ) )
                .memoryCacheSize( 2 * 1024 * 1024 )
                .imageDownloader( new SetTokenImageDownloader( this ) )
                .build( );
        ImageLoader.getInstance( ).init( config );
//        DataBaseManager.init( this );
    }


}



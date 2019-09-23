package io.grabity.planetwallet.Views.p7_Setting.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

import io.grabity.planetwallet.Common.commonset.C;
import io.grabity.planetwallet.Common.components.AbsPopupView.PopupView;
import io.grabity.planetwallet.Common.components.PlanetWalletActivity;
import io.grabity.planetwallet.MiniFramework.networktask.Get;
import io.grabity.planetwallet.MiniFramework.utils.Route;
import io.grabity.planetwallet.MiniFramework.utils.Utils;
import io.grabity.planetwallet.MiniFramework.wallet.store.PlanetStore;
import io.grabity.planetwallet.R;
import io.grabity.planetwallet.VO.Board;
import io.grabity.planetwallet.VO.Planet;
import io.grabity.planetwallet.VO.ReturnVO;
import io.grabity.planetwallet.VO.Version;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Board.BoardActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Board.ServiceActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.DetailPlanetActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Planet.PlanetManagementActivity;
import io.grabity.planetwallet.Views.p7_Setting.Activity.Setting.SecurityActivity;
import io.grabity.planetwallet.Views.p7_Setting.Adapter.PopupCurrencyAdapter;
import io.grabity.planetwallet.Widgets.AdvanceRecyclerView.AdvanceArrayAdapter;
import io.grabity.planetwallet.Widgets.CircleImageView;
import io.grabity.planetwallet.Widgets.ListPopupView.ListPopup;
import io.grabity.planetwallet.Widgets.PlanetView;
import io.grabity.planetwallet.Widgets.RoundButton.RoundButton;
import io.grabity.planetwallet.Widgets.ToolBar;


public class SettingActivity extends PlanetWalletActivity implements ToolBar.OnToolBarClickListener, ListPopup.OnListPopupItemClickListener {

    private ViewMapper viewMapper;

    private AdvanceArrayAdapter adapter;
    private ArrayList< String > items;

    private Planet planet;
    private Board board;

    @Override
    protected void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_setting, true );
        viewMapper = new ViewMapper( );
        viewInit( );
    }

    @Override
    protected void onResume( ) {
        super.onResume( );
        setData( );
    }

    @Override
    protected void setData( ) {
        super.setData( );
        if ( getSerialize( C.bundleKey.PLANET ) == null ) {
            finish( );
        } else {
            board = new Board( );

            planet = ( Planet ) getSerialize( C.bundleKey.PLANET );
            planet = PlanetStore.getInstance( ).getPlanet( planet.getKeyId( ) );
            viewMapper.textName.setText( localized( R.string.setting_planet_main_title, planet.getName( ) ) );
            viewMapper.planetView.setData( planet.getAddress( ) );
            viewMapper.textVersion.setText( Utils.getAppVersion( this ) );

            viewMapper.imageVersionUpdate.setVisibility( compareVersion( getPlanetWalletApplication( ).getRecentVersion( ) ) ? View.VISIBLE : View.GONE );


            new Get( this ).action( Route.URL( "version", "android" ), 0, 0, null );
        }

        items = new ArrayList<>( Arrays.asList( C.currency.USD, C.currency.KRW, C.currency.CNY ) );
        adapter = new PopupCurrencyAdapter( this, items );

    }

    @Override
    protected void viewInit( ) {
        super.viewInit( );

        //next version coinMarketCap update
        viewMapper.btnCurrency.setVisibility( View.GONE );

        viewMapper.toolBar.setLeftButton( ToolBar.ButtonItem( ).setTag( C.tag.TOOLBAR_CLOSE ) );
        viewMapper.toolBar.setOnToolBarClickListener( this );

        viewMapper.btnPlanet.setOnClickListener( this );
        viewMapper.btnUniverse.setOnClickListener( this );
        viewMapper.btnSecurity.setOnClickListener( this );
        viewMapper.btnAnnouncements.setOnClickListener( this );
        viewMapper.btnFaq.setOnClickListener( this );
        viewMapper.btnThemeWhite.setOnClickListener( this );
        viewMapper.btnThemeBlack.setOnClickListener( this );
        viewMapper.btnCurrency.setOnClickListener( this );
        viewMapper.btnVersion.setOnClickListener( this );
        viewMapper.textVersion.setText( Utils.getAppVersion( this ) );

        btnThemeSetting( );
    }

    void btnThemeSetting( ) {
        viewMapper.btnThemeBlack.setBorderColorNormal( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#FF0050" ) : Color.parseColor( "#5C5964" ) );
        viewMapper.btnThemeWhite.setBorderColorNormal( !getPlanetWalletApplication( ).getCurrentTheme( ) ? Color.parseColor( "#BCBDD5" ) : Color.parseColor( "#FF0050" ) );
    }

    @Override
    public void onReceive( boolean error, int requestCode, int resultCode, int statusCode, String result ) {
        super.onReceive( error, requestCode, resultCode, statusCode, result );
        if ( !error ) {
            if ( statusCode == 200 && requestCode == 0 ) {
                ReturnVO returnVO = Utils.jsonToVO( result, ReturnVO.class, Version.class );
                if ( returnVO.isSuccess( ) ) {
                    Version version = ( Version ) returnVO.getResult( );
                    viewMapper.imageVersionUpdate.setVisibility( compareVersion( version.getVersion( ) ) ? View.VISIBLE : View.GONE );
                    getPlanetWalletApplication( ).setRecentVersion( version.getVersion( ) );
                    getPlanetWalletApplication( ).setPlayStoreUrl( version.getUrl( ) );

                }
            }
        }
    }

    @Override
    public void onClick( View v ) {
        super.onClick( v );
        if ( v == viewMapper.btnPlanet ) {
            sendAction( DetailPlanetActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        } else if ( v == viewMapper.btnUniverse ) {
            sendAction( PlanetManagementActivity.class, Utils.createSerializableBundle( C.bundleKey.PLANET, planet ) );
        } else if ( v == viewMapper.btnSecurity ) {
            sendAction( SecurityActivity.class );
        } else if ( v == viewMapper.btnAnnouncements ) {
            board.setType( C.boardCategory.CATEGORY_ANNOUNCEMENTS );
            sendAction( BoardActivity.class, Utils.createSerializableBundle( C.bundleKey.BOARD, board ) );
        } else if ( v == viewMapper.btnFaq ) {
            sendAction( ServiceActivity.class );
        } else if ( v == viewMapper.btnThemeBlack ) {

            getPlanetWalletApplication( ).setTheme( false );
            setTheme( false );

            btnThemeSetting( );

        } else if ( v == viewMapper.btnThemeWhite ) {

            getPlanetWalletApplication( ).setTheme( true );
            setTheme( true );

            btnThemeSetting( );

        } else if ( v == viewMapper.btnCurrency ) {

            ListPopup.newInstance( this )
                    .setAdapter( adapter )
                    .setOnListPopupItemClickListener( this )
                    .show( );
        } else if ( v == viewMapper.btnVersion ) {
            if ( viewMapper.imageVersionUpdate.getVisibility( ) == View.VISIBLE ) {
                sendActionUri( getPlanetWalletApplication( ).getPlayStoreUrl( ) );
            }

        }

    }

    @Override
    public void onToolBarClick( Object tag, View view ) {
        if ( Utils.equals( tag, C.tag.TOOLBAR_CLOSE ) ) {
            super.onBackPressed( );
        }
    }

    private boolean compareVersion( String version ) {
        try {
            Double localVersion = Double.parseDouble( Utils.getAppVersion( this ) );
            Double recentVersion = Double.parseDouble( version );
            return localVersion < recentVersion;

        } catch ( NumberFormatException e ) {
            e.printStackTrace( );
        }
        return false;
    }


    @Override
    public void onListPopupItemClick( PopupView popup, View view, int position ) {
        viewMapper.textCurrency.setText( items.get( position ) );
        super.onBackPressed( );
    }

    public class ViewMapper {

        ToolBar toolBar;

        TextView textName;
        TextView textCurrency;
        TextView textVersion;

        ViewGroup btnPlanet;
        ViewGroup btnUniverse;
        ViewGroup btnSecurity;
        ViewGroup btnAnnouncements;
        ViewGroup btnFaq;
        ViewGroup btnCurrency;
        ViewGroup btnVersion;


        RoundButton btnThemeWhite;
        RoundButton btnThemeBlack;

        PlanetView planetView;

        CircleImageView imageVersionUpdate;

        public ViewMapper( ) {
            toolBar = findViewById( R.id.toolBar );
            textName = findViewById( R.id.text_setting_name );
            btnPlanet = findViewById( R.id.group_setting_planet );
            btnUniverse = findViewById( R.id.group_setting_universe );
            btnSecurity = findViewById( R.id.group_setting_security );
            btnAnnouncements = findViewById( R.id.group_setting_announcements );
            btnFaq = findViewById( R.id.group_setting_faq );
            btnVersion = findViewById( R.id.group_setting_version );

            btnThemeBlack = findViewById( R.id.btn_setting_theme_black );
            btnThemeWhite = findViewById( R.id.btn_setting_theme_white );

            textCurrency = findViewById( R.id.text_setting_currency );
            btnCurrency = findViewById( R.id.group_setting_currency );

            planetView = findViewById( R.id.planet_setting_planetview );

            textVersion = findViewById( R.id.text_setting_version );

            imageVersionUpdate = findViewById( R.id.image_setting_version_update );


        }

    }
}